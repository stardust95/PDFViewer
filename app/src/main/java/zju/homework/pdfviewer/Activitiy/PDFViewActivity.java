package zju.homework.pdfviewer.Activitiy;

import android.content.DialogInterface;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.annotations.AnnotationProvider;
import com.pspdfkit.annotations.BorderStyle;
import com.pspdfkit.annotations.FreeTextAnnotation;
import com.pspdfkit.annotations.HighlightAnnotation;
import com.pspdfkit.annotations.InkAnnotation;
import com.pspdfkit.annotations.NoteAnnotation;
import com.pspdfkit.configuration.PSPDFConfiguration;
import com.pspdfkit.framework.ac;
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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import zju.homework.pdfviewer.BuildConfig;
import zju.homework.pdfviewer.R;
import zju.homework.pdfviewer.Utils.NetworkManager;

import static zju.homework.pdfviewer.Activitiy.PDFViewActivity.mapper;

class ItemsJsonDeserializer extends JsonDeserializer<List<RectF>> {

    @Override
    public List<RectF> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonParseException{
        InnerItems innerItems = jp.readValueAs(InnerItems.class);

        return innerItems.elements;
    }

    private static class InnerItems {
        public List<RectF> elements;
    }
}

class AnnotationDeserializer extends JsonDeserializer<Annotation> {

    @Override
    public Annotation deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonParseException{
        JsonNode node = jp.getCodec().readTree(jp);
        Annotation result = null;
        int page, borderColor, borderWidth, color, fillcolor;
        String type, content, creator, name, richText;
        BorderStyle style = null;
        RectF boundingBox = null;
        Date createdDate = null, modifiedDate = null;
        List<Integer> dashArray = null;

        page = node.get("pageIndex").asInt();
//        page = 3;     // DEBUG
        type = node.get("type").textValue();

        if( type.equals("INK") ){
            result = new InkAnnotation(page);
            List<List<PointF>> lines = mapper.readValue(node.get("lines").toString(),
                    new TypeReference<List<List<PointF>>>() {});
            ((InkAnnotation)result).setLines(lines);
            ((InkAnnotation)result).setLineWidth( node.get("lineWidth").asInt() );
        }else if( type.equals("FREETEXT") ){
            content = node.get("contents").asText();
            result = new FreeTextAnnotation(page, boundingBox, content);
            ((FreeTextAnnotation)result).setTextColor( node.get("textColor").asInt() );
            ((FreeTextAnnotation)result).setTextSize( node.get("textSize").asInt() );
            ((FreeTextAnnotation)result).setTextStrokeColor( node.get("textStrokeColor").asInt() );
        }else if( type.equals("HIGHLIGHT") ){
            List<RectF> rects = mapper.readValue(node.get("rects").toString(), new TypeReference<List<RectF>>() {});
            result = new HighlightAnnotation(page, rects);
        }else if( type.equals("NOTE") ){
            content = node.get("contents").asText();
            String iconName = node.get("iconName").asText();
            result = new NoteAnnotation(page, boundingBox, content, iconName);
        }else{
            throw new JsonParseException(jp, "Type Not Match of 4 basic annotation");
        }


        if( !node.get("borderStyle").asText().equals("NONE") ) {            // if has border style
            switch ( node.get("borderStyle").asText() ){
                case "SOLID": style = BorderStyle.SOLID; break;
                case "BEVELED": style = BorderStyle.BEVELED; break;
                case "DASHED": style = BorderStyle.DASHED; break;
                case "INSET": style = BorderStyle.INSET; break;
                case "NONE": style = BorderStyle.NONE; break;
                case "UNDERLINE": style = BorderStyle.UNDERLINE; break;
                case "UNKNOWN": style = BorderStyle.UNKNOWN; break;
            }
            result.setBorderStyle(style);
        }
        if( node.get("createdDate") != null ) {
            createdDate = mapper.readValue(node.get("createdDate").asText(), Date.class);
            result.setCreatedDate(createdDate);
        }

        if( node.get("modifiedDate") != null ) {
            modifiedDate = mapper.readValue(node.get("modifiedDate").asText(), Date.class);
            result.setModifiedDate(modifiedDate);
        }
        if( node.get("borderDashArray") != null ) {
            dashArray = mapper.readValue(node.get("borderDashArray").asText(), new TypeReference<List<Integer>>() {});
            result.setBorderDashArray(dashArray);
        }

        if( node.get("boundingBox") != null ){
            boundingBox = mapper.readValue(node.get("boundingBox").toString(), RectF.class);
            result.setBoundingBox(boundingBox);
        }

        if( node.get("contents") != null ) {
            content = node.get("contents").asText();
            result.setContents(content);
        }
        if( node.get("creator") != null ) {
            creator = node.get("creator").asText();
            result.setCreator(creator);
        }
        if( node.get("name") != null ) {
            name = node.get("name").asText();
            result.setName(name);
        }
        if( node.get("richText") != null ) {
            richText = node.get("richText").asText();
            result.setRichText(richText);
        }

        if( node.get("borderColor") != null ) {
            borderColor = node.get("borderColor").asInt();
            result.setBorderColor(borderColor);
        }
        if( node.get("borderWidth") != null ) {
            borderWidth = node.get("borderWidth").asInt();
            result.setBorderWidth(borderWidth);
        }
        if( node.get("color") != null ) {
            color = node.get("color").asInt();
            result.setColor(color);
        }
        if( node.get("fillColor") != null ) {
            fillcolor = node.get("fillColor").asInt();
            result.setFillColor(fillcolor);
        }

        return result;
    }

}


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = InkAnnotation.class, name = "InkAnnotation"),
        @JsonSubTypes.Type(value = FreeTextAnnotation.class, name = "FreeTextAnnotation"),
        @JsonSubTypes.Type(value = HighlightAnnotation.class, name = "HighlightAnnotation"),
        @JsonSubTypes.Type(value = NoteAnnotation.class, name = "NoteAnnotation")
})
@JsonDeserialize(using = AnnotationDeserializer.class)
abstract class AnnotationMixin extends Annotation{
    @JsonCreator
    AnnotationMixin(@JsonProperty("page") int pageIndex){
        super(pageIndex);
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
abstract class SubclassAnnotationMixin {
    @JsonCreator
    SubclassAnnotationMixin(@JsonProperty("acName") ac acName){ }
}

@JsonIgnoreProperties(ignoreUnknown = true, value = {"empty"})
//@JsonDeserialize(using = CollectionDeserializer.class)
abstract class IgnoreUnknownMixin{ }

public class PDFViewActivity extends AppCompatActivity implements PSPDFAnnotationManager.OnAnnotationCreationModeChangeListener,
        PSPDFAnnotationManager.OnAnnotationEditingModeChangeListener, TextSelectionManager.OnTextSelectionModeChangeListener,
        PSPDFAnnotationManager.OnAnnotationUpdatedListener{

    static final String LOG_TAG = "*** PDFVIEWER TAG ***";

    static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();

        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.addMixIn(RectF.class, IgnoreUnknownMixin.class);
        mapper.addMixIn(Annotation.class, AnnotationMixin.class);

        mapper.addMixIn(InkAnnotation.class, SubclassAnnotationMixin.class);
        mapper.addMixIn(FreeTextAnnotation.class, SubclassAnnotationMixin.class);
        mapper.addMixIn(HighlightAnnotation.class, SubclassAnnotationMixin.class);
        mapper.addMixIn(NoteAnnotation.class, SubclassAnnotationMixin.class);
    }

    public static final String EXTRA_URI = "ToolbarsInFragmentActivity.DocumentUri";

    private static final PSPDFConfiguration config = new PSPDFConfiguration.Builder(BuildConfig.PSPDFKIT_LICENSE_KEY).build();

    private PSPDFFragment fragment;
    private ToolbarCoordinatorLayout toolbarCoordinatorLayout;
    private Button annotationCreationButton;

    private AnnotationCreationToolbar annotationCreationToolbar;
    private TextSelectionToolbar textSelectionToolbar;
    private AnnotationEditingToolbar annotationEditingToolbar;

    private boolean annotationCreationActive = false;

    private PropertyInspectorCoordinatorLayout inspectorCoordinatorLayout;
    private AnnotationEditingInspectorController annotationEditingInspectorController;
    private AnnotationCreationInspectorController annotationCreationInspectorController;

    private NetworkManager networkManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        setSupportActionBar(null);

        networkManager = new NetworkManager();

        toolbarCoordinatorLayout = (ToolbarCoordinatorLayout) findViewById(R.id.toolbarCoordinatorLayout);

        annotationCreationToolbar = new AnnotationCreationToolbar(this);
        textSelectionToolbar = new TextSelectionToolbar(this);
        annotationEditingToolbar = new AnnotationEditingToolbar(this);

        // Use this if you want to use annotation inspector with annotation creation and editing toolbars.
        inspectorCoordinatorLayout = (PropertyInspectorCoordinatorLayout) findViewById(R.id.inspectorCoordinatorLayout);
        annotationEditingInspectorController = new DefaultAnnotationEditingInspectorController(this, inspectorCoordinatorLayout);
        annotationCreationInspectorController = new DefaultAnnotationCreationInspectorController(this, inspectorCoordinatorLayout);

        // The actual document Uri is provided with the launching intent. You can simply change that inside the CustomSearchUiExample class.
        // This is a check that the example is not accidentally launched without a document Uri.
        final Uri uri = getIntent().getParcelableExtra(EXTRA_URI);
        if (uri == null) {
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
            fragment = PSPDFFragment.newInstance(uri, config);
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

        updateButtonText();
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

        // get annotations
        AnnotationProvider annotationProvider = fragment.getDocument().getAnnotationProvider();
        List<Annotation> annotationList = annotationProvider.getAnnotations(fragment.getPage());

        String path = null;

        for(Annotation annotation : annotationList){
            if( annotation == null )
                continue;
            path = objectToJson(annotation, "test");
//            annotationProvider.removeAnnotationFromPage(annotation);
//            fragment.notifyAnnotationHasChanged(annotation);
        }
        try{
            fragment.getDocument().saveIfModified();
        }catch (IOException ex){

        }

        Annotation annotation = (Annotation) jsonToObject(path, Annotation.class);

        if( annotation != null ){
//            annotationProvider.addAnnotationToPage(annotation);
//            fragment.notifyAnnotationHasChanged(annotation);
        }else{
            Toast.makeText(this, "annotation must not be null", Toast.LENGTH_LONG);
        }

        try{
            fragment.getDocument().saveIfModified();
        }catch (IOException ex){

        }

//        fragment.getDocument().saveIfModifiedAsync()
//                .observeOn(AndroidSchedulers.mainThread());

        updateButtonText();
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


    public String objectToJson(Object obj, String filePath){

        try{
            File file = File.createTempFile(filePath, "json", this.getCacheDir());
            mapper.writeValue(file, obj);
            return file.getAbsolutePath();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public Object jsonToObject(String absolutePath, Class cls){
        try{
            File file = new File(absolutePath);
            Scanner in = new Scanner(new FileReader(file));
            Object obj = mapper.readValue(file, cls);
            Log.v(LOG_TAG, "filepath = " + absolutePath + ", content = " + in.toString());
            return obj;
        }

        catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    private class syncAnnotationTask extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params){
            return networkManager.postJson(networkManager.host + networkManager.registerUrl, "asdasd");
        }

        @Override
        protected void onPostExecute(String responseMsg){
            Toast.makeText(PDFViewActivity.this, responseMsg, Toast.LENGTH_LONG);
        }
    }

    private class registerTask extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params){
            return networkManager.postJson(networkManager.host + networkManager.registerUrl, "asdasd");
        }

        @Override
        protected void onPostExecute(String responseMsg){
            Toast.makeText(PDFViewActivity.this, responseMsg, Toast.LENGTH_LONG);
        }
    }

}
