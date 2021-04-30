package exe._3_analyze;

import beans.other.run.Registry;
import beans.test.result.StraightJunitTestResult;
import beans.test.rowdata.test.TestInfoStraight;
import beans.trace.ExecutionTrace;
import modules.refactoring.trace.ImpactAnalyzerController;
import modules.refactoring.trace.RenameSpecifier;
import modules.source.structure.StructureAnalyzer;
import utils.exception.NoParentsException;
import utils.setting.SettingManager;

import java.util.List;

public class ImpactAnalysisService {
    SettingManager sm;
    public Registry registry;
    public StructureAnalyzer structureAnalyzerX, structureAnalyzerX_1;

    public void setRegistry(Registry r){
        this.registry = r;
    }
    public ImpactAnalysisService(SettingManager sm) {
        this.sm = sm;
    }

    /**
     * Detect refactorings during a test exercise
     * @throws NoParentsException
     */
    public void analyze() throws NoParentsException {
        //X
        ImpactAnalyzerController impactAnalyzerX = new ImpactAnalyzerController(sm, registry.commitId);
        List<StraightJunitTestResult> testResultsX = TeaDBUtil.getTests(registry.project, impactAnalyzerX.getCommitId());
        assert testResultsX.size()==1;
        structureAnalyzerX = conductImpactAnalysis(impactAnalyzerX, testResultsX);
        //X_1
        ImpactAnalyzerController impactAnalyzerX_1 = impactAnalyzerX.getImpactAnalyzerX_1();
        List<StraightJunitTestResult> testResultsX_1 = TeaDBUtil.getTests(registry.project, impactAnalyzerX_1.getCommitId());
        assert testResultsX_1.size()==1;
        structureAnalyzerX_1 = conductImpactAnalysis(impactAnalyzerX_1, testResultsX_1);
        //Link
        RenameSpecifier.linkMethods(structureAnalyzerX_1, structureAnalyzerX, impactAnalyzerX.getRefactorings());
    }


    /**
     * Detect refactorings for each test exercise
     * @throws NoParentsException
     */
    public static StructureAnalyzer conductImpactAnalysis(ImpactAnalyzerController impactAnalyzer, List<StraightJunitTestResult> testResults) throws NoParentsException {
        for(StraightJunitTestResult tr: testResults){
            for(Object t: tr.getTestResults()){
                TestInfoStraight tis = (TestInfoStraight)t;
                List<ExecutionTrace> paths = TeaDBUtil.getPath(tr, tis);
                impactAnalyzer.analyze(tis, paths);
            }
        }
        return impactAnalyzer.getStructureAnalyzer();
    }

}
