package zju.homework.pdfviewer.Java;

import android.graphics.RectF;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.List;

/**
 * Created by stardust on 2016/11/18.
 */


public class ItemsJsonDeserializer extends JsonDeserializer<List<RectF>> {

    @Override
    public List<RectF> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonParseException {
        InnerItems innerItems = jp.readValueAs(InnerItems.class);

        return innerItems.elements;
    }

    private static class InnerItems {
        public List<RectF> elements;
    }
}