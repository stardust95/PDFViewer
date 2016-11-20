package zju.homework.pdfviewer.Java;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * Created by stardust on 2016/11/20.
 */

public class GroupDeserializer extends JsonDeserializer<Group> {
    @Override
    public Group deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String creator = node.get("creator").asText();
        String id = node.get("id").asText();
        String pdfData = node.get("pdfData").asText();
        String fileName = node.get("fileName").asText();
        return new Group(id, new Account(creator), pdfData, fileName);
    }
}