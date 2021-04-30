package util;

import beans.commit.Commit;
import beans.test.result.AbstractJunitTestResult;
import modules.build.BuildRunner;
import modules.build.BuildRunnerStraight;
import modules.git.GitController;
import modules.refactoring.trace.ImpactAnalyzerController;
import modules.source.structure.StructureAnalyzer;
import org.refactoringminer.api.Refactoring;
import utils.setting.SettingManager;

import java.util.List;

public class SpecifierStub {
        public Commit commit;
        public SettingManager sm;
        public GitController gitX;
        public GitController gitX_1;
        public List<Refactoring> refactorings;
        public BuildRunner buildRunner;
        public ImpactAnalyzerController specifier;
        public AbstractJunitTestResult result;
        public StructureAnalyzer analyzer;

        public SpecifierStub(String project, String commitId) throws Exception{
            sm = new SettingManager(new String[]{project});
            gitX = new GitController(sm, "/main/");
            gitX.checkout(commitId);
            commit = gitX.getCommit();//next build
            commit.project = sm.getProject().name;
            buildRunner = new BuildRunnerStraight(sm, gitX, commit.commitId);
            buildRunner.deploy();
            analyzer = new StructureAnalyzer(buildRunner.maven);
            analyzer.scan();
            result = buildRunner.run();

    }


}
