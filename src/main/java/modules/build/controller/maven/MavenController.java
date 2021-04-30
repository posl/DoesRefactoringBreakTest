package modules.build.controller.maven;

import beans.commit.Commit;
import modules.build.controller.BuildToolController;
import modules.build.controller.maven.setup.SetElementOfPom;
import modules.git.GitController;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.*;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import utils.exception.NoParentsException;
import utils.exception.NoSureFireException;
import utils.exception.NoTargetBuildFileException;
import utils.log.LogCollector;
import utils.setting.SettingManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class MavenController implements BuildToolController {

//    public static final String MAVEN_HOME="/usr/local/Cellar/maven/3.6.3_1/";
    private static final String DEFAULT_JAVA_VERSION = "8";
    public GitController gc;
    private final String pomFile;
    private final Invoker invoker;
    private LogCollector lc;
    public Model model;
    public MavenController(GitController gc) {
        this.gc = gc;
        this.pomFile = this.gc.getRepoDir()+"/pom.xml";
        this.invoker = new DefaultInvoker();
        this.invoker.setMavenHome(new File(System.getenv("M2_HOME")));
    }

    /**
     * Write over pom file
     */
    public void writePom() {
        try {
            new MavenXpp3Writer().write(Files.newBufferedWriter(new File(this.pomFile).toPath()), model);
        } catch (IOException e) {
            logger.error(e);
            throw new AssertionError();
        }
    }

    /**
     * Meven execution. This method receives commands.\
     * e.g., clean, compile, test
     * @param goal
     * @param option
     * @return
     */
    public InvocationResult run(String goal, String option){
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(this.pomFile));
        request.setGoals( Collections.singletonList(goal) );
        if(option!=null){
            request.setMavenOpts(option);
        }
        this.lc = new LogCollector();
        this.invoker.setOutputHandler(new PrintStreamHandler(new PrintStream(this.lc), true));
        try {
            return this.invoker.execute(request);
        } catch (MavenInvocationException e) {
            logger.error(e);
            throw new RuntimeException();
        }
    }
    public List<String> getErrors(){
        return this.lc.getError();
    }

    /**
     * insert plugins
     * @param setter
     * @throws NoSureFireException
     */
    @Override
    public void setupExtraPom(SetElementOfPom setter) throws NoSureFireException {
        this.model = setter.setUpPom(this.model, this.getSettingManager().getProject());
    }

    @Override
    public String getParentCommitId() throws NoParentsException {
        return this.gc.getParentCommitId();
    }

    @Override
    public String getProject() {
        return this.gc.project;
    }

    @Override
    public SettingManager getSettingManager() {
        return gc.sm;
    }

    /**
     * get the output by surefire
     * @return
     */
    public String getSureFireOutputDir() {
       return this.getTargetDir()+"/"+ "surefire-reports/";
    }

    /**
     * get directory to store JUnit outputs
     * @return
     */
    public String getXMLStoreDir() {
        return gc.sm.getOutputDir()+"xml/";
    }
    public String getHomeDir(){
        return this.gc.getRepoDir();
    }

    @Override
    public String getRepoDir() {
        return gc.getRepoDir();
    }

    @Override
    public Commit getCommit() {
        try{
            return this.gc.getCommit();
        }catch (NoParentsException pe){
            System.err.println("NoParentsException");
            return null;
        }

    }

    public String getCommitId() {
        return gc.commitId;
    }

    /**
     * read pom file
     * @throws NoTargetBuildFileException
     */
    @Override
    public void readBuildFile() throws NoTargetBuildFileException {
        try {
            model = new MavenXpp3Reader().read(new FileReader(this.pomFile));
        } catch (IOException | XmlPullParserException e) {
            throw new NoTargetBuildFileException();
        }
    }

    /**
     * check out repository
     * @param commitId
     */
    @Override
    public void checkout(String commitId) {
        try{
            this.gc.checkout(commitId);
        }catch (NoParentsException pe){
            System.err.println("NoParentsException");
        }
    }

    /**
     * check out repository
     * @param commitId
     * @param parent
     */
    @Override
    public void checkout(String commitId, boolean parent) {
        try{
            this.gc.checkout(commitId, parent, false);
        }catch (NoParentsException pe){
            System.err.println("NoParentsException");
        }
    }

    /**
     * get target directory path including class dir from root
     * @return
     */
    public String getTargetDir(){//${project.build.directory}
        return this.getTargetDir(true);
    }
    /**
     * get target directory path from root (if the argument is true)
     * @return
     */
    public String getTargetDir(boolean absolute){//${project.build.directory}
        String baseDir = "";
        if(absolute){
            baseDir = this.gc.getRepoDir();
        }
        try {
            String dir = model.getBuild().getDirectory();
            if (dir != null) {
                return baseDir+dir;
            }
        }catch (NullPointerException e){
            //do nothing
        }
        return baseDir+"target";
    }

    /**
     * get source code directory path from root (if the argument is true)
     * @param absolute
     * @return
     */
    public String getSrcDir(boolean absolute){//${project.build.sourceDirectory}
        String baseDir = "";
        if(absolute){
            baseDir = this.gc.getRepoDir();
        }
        String dir = this.getSrcDirSetting();
        return baseDir+dir;
    }

    /**
     * get source code directory path
     * @return
     */
    public String getSrcDir() {//${project.build.sourceDirectory}
        return this.getSrcDir(true);
    }

    /**
     * get test code directory path from root (if the argument is true)
     * @return
     */
    public String getTestDir(){
        return this.getTestDir(true);
    }

    /**
     * get test code directory path
     * @param absolute
     * @return
     */
    public String getTestDir(boolean absolute){//${project.build.testSourceDirectory}
        String baseDir = "";
        if(absolute){
            baseDir = this.gc.getRepoDir();
        }
        String dir = this.getTestDirSetting();

        return baseDir+dir;
    }

    /**
     * get source code directory path according to pom file
     * @return
     */
    public String getSrcDirSetting(){
        String defaultDir = "src/main/java";
        try {
            String dir = model.getBuild().getSourceDirectory();
            if(dir!=null) return dir;
        }catch (NullPointerException npe){
            // if build is null
        }
        return defaultDir;
    }
    /**
     * get test code directory path according to pom file
     * @return
     */
    public String getTestDirSetting(){
        String defaultDir = "src/test/java";
        try {
            String dir = model.getBuild().getTestSourceDirectory();
            if(dir!=null) return dir;
        }catch (NullPointerException npe){
            // if build is null
        }
        return defaultDir;
    }
}
