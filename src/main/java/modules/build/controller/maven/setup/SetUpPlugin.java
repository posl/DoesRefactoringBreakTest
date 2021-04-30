package modules.build.controller.maven.setup;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import utils.log.MyLogger;

import java.util.ArrayList;
import java.util.List;


public abstract class SetUpPlugin implements SetElementOfPom{
    public MyLogger logger = MyLogger.getInstance();
    public List<String> surefireList = new ArrayList<String>();
    public String homeDir;
    public String srcDir;
    public String testDir;
    public String targetDir;





    protected abstract Plugin getPlugin(Plugin plugin);

    public abstract String getArtifact();
    public abstract String getGroupID();
    public abstract String getVersion();
    public abstract void setInheritanceOptions(Xpp3Dom p);





}
