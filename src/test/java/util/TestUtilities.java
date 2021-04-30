package util;

import beans.commit.Commit;
import beans.other.run.IARegistry;
import beans.other.run.Registry;
import beans.other.run.StraightRegistry;
import beans.refactoring.Refactoring2;
import beans.test.result.AbstractJunitTestResult;
import exe._2_run.RefactoringEffortAnalyzerStraight;
import exe._3_analyze.ImpactAnalysisService;
import modules.build.controller.BuildToolController;
import modules.build.controller.maven.MavenController;
import modules.git.GitController;
import modules.refactoring.detect.RefactoringMinerController;
import modules.source.execution_trace.impl.SeLogger;
import modules.source.execution_trace.impl.SeLoggerStub;
import modules.source.structure.StructureAnalyzer;
import org.refactoringminer.api.Refactoring;
import utils.exception.FinishException;
import utils.exception.NoParentsException;
import utils.setting.SettingManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestUtilities {
    public static StructureAnalyzer getStructureAnalyzer(String project, String commitId) throws NoParentsException {
        return getStructureAnalyzer(project, commitId, false);
    }
    public static StructureAnalyzer getStructureAnalyzer(String project, String commitId, boolean parent) throws NoParentsException {
        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/test/");
        gitX.checkout(commitId);
        List<Refactoring> refactorings = RefactoringMinerController.getRefactoringInstanceAtCommit(gitX.getRepo(), commitId);
        Commit commit = gitX.getCommit();

        BuildToolController mavenX = new MavenController(gitX);
        StructureAnalyzer analyzerX = new StructureAnalyzer(mavenX, parent);
        analyzerX.scan();
        analyzerX.flagChange(commit);
        analyzerX.setRefactoring(refactorings);
        return analyzerX;
    }

    public static AbstractJunitTestResult getResult(String project, String commitId) throws NoParentsException, FinishException {
        RefactoringEffortAnalyzerStraight analyzer = getAnalyzer(project, commitId);
        analyzer.analyze();
        return analyzer.getResult();
    }
    public static RefactoringEffortAnalyzerStraight getAnalyzer(String project, String commitId) throws NoParentsException, FinishException {
        Registry registry = new StraightRegistry();
        registry.commitId = commitId;
        registry.project = project;
        RefactoringEffortAnalyzerStraight analyzer = new RefactoringEffortAnalyzerStraightStub(registry);
        return analyzer;
    }


    public static SeLogger getSeLogger(String project, String commitId) throws NoParentsException {
        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/x/");
        GitController gitX_1 = new GitController(sm, "/x_1/");
        gitX.checkout(commitId);
        MavenController mc = new MavenController(gitX);
        SeLogger seLogger = new SeLoggerStub(mc);
        return seLogger;
    }

    public static ImpactAnalysisService getService(String project, String commitId) throws NoParentsException {
        SettingManager sm = new SettingManager(new String[]{project});
        ImpactAnalysisService service = new ImpactAnalysisService(sm);
        Registry r = new IARegistry(project, commitId);
        service.setRegistry(r);
        service.analyze();
        return service;
    }
    public static Refactoring getRefactoring(String project, String commitId, Integer hash) {
        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "x");//"repos/"+sm.getProject().name+
        List<Refactoring> refactoringResults = RefactoringMinerController.getRefactoringInstanceAtCommit(gitX.getRepo(), commitId);
        for(Refactoring r: refactoringResults){
            if(r.hashCode() == hash){
                return r;
            }
        }
        return null;
    }
    public static Set<String> transformStringListFromRefactoring(Set<Refactoring2> refs){
        Set<String> set = new HashSet<>();
        for(Refactoring2 r: refs){
            set.add(r.refactoring.getRefactoringType().getDisplayName());
        }
        return set;
    }


}
