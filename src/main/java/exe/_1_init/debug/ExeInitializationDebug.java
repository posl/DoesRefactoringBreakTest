package exe._1_init.debug;

import beans.commit.Commit;
import exe._1_init.ExeInitialization;
import modules.git.GitController;
import utils.exception.NoParentsException;
import utils.setting.SettingManager;

import java.util.ArrayList;
import java.util.List;

public class ExeInitializationDebug {
    /**
     * This class is used to register a specific commit
     * @param args
     * @throws NoParentsException
     */
    public static void main(String[] args) throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        List<String> targetsId = new ArrayList<>();
        targetsId.add("2833f1d5157877675801271a326bb2ba9b9b43ca");


        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/x/");
        List<Commit> commits = gitX.getCommits(targetsId);
        ExeInitialization.store(sm, gitX, commits);
    }
}
