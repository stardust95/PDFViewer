package zju.homework.pdfviewer.Activitiy;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import zju.homework.pdfviewer.R;
import zju.homework.pdfviewer.Utils.DrawerArrowDrawable;
import zju.homework.pdfviewer.Utils.Util;
import zju.homework.pdfviewer.Java.Account;

public class MainActivity extends AppCompatActivity {

    private Account mAccount;   //用户

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setDrawerArrow();

        //ActivityCollector.addActivity(this);

        //Util.showOpenFileDialog(MainActivity.this);
        Util.userLogin(MainActivity.this);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //打开文件
        if( requestCode == Util.REQUEST_OPEN_DOCUMENT ) {
            if( resultCode == Activity.RESULT_OK && data != null) {
                final Uri uri = data.getData();
                Intent intent = new Intent(MainActivity.this, PDFViewActivity.class);
                intent.putExtra(PDFViewActivity.EXTRA_URI, uri);
                //添加email
                this.startActivity(intent);
            }
        }

        //登录，得到从登录界面传回的用户信息（email）
        else if( requestCode == Util.REQUEST_LOGIN ) {
            if( resultCode == Activity.RESULT_OK && data != null) {
                mAccount = (Account)data.getSerializableExtra(LoginActivity.GET_EMAIL_KEY);
            }
        }

    }


}
