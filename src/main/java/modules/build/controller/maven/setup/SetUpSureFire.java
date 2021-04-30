package modules.build.controller.maven.setup;

import org.apache.maven.model.*;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import utils.exception.NoSureFireException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SetUpSureFire extends SetUpPlugin {
    public static final String GROUP_ID = "org.apache.maven.plugins";
    public static final String ARTIFACT="maven-surefire-plugin";
    public static final String VERSION="2.22.2";

    /**
     * Find existing surefire setting. If the repository does not have surefire, we add it.
     * @param model
     * @param project
     * @return
     * @throws NoSureFireException
     */
    public Model setUp(Model model, String project) throws NoSureFireException {
        boolean isExistSurefire=false;
        // find surefire in build element
        isExistSurefire = this.surefireCheckInPlugin(model, isExistSurefire);
        isExistSurefire = this.surefireCheckInPluginManagement(model, isExistSurefire);
        // find surefire in profile element
        isExistSurefire = this.surefireCheckInProfile(model, isExistSurefire);
        //Otherwise, make it
        if(!isExistSurefire){
            this.defaultActionIfNoSureFire(model);
        }
        return model;
    }

    /**
     * find sure fire in the Plugin element
     * @param model
     * @param isExistSurefire
     * @return
     */
    private boolean surefireCheckInPlugin(Model model, boolean isExistSurefire) {
        Build b = model.getBuild();
        if(b!=null){
            Build tmpBuild = new Build();
            for(Plugin plugin: b.getPlugins()){
                isExistSurefire = isExistSurefire|this.setupPlugin(tmpBuild, plugin);
            }
            model.setBuild(tmpBuild);
        }else{
            b = new Build();
            model.setBuild(b);
        }
        return isExistSurefire;
    }

    /**
     * find sure fire in the PluginManagement element
     * @param model
     * @param isExistSurefire
     * @return
     */
    private boolean surefireCheckInPluginManagement(Model model, boolean isExistSurefire) {
        Build b = model.getBuild();
        PluginManagement pm = b.getPluginManagement();
        if(pm!=null){
            PluginManagement tmpPm = new PluginManagement();
            for(Plugin plugin: pm.getPlugins()){
                isExistSurefire = isExistSurefire|this.setupPlugin(tmpPm, plugin);
            }
            b.setPluginManagement(tmpPm);
        }
        return isExistSurefire;
    }
    /**
     * find sure fire in the Profile element
     * @param model
     * @param isExistSurefire
     * @return
     */
    private boolean surefireCheckInProfile(Model model, boolean isExistSurefire) {
        for(int i = 0; i<model.getProfiles().size();i++){
            Profile profile = model.getProfiles().get(i);
            Build pb = new Build();
            if(profile.getBuild() == null) continue;
            for(Plugin plugin : profile.getBuild().getPlugins()){
                isExistSurefire = isExistSurefire|this.setupPlugin(pb, plugin);
            }
            profile.setBuild(pb);
            model.getProfiles().set(i, profile);
        }
        return isExistSurefire;
    }

    /**
     * set up default surefire
     * @param model
     */
    private void defaultActionIfNoSureFire(Model model) {
        Build b = model.getBuild();
        List<Plugin> list = b.getPlugins();
        if(list==null){
            list = new ArrayList<>();
        }
        Plugin p = new Plugin();
        p.setArtifactId(this.getArtifact());
        p.setGroupId(this.getGroupID());
        p.setVersion(this.getVersion());
        p.setConfiguration(this.createConf());
        list.add(p);
        b.setPlugins(list);
        //source directory setting
        String src = this.getDirectory(model.getBuild().getSourceDirectory(), srcCandidates);
        b.setSourceDirectory(src);
        //test directory setting
        String test = this.getDirectory(model.getBuild().getTestSourceDirectory(), testCandidates);
        b.setTestSourceDirectory(test);
    }
    String[] srcCandidates = {"src/main/java/", "src/java/", "src/"};//should order by longer name
    String[] testCandidates = {"src/test/java/", "test/java/", "tests/"};
    private String getDirectory(String sourceDirectory, String[] candidates) {
        if(sourceDirectory!=null){
            return sourceDirectory;
        }
        for(String c: candidates){
            File f = new File(c);
            if(f.exists()){
                return c;
            }
        }
        throw new RuntimeException();
    }

    /**
     * insert into xml file
     * @param b
     * @param plugin
     */
    private boolean setupPlugin(Object b, Plugin plugin) {
        if(plugin.getArtifactId().equals(getArtifact())){//if surefire exists
            Plugin p = this.getPlugin(plugin);
            this.addPlugin(b, p);
            return true;
        }else{
            this.addPlugin(b, plugin);
            return false;
        }
    }

    /**
     * insert into xml file
     * @param b
     * @param plugin
     */
    private void addPlugin(Object b, Plugin plugin) {
        if(b instanceof Build){
            ((Build) b).addPlugin(plugin);
        }else if(b instanceof PluginManagement){
            ((PluginManagement) b).addPlugin(plugin);
        }else{
            throw new RuntimeException();
        }
    }


    @Override
    public Plugin getPlugin(Plugin plugin) {
        if(plugin.getConfiguration()!=null){//if existing setting file exists
            this.updateConfiguration(plugin);
        }else{
            Xpp3Dom conf = this.createConf();
            plugin.setConfiguration(conf);
            plugin.setVersion(this.getVersion());
        }
        return plugin;
    }

    /**
     * create a configuration element
     * @return
     */
    private Xpp3Dom createConf() {
        Xpp3Dom conf = new Xpp3Dom("configuration");
        addChild(conf, "testFailureIgnore", "true");
        this.setInheritanceOptions(conf);
        return conf;
    }

    /**
     * update a configuration element
     * @return
     */
    private void updateConfiguration(Plugin plugin) {
        Xpp3Dom conf = ((Xpp3Dom)plugin.getConfiguration());
        addChild(conf, "skip", "false");
        addChild(conf, "testFailureIgnore", "true");
        this.setInheritanceOptions(conf);
        plugin.setConfiguration(conf);
    }


    @Override
    public String getArtifact() {
        return ARTIFACT;
    }

    @Override
    public String getGroupID() {
        return GROUP_ID;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void setInheritanceOptions(Xpp3Dom conf) {
    }


}
