package stevedore.infrastructure.read;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import stevedore.Project;

import java.io.IOException;

public class ProjectCodec implements Codec<Project> {
    private final ObjectMapper mapper;

    public ProjectCodec() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void encode(final BsonWriter writer, final Project value, final EncoderContext encoderContext) {
        try {
            mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Project decode(final BsonReader reader, final DecoderContext decoderContext) {
        try {
            return mapper.readValue(reader.readString(), Project.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Class<Project> getEncoderClass() {
        return Project.class;
    }
}
