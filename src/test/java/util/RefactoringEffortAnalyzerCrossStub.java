package util;

import beans.commit.Commit;
import beans.other.run.Registry;
import exe._2_run.RefactoringEffortAnalyzerCross;
import modules.git.GitController;
import utils.exception.FinishException;
import utils.exception.NoParentsException;
import utils.setting.SettingManager;

public class RefactoringEffortAnalyzerCrossStub extends RefactoringEffortAnalyzerCross {
    public RefactoringEffortAnalyzerCrossStub(Registry reg) throws NoParentsException, FinishException {
        super();
        registry = reg;
        this.sm = new SettingManager(new String[]{reg.project});
        this.git = new GitController(sm, "/crossTest/");
        git.checkout(reg.commitId);
        Commit commit = git.getCommit();
        commit.project = sm.getProject().name;
        this.commit = commit;
    }
    @Override
    protected void setRegistry() {

    }
    @Override
    protected void initiate() {
    }
}
