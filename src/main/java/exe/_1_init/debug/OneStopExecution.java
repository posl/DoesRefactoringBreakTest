package exe._1_init.debug;

import beans.commit.Commit;
import exe._1_init.ExeInitialization;
import exe._2_run.debug.RefactoringEffortAnalyzerCrossDebugger;
import exe._2_run.debug.RefactoringEffortAnalyzerStraightDebugger;
import exe._3_analyze.ImpactAnalyzerMain;
import modules.git.GitController;
import utils.exception.FinishException;
import utils.exception.NoParentsException;
import utils.setting.SettingManager;

import java.util.ArrayList;
import java.util.List;

public class OneStopExecution {
    /**
     * This class is used to register a specific commit and build them.
     * @param args
     * @throws NoParentsException
     */
    public static void main(String[] args) throws NoParentsException, FinishException {
        String project = "javapoet";
        List<String> targetsId = new ArrayList<>();
        targetsId.add("c5b6b36b2e98b59f0711c1bfc8486c32eb3b482a");
        targetsId.add("e8cd8f4881897b00043c8114aac7537ab11c4b16");

        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/x/");
        List<Commit> commits = gitX.getCommits(targetsId);
        ExeInitialization.store(sm, gitX, commits);

        try {
            RefactoringEffortAnalyzerCrossDebugger.main(null);
        }catch (FinishException fe){

        }
        try {
            RefactoringEffortAnalyzerStraightDebugger.main(null);
        }catch (FinishException fe){

        }
        ImpactAnalyzerMain.main(null);


    }
}
