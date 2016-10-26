package stevedore.infrastructure.read;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import stevedore.*;
import stevedore.Environment;
import stevedore.Project;

import java.io.IOException;

public class EnvironmentCodec implements Codec<Environment> {
    private final ObjectMapper mapper;

    public EnvironmentCodec() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void encode(final BsonWriter writer, final Environment value, final EncoderContext encoderContext) {
        try {
            mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Environment decode(final BsonReader reader, final DecoderContext decoderContext) {
        try {
            return mapper.readValue(reader.readString(), stevedore.Environment.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Class<Environment> getEncoderClass() {
        return Environment.class;
    }
}
