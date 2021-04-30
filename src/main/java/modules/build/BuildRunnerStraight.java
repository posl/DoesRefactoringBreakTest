package modules.build;

import modules.git.GitController;
import modules.test.JunitTestResultManager;
import modules.test.JunitTestResultManagerStraight;
import utils.exception.*;
import utils.setting.SettingManager;

import java.io.IOException;

public class BuildRunnerStraight extends BuildRunner{
    /**
     * initialize maven
     *
     * @param sm
     * @param gitMain
     * @param commitId
     */
    public BuildRunnerStraight(SettingManager sm, GitController gitMain, String commitId) throws DependencyProblemException, ProductionProblemException, NoTargetBuildFileException, IOException, NoSureFireException {
        super(sm, gitMain, commitId);
    }

    /**
     * straight build do nothing
     * @throws IOException
     */
    @Override
    public void deploy()  throws IOException{
        //nothing
    }

    @Override
    protected JunitTestResultManager getTestJunitResultManager() throws NoParentsException {
        return new JunitTestResultManagerStraight(maven);
    }


}
