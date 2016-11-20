package zju.homework.pdfviewer.Java;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by stardust on 2016/11/20.
 */

public class GroupSerializer extends JsonSerializer<Group> {
    @Override
    public void serialize(Group group, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", group.getId());
        jsonGenerator.writeStringField("pdfData", group.getPdfData());
        jsonGenerator.writeStringField("fileName", group.getFileName());
        jsonGenerator.writeStringField("creator", group.getCreator().getID());
        jsonGenerator.writeEndObject();
    }
}