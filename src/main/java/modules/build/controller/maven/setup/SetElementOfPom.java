package modules.build.controller.maven.setup;

import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import utils.exception.NoSureFireException;
import utils.log.MyLogger;
import utils.setting.inner.Project;

public interface SetElementOfPom {
    MyLogger logger = MyLogger.getInstance();
    Model setUp(Model model, String project) throws NoSureFireException;
    default Model setUpPom(Model model, Project project) throws NoSureFireException {
        model = setUp(model, project.name);
        return model;
    }

    /**
     * Append Maven XML (e.g., plugins) to pom.xml
     * @param xml
     * @param key
     * @param value
     */
    default void addChild(Xpp3Dom xml, String key, String value) {
        Xpp3Dom x = xml.getChild(key);
        if(x==null) {
            Xpp3Dom testFailureIgnore = new Xpp3Dom(key);
            testFailureIgnore.setValue(value);
            xml.addChild(testFailureIgnore);
        }else{
            x.setValue(value);
        }
    }

}
