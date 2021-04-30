package modules.build;

import modules.build.controller.BuildToolController;
import modules.build.controller.maven.MavenController;
import modules.git.GitController;
import modules.test.JunitTestResultManager;
import modules.test.JunitTestResultManagerCross;
import org.apache.commons.io.FileUtils;
import utils.exception.*;
import utils.setting.SettingManager;

import java.io.File;
import java.io.IOException;

public class BuildRunnerCross extends BuildRunner{
    public final BuildToolController mavenX_1;

    /**
     * initialize maven
     *
     * @param sm
     * @param gitMain
     * @param commitId
     */
    public BuildRunnerCross(SettingManager sm, GitController gitMain, String commitId) throws DependencyProblemException, ProductionProblemException, NoTargetBuildFileException, IOException, NoParentsException, NoSureFireException {
        super(sm, gitMain, commitId);
        mavenX_1 = new MavenController(new GitController(sm, "/x_1/"));
        mavenX_1.checkout(commitId, true);
    }

    @Override
    public void deploy() throws IOException {
        this.clean(maven);
        this.deploy(mavenX_1, maven);
    }

    @Override
    protected JunitTestResultManager getTestJunitResultManager() throws NoParentsException {
        return new JunitTestResultManagerCross(maven);
    }

    /**
     * delete existing test files for cross builds
     * @param mc
     * @throws IOException
     */
    protected void clean(BuildToolController mc) {
        try {
            FileUtils.deleteDirectory(new File(mc.getTargetDir()));
            FileUtils.deleteDirectory(new File(mc.getTestDir()));
        } catch (IOException e) {
        }
    }
    @Override
    protected void setUpDynamicAnalyzer(SettingManager sm) {

    }
    /**
     * Copy the test code in the previous revision to the directory the target revision
     * @param mavenX_1
     * @param mavenX
     * @throws IOException
     */
    private void deploy(BuildToolController mavenX_1, BuildToolController mavenX) throws IOException {
        //copy
        FileUtils.copyDirectory(new File(mavenX_1.getTestDir()), new File(mavenX.getTestDir()));
    }


}
