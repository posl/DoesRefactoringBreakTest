package modules.build;

import modules.git.GitController;
import modules.test.JunitTestResultManager;
import modules.test.JunitTestResultManagerPure;
import utils.exception.*;
import utils.setting.SettingManager;

import java.io.IOException;

public class BuildRunnerPure extends BuildRunner{
    /**
     * initialize maven
     *
     * @param sm
     * @param gitMain
     * @param commitId
     */
    public BuildRunnerPure(SettingManager sm, GitController gitMain, String commitId) throws DependencyProblemException, ProductionProblemException, NoTargetBuildFileException, IOException, NoSureFireException {
        super(sm, gitMain, commitId);
    }

    @Override
    public void deploy()  throws IOException{
        //nothing
    }

    @Override
    protected JunitTestResultManager getTestJunitResultManager() throws NoParentsException {
        return new JunitTestResultManagerPure(maven);
    }
    @Override
    protected void setUpDynamicAnalyzer(SettingManager sm) {

    }
    @Override
    public void setUpExtraPom(){
    }

}
