package zju.homework.pdfviewer.Java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pspdfkit.framework.ac;

/**
 * Created by stardust on 2016/11/18.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SubclassAnnotationMixin {
    @JsonCreator
    SubclassAnnotationMixin(@JsonProperty("acName") ac acName){ }
}
