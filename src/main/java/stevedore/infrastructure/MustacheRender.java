package stevedore.infrastructure;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import stevedore.Render;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class MustacheRender implements Render {
    private static final String CONFIG_FILENAME = "config.yml";
    private final MustacheFactory mf;

    public MustacheRender() {
        mf = new DefaultMustacheFactory();
    }

    @Override
    public void render(HashMap<String, Object> variables) {
        try {
            mf.compile("project.mustache").execute(new FileWriter(CONFIG_FILENAME), variables).flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
