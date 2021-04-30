package modules.source.static_.mapping;


import beans.source.MethodDefinition;
import exe._3_analyze.ImpactAnalysisService;
import modules.refactoring.trace.ImpactAnalyzerController;
import modules.source.structure.StructureAnalyzer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.refactoringminer.api.Refactoring;
import util.TestUtilities;
import utils.setting.SettingManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestRefactoringSpecifier {


    @Test
    public void checkChangeLine() throws Exception {
        String target = "b6303ad968a44b9b8191fc4c2af03b44f9bd4d62";
        Map<String, Map<Integer, Boolean>> answers = new HashMap<>();
        answers.put("src/main/java/functions/Calculator.java;plus#Double", new HashMap<Integer, Boolean>() {
            {put(11, false);
                put(12, true);
                put(13, false);
                put(14, false);
                put(15, false);
                put(16, false);
            }});

        SettingManager sm = new SettingManager(new String[]{"TestEffortEstimationTutorial"});

        ImpactAnalyzerController rs = new ImpactAnalyzerController(sm, target);
        //TODO: 処理がイル
        StructureAnalyzer structure = rs.getStructureAnalyzer();
        for(String key: answers.keySet()){
            Map<Integer, Boolean> answer = answers.get(key);
            MethodDefinition md = structure.getMethod(key);
            System.out.println(md.changedLines);
            for(Integer lineNo: answer.keySet()){
                Boolean isChange = md.changedLines.get(lineNo);
                System.out.println(lineNo+": "+isChange+"-"+answer.get(lineNo));
                Assert.assertEquals(answer.get(lineNo), isChange);
            }
        }
    }
    @Ignore
    @Test
    public void testFlagRefactoring() throws Exception {
        String commitId = "b6303ad968a44b9b8191fc4c2af03b44f9bd4d62";
        String project = "TestEffortEstimationTutorial";
        Map<String, Map<Integer, String>> refactoringAnswers = new HashMap<>();
        refactoringAnswers.put("src/main/java/functions/distributions/Calculator2.java;power#Double", new HashMap<Integer, String>() {
            {put(8, "Change Parameter Type");
                put(9, null);
                put(10, null);
                put(11, null);
            }});
        Map<String, Map<Integer, Integer>> Layeranswers = new HashMap<>();
        Layeranswers.put("src/main/java/functions/distributions/Calculator2.java;power#Double", new HashMap<Integer, Integer>() {
            {put(8, 0);
                put(9, null);
                put(10, null);
                put(11, null);
            }});
        Map<String, Map<Integer, String>> whoMadeanswers = new HashMap<>();
        whoMadeanswers.put("src/main/java/functions/distributions/Calculator2.java;power#Double", new HashMap<Integer, String>() {
            {put(8, "src/main/java/functions/distributions/Calculator2.java;power#Double");
                put(9, null);
                put(10, null);
                put(11, null);
            }});

        ImpactAnalysisService service = TestUtilities.getService(project, commitId);

        StructureAnalyzer structure = service.structureAnalyzerX;
        for(String key: refactoringAnswers.keySet()){
                Map<Integer, String> refactoringAnswer = refactoringAnswers.get(key);
                Map<Integer, Integer> layerAnswer = Layeranswers.get(key);
                Map<Integer, String> whoMadeAnswer = whoMadeanswers.get(key);
                MethodDefinition md = structure.getMethod(key);
                for(Integer lineNo: refactoringAnswer.keySet()){
                    Set<Refactoring> refactoring = md.inherentRefactorings.get(lineNo);
                    for(Refactoring r2: refactoring){
                        System.out.println(lineNo+": "+r2.getName()+"-"+refactoringAnswer.get(lineNo));
                        Assert.assertEquals(refactoringAnswer.get(lineNo), r2.getName());
                        //TODO: check layer
                        //TODO: check whoMadeAnswer(methods)
                    }
                }
            }
        }


}
