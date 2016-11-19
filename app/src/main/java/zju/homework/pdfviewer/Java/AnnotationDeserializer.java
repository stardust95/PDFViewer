package zju.homework.pdfviewer.Java;

import android.graphics.PointF;
import android.graphics.RectF;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.annotations.BorderStyle;
import com.pspdfkit.annotations.FreeTextAnnotation;
import com.pspdfkit.annotations.HighlightAnnotation;
import com.pspdfkit.annotations.InkAnnotation;
import com.pspdfkit.annotations.NoteAnnotation;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import zju.homework.pdfviewer.Activitiy.Util;

/**
 * Created by stardust on 2016/11/18.
 */

public class AnnotationDeserializer extends JsonDeserializer<Annotation> {

    @Override
    public Annotation deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonParseException {



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
            List<List<PointF>> lines = (List<List<PointF>>) Util.jsonToObject(node.get("lines").toString(),
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
            List<RectF> rects = (List<RectF>) Util.jsonToObject(node.get("rects").toString(), new TypeReference<List<RectF>>() {});
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
            createdDate = (Date) Util.jsonToObject(node.get("createdDate").asText(), Date.class);
            result.setCreatedDate(createdDate);
        }

        if( node.get("modifiedDate") != null ) {
            modifiedDate = (Date) Util.jsonToObject(node.get("modifiedDate").asText(), Date.class);
            result.setModifiedDate(modifiedDate);
        }
        if( node.get("borderDashArray") != null ) {
            dashArray = (List<Integer>) Util.jsonToObject(node.get("borderDashArray").asText(), new TypeReference<List<Integer>>() {});
            result.setBorderDashArray(dashArray);
        }

        if( node.get("boundingBox") != null ){
            boundingBox = (RectF) Util.jsonToObject(node.get("boundingBox").toString(), RectF.class);
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