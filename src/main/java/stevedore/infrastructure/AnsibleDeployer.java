package stevedore.infrastructure;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import stevedore.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class AnsibleDeployer implements Deployer{
    public static final String ANSIBLE_PATH = "$HOME/dev/ansible_deploy_docker";
    private final Render render;

    public AnsibleDeployer(Render render) {
        this.render = render;
    }

    @Override
    public void release(Project project, Environment environment, Release release) {
        render.render(getCombinedConfiguration(project, environment));
        executeAnsiblePlaybook(ANSIBLE_PATH + "/release.yml");

//        ProcessBuilder pb = new ProcessBuilder("/tmp/test/script.sh", "myArg1", "myArg2");
//        pb.directory(new File("/tmp/test"));
//        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
//        Map<String, String> env = pb.environment();
//        env.put("VAR1", "myValue");
//        env.remove("OTHERVAR");
//        env.put("VAR2", env.get("VAR1") + "suffix");
//        try {
//            Process p = pb.start();
//            if (p.exitValue() != 0) {
//                throw new RuntimeException("Ansible playbook has failed");
//            }
//            return environment.release(release);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            return null;
//        }
    }

    @Override
    public void deploy(Project project, Environment environment, Version version) {
        render.render(getCombinedConfiguration(project, environment));
        executeAnsiblePlaybook(ANSIBLE_PATH + "/deploy.yml");
    }

    private HashMap getCombinedConfiguration(Project project, Environment environment) {
        HashMap<String, Object> config = new HashMap();
        config.putAll(project.toHashMap());
        config.putAll(environment.toHashMap());

        return config;
    }

    private void executeAnsiblePlaybook(String playbook) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ansible-playbook", "-i", "localhost", playbook, "--extra-vars", "\"@config.yml\"");
            Process p = pb.start();
            if (p.exitValue() != 0) {
                throw new RuntimeException("Ansible playbook has failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
