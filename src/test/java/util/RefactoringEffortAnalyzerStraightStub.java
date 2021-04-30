package util;

import beans.commit.Commit;
import beans.other.run.Registry;
import exe._2_run.RefactoringEffortAnalyzerStraight;
import modules.git.GitController;
import utils.exception.FinishException;
import utils.exception.NoParentsException;
import utils.setting.SettingManager;

public class RefactoringEffortAnalyzerStraightStub extends RefactoringEffortAnalyzerStraight {
    public RefactoringEffortAnalyzerStraightStub(Registry reg) throws NoParentsException, FinishException {
        super();
        registry = reg;
        this.sm = new SettingManager(new String[]{reg.project});
        this.git = new GitController(sm, "/main/");
        git.checkout(reg.commitId);
        Commit commit = git.getCommit();
        commit.project = sm.getProject().name;
        this.commit = commit;
    }
}
