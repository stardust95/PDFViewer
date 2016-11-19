package zju.homework.pdfviewer.Java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.annotations.FreeTextAnnotation;
import com.pspdfkit.annotations.HighlightAnnotation;
import com.pspdfkit.annotations.InkAnnotation;
import com.pspdfkit.annotations.NoteAnnotation;

/**
 * Created by stardust on 2016/11/18.
 */


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
public abstract class AnnotationMixin extends Annotation {
    @JsonCreator
    AnnotationMixin(@JsonProperty("page") int pageIndex){
        super(pageIndex);
    }
}
