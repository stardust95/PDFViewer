package zju.homework.pdfviewer.Activitiy;

import android.content.DialogInterface;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.fasterxml.jackson.core.json.UTF8DataInputJsonParser;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.annotations.AnnotationProvider;
import com.pspdfkit.annotations.NoteAnnotation;
import com.pspdfkit.configuration.PSPDFConfiguration;
import com.pspdfkit.ui.PSPDFFragment;
import com.pspdfkit.ui.inspector.PropertyInspectorCoordinatorLayout;
import com.pspdfkit.ui.inspector.annotation.AnnotationCreationInspectorController;
import com.pspdfkit.ui.inspector.annotation.AnnotationEditingInspectorController;
import com.pspdfkit.ui.inspector.annotation.DefaultAnnotationCreationInspectorController;
import com.pspdfkit.ui.inspector.annotation.DefaultAnnotationEditingInspectorController;
import com.pspdfkit.ui.special_mode.controller.AnnotationCreationController;
import com.pspdfkit.ui.special_mode.controller.AnnotationEditingController;
import com.pspdfkit.ui.special_mode.controller.TextSelectionController;
import com.pspdfkit.ui.special_mode.manager.PSPDFAnnotationManager;
import com.pspdfkit.ui.special_mode.manager.TextSelectionManager;
import com.pspdfkit.ui.toolbar.AnnotationCreationToolbar;
import com.pspdfkit.ui.toolbar.AnnotationEditingToolbar;
import com.pspdfkit.ui.toolbar.TextSelectionToolbar;
import com.pspdfkit.ui.toolbar.ToolbarCoordinatorLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import zju.homework.pdfviewer.BuildConfig;
import zju.homework.pdfviewer.Java.AnnotationData;
import zju.homework.pdfviewer.Java.Group;
import zju.homework.pdfviewer.R;
import zju.homework.pdfviewer.Tasks.CreateGroupTask;
import zju.homework.pdfviewer.Tasks.DownloadAnnotationTask;
import zju.homework.pdfviewer.Tasks.UploadAnnotationTask;
import zju.homework.pdfviewer.Utils.NetworkManager;


public class PDFViewActivity extends AppCompatActivity implements PSPDFAnnotationManager.OnAnnotationCreationModeChangeListener,
        PSPDFAnnotationManager.OnAnnotationEditingModeChangeListener, TextSelectionManager.OnTextSelectionModeChangeListener,
        PSPDFAnnotationManager.OnAnnotationUpdatedListener{
    private static final String LOG_TAG = PDFViewActivity.class.getName();
    private Uri fileUri;
    private boolean isOnline;

    public static final String EXTRA_URI = "PDFViewAcivity.DocumentUri";
    public static final String EXTRA_ACCOUNT = "PDFViewAcivity.Account";
    public static final String EXTRA_GROUP = "PDFViewAcivity.Group";

    private static final PSPDFConfiguration config = new PSPDFConfiguration.Builder(BuildConfig.PSPDFKIT_LICENSE_KEY).build();

    private PSPDFFragment fragment;
    private ToolbarCoordinatorLayout toolbarCoordinatorLayout;
    private Button annotationCreationButton;
    private Button annotationClearButton;
    private Button changeAccountButton;

    private AnnotationCreationToolbar annotationCreationToolbar;
    private TextSelectionToolbar textSelectionToolbar;
    private AnnotationEditingToolbar annotationEditingToolbar;

    private boolean annotationCreationActive = false;

    private PropertyInspectorCoordinatorLayout inspectorCoordinatorLayout;
    private AnnotationEditingInspectorController annotationEditingInspectorController;
    private AnnotationCreationInspectorController annotationCreationInspectorController;

    private NetworkManager networkManager;
    private HashMap<Annotation, Boolean> hasUpload;
    private String account;
    private String groupId;


    private void testCreateGroup(){


        CreateGroupTask createGroupTask = new CreateGroupTask(){
            @Override
            protected void onPostExecute(Boolean res) {
                super.onPostExecute(res);
                if( res == true ){
                    createAndShowDialog("Create Group Success", "msg");
                }else{
                    createAndShowDialog("Create Group Failed", "msg");
                }
            }
        };

        try{
            String groupName = "group1";
            String pdfData = Util.inputStreamToBase64(this.getContentResolver().openInputStream(fileUri));
            Group group = new Group(groupName, pdfData, fileUri.getLastPathSegment(), account);
            createGroupTask.execute(group);

        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        setSupportActionBar(null);

        networkManager = new NetworkManager();
        isOnline = false;
        hasUpload = new HashMap<Annotation, Boolean>();
        account = null;

        toolbarCoordinatorLayout = (ToolbarCoordinatorLayout) findViewById(R.id.toolbarCoordinatorLayout);

        annotationCreationToolbar = new AnnotationCreationToolbar(this);
        textSelectionToolbar = new TextSelectionToolbar(this);
        annotationEditingToolbar = new AnnotationEditingToolbar(this);

        // Use this if you want to use annotation inspector with annotation creation and editing toolbars.
        inspectorCoordinatorLayout = (PropertyInspectorCoordinatorLayout) findViewById(R.id.inspectorCoordinatorLayout);
        annotationEditingInspectorController = new DefaultAnnotationEditingInspectorController(this, inspectorCoordinatorLayout);
        annotationCreationInspectorController = new DefaultAnnotationCreationInspectorController(this, inspectorCoordinatorLayout);

        account = getIntent().getStringExtra(EXTRA_ACCOUNT);
        groupId = getIntent().getStringExtra(EXTRA_GROUP);
        if( groupId != null ){
            isOnline = true;
        }
        // The actual document Uri is provided with the launching intent. You can simply change that inside the CustomSearchUiExample class.
        // This is a check that the example is not accidentally launched without a document Uri.
        fileUri = getIntent().getParcelableExtra(EXTRA_URI);
        if (fileUri == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Could not start example.")
                    .setMessage("No document Uri was provided with the launching intent.")
                    .setNegativeButton("Leave example", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .show();

            return;
        }

        fragment = (PSPDFFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = PSPDFFragment.newInstance(fileUri, config);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

        fragment.registerAnnotationCreationModeChangeListener(this);
        fragment.registerAnnotationEditingModeChangeListener(this);
        fragment.registerTextSelectionModeChangeListener(this);

        // annotation listener
        fragment.registerAnnotationUpdatedListener(this);

        annotationCreationButton = (Button) findViewById(R.id.openAnnotationEditing);
        annotationCreationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (annotationCreationActive) {
                    fragment.exitCurrentlyActiveMode();
                } else {
                    fragment.enterAnnotationCreationMode();
                }
            }
        });

        annotationClearButton = (Button) findViewById(R.id.clearAnnotation);
        annotationClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnnotationProvider provider = fragment.getDocument().getAnnotationProvider();
                for (Annotation annotation : provider.getAnnotations(fragment.getPage())){
                    provider.removeAnnotationFromPage(annotation);
                    fragment.notifyAnnotationHasChanged(annotation);
                }
                try {
                    fragment.getDocument().saveIfModified();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });

        updateButtonText();

//        testCreateGroup();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragment.unregisterAnnotationCreationModeChangeListener(this);
        fragment.unregisterAnnotationEditingModeChangeListener(this);
        fragment.unregisterTextSelectionModeChangeListener(this);
        fragment.unregisterAnnotationUpdatedListener(this);
    }
    @Override
    public void onAnnotationUpdated(@NonNull Annotation annotation) {
        Log.v("TAG", "The annotation was updated");
        Toast.makeText(this, "annotation was updated", Toast.LENGTH_LONG);
    }


    /**
     * Called when the annotation creation mode has been entered.
     * @param controller Provided controller for managing annotation creation mode.
     */
    @Override
    public void onEnterAnnotationCreationMode(@NonNull AnnotationCreationController controller) {
        // When entering the annotation creation mode we bind the creation inspector to the provided controller.
        // Controller handles request for toggling annotation inspector.
        annotationCreationInspectorController.bindAnnotationCreationController(controller);

        // When entering the annotation creation mode we bind the toolbar to the provided controller, and
        // issue the coordinator layout to animate the toolbar in place.
        // Whenever the user presses an action, the toolbar forwards this command to the controller.
        // Instead of using the `AnnotationEditingToolbar` you could use a custom UI that operates on the controller.
        // Same principle is used on all other toolbars.
        annotationCreationToolbar.bindController(controller);
        toolbarCoordinatorLayout.displayContextualToolbar(annotationCreationToolbar, true);
        annotationCreationActive = true;
        updateButtonText();

        if( !isOnline ){
            return;
        }

        DownloadAnnotationTask task = new DownloadAnnotationTask(){
            protected void onPostExecute(final String result) {
                super.onPostExecute(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<AnnotationData> annotationDatas = (List<AnnotationData>) Util.jsonToObject(result, new TypeReference<List<AnnotationData>>() {});
                        if( annotationDatas == null || annotationDatas.size() == 0 ){
                            Toast.makeText(PDFViewActivity.this, "No Annotations to sync", Toast.LENGTH_LONG);
                            return;
                        }
//                        AnnotationProvider provider = fragment.getDocument().getAnnotationProvider();
                        for (AnnotationData annotationData : annotationDatas){
                            Annotation annotation = (Annotation) Util.jsonToObject(annotationData.getJsonData(), Annotation.class);
                            fragment.getDocument().getAnnotationProvider().addAnnotationToPage(annotation);
                            fragment.notifyAnnotationHasChanged(annotation);
                        }
                        try {
                            fragment.getDocument().saveIfModified();
                            Log.i(LOG_TAG, "Document is saved");
                        }catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }
                });
//                Toast.makeText(PDFViewActivity.this, responseMsg, Toast.LENGTH_LONG);
            }
        };
        task.execute(account, groupId);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                if( hasUpload.size() > 0 ){
//                    for (Annotation an : hasUpload.keySet()){
//                        if ( fragment.getPage() == an.getPageIndex() ){
//                            continue;
//                        }
//                        fragment.getDocument().getAnnotationProvider().addAnnotationToPage(an);
//                        fragment.notifyAnnotationHasChanged(an);
//                    }
//                }
////                else{
////                    NoteAnnotation noteAnnotation = new NoteAnnotation(7, new RectF(100, 100, 100, 100), "TEST CONTENT", NoteAnnotation.COMMENT);
////                    provider.addAnnotationToPage(noteAnnotation);
////                    fragment.notifyAnnotationHasChanged(noteAnnotation);
////                }
//                try {
//                    fragment.getDocument().saveIfModified();
//                }catch (IOException ex){
//                    ex.printStackTrace();
//                }
//            }
//        });

    }

    /**
     * Called when the annotation creation mode has changed, meaning another annotation type is
     * being selected for creation. Provided controller holds all the new data.
     * @param controller Provided controller for managing annotation creation mode.
     */
    @Override
    public void onChangeAnnotationCreationMode(@NonNull AnnotationCreationController controller) {
        // Nothing to be done here, if toolbar is bound to the controller it will pick up the changes.
    }

    /**
     * Called when the annotation creation mode has been exited.
     * @param controller Provided controller for managing annotation creation mode.
     */
    @Override
    public void onExitAnnotationCreationMode(@NonNull AnnotationCreationController controller) {
        // Once we're done with editing, unbind the controller from the toolbar, and remove it from the
        // toolbar coordinator layout (with animation in this case).
        // Same principle is used on all other toolbars.
        toolbarCoordinatorLayout.removeContextualToolbar(true);
        annotationCreationToolbar.unbindController();
        annotationCreationActive = false;

        // Also unbind the annotation creation controller from the inspector controller.
        annotationCreationInspectorController.unbindAnnotationCreationController();

        updateButtonText();

        if( !isOnline ){
            return;
        }

        // get annotations
        AnnotationProvider annotationProvider = fragment.getDocument().getAnnotationProvider();
        List<Annotation> annotationList = annotationProvider.getAnnotations(fragment.getPage());

        for(final Annotation annotation : annotationList){
            if( annotation == null || hasUpload.get(annotation) == Boolean.TRUE ){
                continue;
            }
            String json = Util.objectToJson(annotation);
            UploadAnnotationTask task = new UploadAnnotationTask(){
                @Override
                protected void onPostExecute(String responseMsg) {
                    super.onPostExecute(responseMsg);
                    hasUpload.put(annotation, Boolean.TRUE);
                    Toast.makeText(PDFViewActivity.this, responseMsg, Toast.LENGTH_LONG);
                }
            };
            task.execute(new AnnotationData(groupId, account, json));
        }
        try{
            fragment.getDocument().saveIfModified();
        }catch (IOException ex){
            ex.printStackTrace();
        }
//
//        Annotation annotation = (Annotation) Util.jsonToObject(path, Annotation.class);
//
//        if( annotation != null ){
//            annotationProvider.addAnnotationToPage(annotation);
//            fragment.notifyAnnotationHasChanged(annotation);
//        }else{
//            Toast.makeText(this, "annotation must not be null", Toast.LENGTH_LONG);
//        }
//
//        try{
//            fragment.getDocument().saveIfModified();
//        }catch (IOException ex){
//
//        }

//        fragment.getDocument().saveIfModifiedAsync()
//                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * Called when annotation editing mode has been entered.
     * @param controller Controller for managing annotation editing.
     */
    @Override
    public void onEnterAnnotationEditingMode(@NonNull AnnotationEditingController controller) {
        annotationEditingInspectorController.bindAnnotationEditingController(controller);

        annotationEditingToolbar.bindController(controller);
        toolbarCoordinatorLayout.displayContextualToolbar(annotationEditingToolbar, true);
    }

    /**
     * Called then annotation editing mode changes, meaning another annotation is being selected for editing.
     * @param controller Controller for managing annotation editing.
     */
    @Override
    public void onChangeAnnotationEditingMode(@NonNull AnnotationEditingController controller) {
        // Nothing to be done here, if toolbar is bound to the controller it will pick up the changes.
    }

    /**
     * Called when annotation editing mode has been exited.
     * @param controller Controller for managing annotation editing.
     */
    @Override
    public void onExitAnnotationEditingMode(@NonNull AnnotationEditingController controller) {
        toolbarCoordinatorLayout.removeContextualToolbar(true);
        annotationEditingToolbar.unbindController();

        annotationEditingInspectorController.unbindAnnotationEditingController();
    }

    /**
     * Called when entering text selection mode.
     * @param controller Provided controller for text selection mode actions.
     */
    @Override
    public void onEnterTextSelectionMode(@NonNull TextSelectionController controller) {
        textSelectionToolbar.bindController(controller);
        toolbarCoordinatorLayout.displayContextualToolbar(textSelectionToolbar, true);
    }

    /**
     * Called when exiting text selection mode.
     * @param controller Provided controller for text selection mode actions.
     */
    @Override
    public void onExitTextSelectionMode(@NonNull TextSelectionController controller) {
        toolbarCoordinatorLayout.removeContextualToolbar(true);
        textSelectionToolbar.unbindController();
    }

    private void updateButtonText() {
        annotationCreationButton.setText(annotationCreationActive ? R.string.close_editor : R.string.open_editor);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

//
//    private class registerTask extends AsyncTask<String, Void, String>{
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... params){
//            try{
//                    networkManager.postJson(Util.URL_ACCOUNT,
//                        mapper.writeValueAsString(new Account(params[0], params[1]))
//                );
//
//            }catch (JsonProcessingException ex){
//                ex.printStackTrace();
//            }
//            return "Register Finished";
//        }
//
//        @Override
//        protected void onPostExecute(String responseMsg){
//            Toast.makeText(PDFViewActivity.this, responseMsg, Toast.LENGTH_LONG);
//        }
//    }



}
