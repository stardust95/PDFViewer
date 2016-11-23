package zju.homework.pdfviewer.Activitiy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;

import zju.homework.pdfviewer.Java.Account;
import zju.homework.pdfviewer.Java.Group;
import zju.homework.pdfviewer.Java.HistoryManager;
import zju.homework.pdfviewer.R;
import zju.homework.pdfviewer.Tasks.CreateGroupTask;
import zju.homework.pdfviewer.Tasks.DownloadDocumentTask;
import zju.homework.pdfviewer.Tasks.JoinGroupTask;
import zju.homework.pdfviewer.Tasks.QuitGroupTask;
import zju.homework.pdfviewer.Utils.ActivityCollector;
import zju.homework.pdfviewer.Utils.DrawerArrowDrawable;
import zju.homework.pdfviewer.Utils.Util;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = MainActivity.class.getName();
    private Account mAccount;   //用户

    private ListView mListView;
    private TextView mProgressText;
    private View mProgressView;

    public static final String SHAREDPREFFILE = "temp";
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";

    private HistoryManager mHistoryManager = new HistoryManager();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Util.setCacheDir(this.getCacheDir());

        mListView = (ListView)findViewById(R.id.menu_list_view);
        mProgressView = (View)findViewById(R.id.main_progress);
        mProgressText = (TextView)findViewById(R.id.progress_text);
        //设置左上角图标绘制
        setDrawerArrow();

        //设置侧滑菜单
        setMenuList();

        //设置主界面的历史列表
        setHistoryList();

        //设置打开新文件的按钮
        setButton();

        ActivityCollector.addActivity(this);

        Util.userLogin(MainActivity.this);

//        testJoinGroup();
//        Util.showOpenFileDialog(MainActivity.this);
//        Util.userLogin(MainActivity.this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("test", "destroy begin");
        mHistoryManager.saveHistories(MainActivity.this);
        Log.d("test", "destroy end");

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressText.setText(message);
            mHistoryListView.setVisibility(show ? View.GONE : View.VISIBLE);
            mHistoryListView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mHistoryListView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mHistoryListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    //与侧滑菜单有关的变量和函数
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;

    private void setDrawerArrow() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ImageView imageView = (ImageView) findViewById(R.id.drawer_indicator);
        final Resources resources = getResources();

        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.light_gray));
        imageView.setImageDrawable(drawerArrowDrawable);

        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;

                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995) {
                    flipped = true;
                    drawerArrowDrawable.setFlip(flipped);
                } else if (slideOffset <= .005) {
                    flipped = false;
                    drawerArrowDrawable.setFlip(flipped);
                }

                drawerArrowDrawable.setParameter(offset);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
    }


    private String[] mDatas = {
            "Create Group",
            "Join Group",
            "Quit Group",
            "About Us",
            "Exit"};

    private void setMenuList() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                MainActivity.this,
                R.layout.text_item,
                mDatas);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //创建组
                if(position == 0) {
                    if( mAccount.hasGroup() ) {
                        showDialogWithText("You have had a group!");
                    }
                    else {
                        Util.showOpenFileDialog(MainActivity.this, Util.REQUEST_CREATE_GROUP);     // 先选择需要协作编辑的文件

                    }
                }
                //加入组
                else if(position == 1) {
                    if(mAccount.getGroup() != null) {
                        showDialogWithText("You have had a group!");
                        return;
                    }

                    final EditText et = new EditText(MainActivity.this);

                    new AlertDialog.Builder(MainActivity.this).setTitle("Input group id")
//                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(et)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog1, int which) {
                                    String input = et.getText().toString();
                                    if (input.equals("")) {
                                        Toast.makeText(getApplicationContext(), "input can't be vacant" + input, Toast.LENGTH_LONG).show();
                                    }
                                    else {

                                        JoinGroupTask joinGroupTask = new JoinGroupTask(){
                                            @Override
                                            protected void onPostExecute(Object res) {
                                                super.onPostExecute(res);
                                                Group group = (Group) res;
                                                showProgress(false, "");
                                                if( res == null ){
                                                    createAndShowDialog("Join Group Failed", "msg");
                                                    return;
                                                }
                                                try{
                                                    String filename = group.getId() + "-" + group.getFileName();
                                                    File tmpFile = File.createTempFile(filename, ".pdf", getExternalCacheDir());
                                                    Log.i(LOG_TAG, tmpFile.getAbsolutePath());
                                                    Uri uri = Util.base64ToFile(group.getPdfData(), tmpFile);
                                                    Intent intent = new Intent(MainActivity.this, PDFViewActivity.class);
                                                    intent.putExtra(PDFViewActivity.EXTRA_URI, uri);
                                                    intent.putExtra(PDFViewActivity.EXTRA_ACCOUNT, mAccount.getID());
                                                    intent.putExtra(PDFViewActivity.EXTRA_GROUP, group.getId());
                                                    MainActivity.this.startActivity(intent);
                                                }catch (IOException ex){
                                                    ex.printStackTrace();
                                                }
                                            }
                                        };
                                        joinGroupTask.execute(input);
                                        showProgress(true, "Joining Group");
//                                        if(!mAccount.setGroup(input)) {
//                                            showDialogWithText("Group id wrong!");
//                                            return;
//                                        }
//                                        else {
//                                            showDialogWithText("Successfully joined!");
//
//                                            mDatas[0] = "Your group id:" + mAccount.getGroup().getId() + "\n"
//                                                    + "Your PDF: " + mAccount.getGroup().getFileName();
//
//                                            adapter.notifyDataSetChanged();
//                                        }
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
                //退出组
                else if(position == 2) {
                    if( !mAccount.hasGroup()) {
                        showDialogWithText("You have not joined a group yet!");
                    }
                    else {
                        QuitGroupTask task = new QuitGroupTask(){
                            @Override
                            protected void onPostExecute(Object res) {
                                super.onPostExecute(res);
                                if( res == null ){
                                    showDialogWithText("Quit group error");
                                }else{
                                    showDialogWithText("Your Group " + mAccount.getGroup().getId() + "  dismissed successfully!");
                                    mAccount.quitGroup();
                                    TextView textView = (TextView)findViewById(R.id.slide_menu_groupinfo);
                                    textView.setText("You have not joined a group");
                                }
                            }
                        };
                        task.execute(mAccount.getGroup());
                    }
                }
                //关于
                else if(position == 3) {

                }
                //退出所有
                else if(position == 4) {
                    ActivityCollector.finishAll();
                }
            }
        });
    }


    //设置历史记录列表
    private ListView mHistoryListView;
    ArrayAdapter<String> mHistoryAdapter;
    private void setHistoryList() {
        mHistoryManager.initHistories(MainActivity.this);

        mHistoryListView = (ListView)findViewById(R.id.history_list_view);
        ArrayList<String> data = mHistoryManager.getListData();
        mHistoryAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.history_item,
                data);

        mHistoryListView.setAdapter(mHistoryAdapter);

        mHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = mHistoryManager.getPdfUriByindex(position);

                openNewPdf(uri);
            }
        });
    }


    //设置打开新pdf文件的按钮点击事件
    private Button mButton1, mButton2;
    private Button openOnlineButton;
    private void setButton() {
        mButton1 = (Button)findViewById(R.id.open_new_button);
        mButton2 = (Button)findViewById(R.id.clear_button);
        openOnlineButton = (Button)findViewById(R.id.open_online_button);

        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showOpenFileDialog(MainActivity.this, Util.REQUEST_OPEN_DOCUMENT);
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHistoryManager.clearHistories();
                mHistoryAdapter.notifyDataSetChanged();
            }
        });

        openOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final EditText et = new EditText(MainActivity.this);

                builder.setTitle("Input PDF Url")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String input = et.getText().toString();
                                // do some checking?
                                downloadAndOpenPDF(input);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    private void downloadAndOpenPDF(String url){
        try {
            final File tmpFile = File.createTempFile("test", ".pdf", getCacheDir());
            DownloadDocumentTask task = new DownloadDocumentTask(){
                @Override
                protected void onPostExecute(Boolean responseMsg) {
                    super.onPostExecute(responseMsg);
                    showProgress(false, "");
                    if( responseMsg == Boolean.TRUE ){
                        Intent intent = new Intent(MainActivity.this, PDFViewActivity.class);
                        intent.putExtra(PDFViewActivity.EXTRA_URI, Uri.fromFile(tmpFile));
                        //添加email
//                        Toast.makeText(MainActivity.this, "Download Document Success", Toast.LENGTH_LONG).show();
                        MainActivity.this.startActivity(intent);
                    }else {
                        Toast.makeText(MainActivity.this, "Download Document Failed", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                protected void onCancelled() {
                    super.onCancelled();
                }
            };
            task.execute(url, tmpFile.getAbsolutePath());
            showProgress(true, "Downloading Document");
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void showDialogWithText(String text) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage(text);
        dialog.setPositiveButton("OK", null);
        dialog.show();
    }


    private void setTopText() {
        TextView textView = (TextView)findViewById(R.id.slide_menu_account);
        textView.setText(mAccount.getID());
    }


    private void openNewPdf(Uri uri) {
        Intent intent = new Intent(MainActivity.this, PDFViewActivity.class);
        intent.putExtra(PDFViewActivity.EXTRA_URI, uri);

        this.startActivity(intent);

        mHistoryManager.addHistory(Util.getTime(), uri.toString());

        //更新listview视图
        mHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //打开文件
        if( requestCode == Util.REQUEST_OPEN_DOCUMENT ) {
            if( resultCode == Activity.RESULT_OK && data != null) {
                final Uri uri = data.getData();

                openNewPdf(uri);
            }
        }

        //登录，得到从登录界面传回的用户信息（email）
        else if( requestCode == Util.REQUEST_LOGIN ) {
            if( resultCode == Activity.RESULT_OK && data != null) {
                String email = data.getStringExtra(LoginActivity.GET_EMAIL_KEY);
                mAccount = new Account(email);
                setTopText();
            }
        }

        else if( requestCode == Util.REQUEST_CREATE_GROUP ){
            CreateGroupTask createGroupTask = new CreateGroupTask(){

                @Override
                protected void onPostExecute(Object obj) {
                    super.onPostExecute(obj);
                    Group group = (Group) obj;

                    showProgress(false, "");
                    if( group != null ){
                        createAndShowDialog("Create Group Success", "msg");
                        mAccount.setGroup(group);
//                showDialogWithText("Successfully created!");
                        TextView textView = (TextView)findViewById(R.id.slide_menu_groupinfo);
                        textView.setText("Your group id:" + mAccount.getGroup().getId() + "\n"
                                + "Your PDF: " + mAccount.getGroup().getFileName());
                        try{
                            File tmpFile = File.createTempFile(group.getFileName(), ".pdf", getCacheDir());
                            Uri uri = Util.base64ToFile(group.getPdfData(), tmpFile);
                            Intent intent = new Intent(MainActivity.this, PDFViewActivity.class);
                            Bundle bundle = new Bundle();
                            intent.putExtra(PDFViewActivity.EXTRA_URI, uri);
                            intent.putExtra(PDFViewActivity.EXTRA_ACCOUNT, mAccount.getID());
                            intent.putExtra(PDFViewActivity.EXTRA_GROUP, group.getId());
//                    intent.putExtra(PDFViewActivity.EXTRA_ACCOUNT, mAccount.getId());
                            //添加email
                            MainActivity.this.startActivity(intent);
                        } catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }else{
                        createAndShowDialog("Create Group Failed", "msg");

                    }
                }
            };

            final Uri fileUri = data.getData();

            try{
                String groupName = Integer.toString(Util.randInt(Group.GROUPID_MIN, Group.GROUPID_MAX));
                String pdfData = Util.inputStreamToBase64(MainActivity.this.getContentResolver().openInputStream(fileUri));
                Group group = new Group(groupName, mAccount, pdfData, fileUri.getLastPathSegment());
                createGroupTask.execute(group);
                showProgress(true, "Creating Group");

            }catch (FileNotFoundException ex){
                ex.printStackTrace();
            }
        }

    }
//
    private void testJoinGroup(){

        final String groupid = "group1";
        final String account = "admin13";
        boolean isNotFinished = true;

        JoinGroupTask joinGroupTask = new JoinGroupTask(){
            @Override
            protected void onPostExecute(Object res) {
                super.onPostExecute(res);
                Group group = (Group) res;
                showProgress(false, "");
                if( res == null ){
                    createAndShowDialog("Join Group Failed", "msg");
                    return;
                }
                try{
                    File tmpFile = File.createTempFile(group.getFileName(), ".pdf", getExternalCacheDir());
                    Log.i(LOG_TAG, tmpFile.getAbsolutePath());
                    Uri uri = Util.base64ToFile(group.getPdfData(), tmpFile);
                    Intent intent = new Intent(MainActivity.this, PDFViewActivity.class);
                    Bundle bundle = new Bundle();
                    intent.putExtra(PDFViewActivity.EXTRA_URI, uri);
                    intent.putExtra(PDFViewActivity.EXTRA_ACCOUNT, account);
                    intent.putExtra(PDFViewActivity.EXTRA_GROUP, groupid);
//                    intent.putExtra(PDFViewActivity.EXTRA_ACCOUNT, mAccount.getId());
                    //添加email
                    MainActivity.this.startActivity(intent);
                }catch (IOException ex){
                    ex.printStackTrace();
                }finally {
                    showProgress(false, "");
                }
            }
        };
        joinGroupTask.execute(groupid);
        showProgress(true, "Joining Group");
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

}
