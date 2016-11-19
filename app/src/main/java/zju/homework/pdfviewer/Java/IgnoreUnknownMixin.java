package zju.homework.pdfviewer.Java;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by stardust on 2016/11/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true, value = {"empty"})
//@JsonDeserialize(using = CollectionDeserializer.class)
public abstract class IgnoreUnknownMixin{ }
