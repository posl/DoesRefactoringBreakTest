package modules.build.controller.maven.setup;

import org.apache.maven.model.Model;

import java.util.Properties;

public class SetUpMavenCompilerProperty extends SetUpProperty{
    /**
     * set up java version
     * @param model
     * @param project
     * @return
     */
    @Override
    public Model setUp(Model model, String project) {
        Properties p = model.getProperties();
        p = getProperties(p, "maven.compiler.source", "13");//TODO: be flexible
        p = getProperties(p, "maven.compiler.target", "13");
        model.setProperties(p);
        return model;
    }
}
