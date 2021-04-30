package modules.source.impact_analysis;

import beans.other.run.IARegistry;
import beans.refactoring.Refactoring2;
import beans.source.TestMethodDefinition;
import beans.test.result.StraightJunitTestResult;
import beans.test.rowdata.test.TestInfoStraight;
import exe._3_analyze.ImpactAnalysisService;
import exe._3_analyze.TeaDBUtil;
import modules.refactoring.trace.ImpactAnalyzerController;
import modules.source.structure.StructureAnalyzer;
import org.junit.Ignore;
import org.junit.Test;
import utils.exception.NoParentsException;
import utils.setting.SettingManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestImpactAnalyzer {


    public StructureAnalyzer commonX(SettingManager sm, IARegistry registry, String signature) throws NoParentsException {
        ImpactAnalyzerController impactAnalyzerX = new ImpactAnalyzerController(sm, registry.commitId);
        List<StraightJunitTestResult> testResultsX = TeaDBUtil.getTests(registry.project, impactAnalyzerX.getCommitId());
        assert testResultsX.size()==1;
        List<StraightJunitTestResult> testResults = new ArrayList<>();
        StraightJunitTestResult r = new StraightJunitTestResult();
        for(StraightJunitTestResult sjtr : testResultsX){
            r.project = sjtr.project;
            r.commitId = sjtr.commitId;
            r.results = new ArrayList<>();

            for(TestInfoStraight ti: sjtr.results){
                if (ti.signature.equals(signature)){
                    r.results.add(ti);
                    break;
                }
            }
            testResults.add(r);
            break;
        }
        StructureAnalyzer structureAnalyzerX = ImpactAnalysisService.conductImpactAnalysis(impactAnalyzerX, testResults);

        return structureAnalyzerX;
    }
    @Ignore//because this uses local db
    @Test
    public void testWhoIsNotNull() throws NoParentsException {
        String project = "jsoup";
        String commitId = "ea1fb65e9ff8eee82c4e379dc3236d09a5ab02e1";
        String testSignature = "src/test/java/org/jsoup/parser/HtmlParserTest.java;handlesMisnestedTagsBI#";
        SettingManager sm = new SettingManager(new String[]{project});
        IARegistry registry = new IARegistry(project, commitId);
        StructureAnalyzer analyzer = commonX(sm, registry, testSignature);
        TestMethodDefinition tmd = analyzer.getTestMethod(testSignature);
        for(Integer i: tmd.directRefactorings.keySet()){
            System.out.println(i);
            for(Refactoring2 r: tmd.directRefactorings.get(i)){
                System.out.println("  "+r.refactoring.getRefactoringType());
                System.out.println("  "+r.refactoring.rightSide());
                System.out.println("  "+r.whoMade);
                System.out.println("  "+r.refactoringHash);
                System.out.println("  "+r.layer);
                assertEquals("EXTRACT_ATTRIBUTE", r.refactoring.getRefactoringType().toString());
            }
        }
    }


}
