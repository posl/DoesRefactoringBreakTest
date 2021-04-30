package exe._3_analyze;

import beans.other.run.Registry;
import beans.other.run.StraightRegistry;
import beans.refactoring.Refactoring2;
import beans.source.TestMethodDefinition;
import beans.test.result.AbstractJunitTestResult;
import beans.test.rowdata.TestInfo;
import beans.trace.ExecutionTrace;
import modules.build.BuildRunner;
import modules.build.BuildRunnerStraight;
import modules.git.GitController;
import modules.refactoring.trace.ImpactAnalyzerController;
import modules.source.structure.StructureAnalyzer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import util.TestUtilities;
import utils.exception.*;
import utils.setting.SettingManager;
import utils.trace.MyTraceUtils;

import java.io.IOException;
import java.util.*;
@Ignore//This takes too much time to run
public class TestDetectRefactoring {
    public static StructureAnalyzer run(Registry registry, String signature) {
        return run(registry, signature, false);
    }
    public static StructureAnalyzer run(Registry registry, String signature, boolean x_1){
        SettingManager sm = new SettingManager(new String[]{registry.project});
        GitController git = new GitController(sm, "/test/");
        BuildRunner br;
        try {
            br = new BuildRunnerStraight(sm, git, registry.commitId);
            AbstractJunitTestResult tr = br.run();
            List<ExecutionTrace> traces = new ArrayList<>();

            MyTraceUtils.setTrace(traces, registry, signature, br.getPassedLines());
            for(ExecutionTrace t: traces){
                System.out.println(t.signature);
                for(String p: t.passes){
                    System.out.println("  "+ p);
                }
            }
            ImpactAnalyzerController impactAnalyzerX = new ImpactAnalyzerController(sm, registry.commitId);
            TestInfo tis = tr.getResult(signature);
            impactAnalyzerX.analyze(tis, traces);
            if(x_1) {
                ImpactAnalyzerController impactAnalyzerX_1 = impactAnalyzerX.getImpactAnalyzerX_1();
                impactAnalyzerX_1.analyze(tis, traces);
                return impactAnalyzerX_1.getStructureAnalyzer();
            }
            return impactAnalyzerX.getStructureAnalyzer();

        } catch (TestUnknownFailureException e) {
            e.printStackTrace();
        } catch (NoTargetBuildFileException e) {
            e.printStackTrace();
        } catch (NoParentsException e) {
            e.printStackTrace();
        } catch (NoSureFireException e) {
            e.printStackTrace();
        } catch (DependencyProblemException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProductionProblemException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }
    private void show(TestMethodDefinition m) {
        for(Integer i: m.directRefactorings.keySet()){
            System.out.println("Line: "+i);
            for(Refactoring2 r: m.directRefactorings.get(i)){
                System.out.println("  "+r.refactoring.getRefactoringType());
            }
        }
    }
    private void verify(TestMethodDefinition m, Map<Integer, String> answers) {
        for(Integer i: answers.keySet()){
            String ans = answers.get(i);
            Set<String> refs = TestUtilities.transformStringListFromRefactoring(m.directRefactorings.get(i));
            System.out.println("refs: "+ refs);
            System.out.println("ans: "+ans);
            Assert.assertTrue(refs.contains(ans));
        }
    }

    @Test
    public void test1_ExtractMethod() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "de322649ec76bfcf6266cdc63fd9642161b9b511";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Extract Method");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test2_InlineMethod() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "e05d63f9268ccead3cf60831e3670b00331c1c6c";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Inline Method");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test3_RenameMethod() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "2667ce30f5451236b9b3c5e2716256b6ebf40d15";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Rename Method");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test4_MoveMethod() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "02fbedb47395691e1c8c7a730978cb78d6f61aab";
        String signature = "src/test/java/functions/CalculatorTest.java;testaho_N002#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(28, "Move Method");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test5_MoveAttribute() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "0d094338bed96379408e43adae2ec3f13cd24650";
        String signature = "src/test/java/functions/CalculatorTest.java;testCalc3_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(40, "Move Attribute");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test6_PullUpMethod() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "8b9ac832d01db3e14b916e780edcdb86650b58f1";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Pull Up Method");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test7_PullUpAttribute() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "8b9ac832d01db3e14b916e780edcdb86650b58f1";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Pull Up Attribute");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test8_PushDownMethod() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "919e0703cb7e272bec15d1c4ed73bb22d7cbd802";
        String signature = "src/test/java/functions/CalculatorTest.java;testaho_N002#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(29, "Push Down Method");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test9_PushDownAttribute() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "8740b40fbbfcbadd5358a11bbfa2f9acbee77739";
        String signature = "src/test/java/functions/CalculatorTest.java;testCalc2_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(33, "Push Down Attribute");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test10_ExtractSuperclass() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "8b9ac832d01db3e14b916e780edcdb86650b58f1";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Extract Superclass");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test11_ExtractInterface() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "6e5ab67747cfbb947e475848320930c9e36277ad";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Extract Interface");
        //###############################

        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature, true);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test12_MoveClass() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "0473d70c138376b8b4f0058daf6f8bff848cf186";
        String signature = "src/test/java/functions/CalculatorTest.java;testCalc3_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(40, "Move Class");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test13_RenameClass() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "558c0adeae913dd70274aff5bd429e3afec39803";
        String signature = "src/test/java/functions/CalculatorTest.java;testCalcExtra_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(40, "Rename Class");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    
    @Test
    public void test14_ExtractAndMoveMethod() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "3a5530ccc6c9d99fbd4a89be7a85d7e6c66f00a5";
        String signature = "src/test/java/functions/CalculatorTest.java;testCalc2_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(34, "Extract And Move Method");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test15_ChangePackage() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "d4c034c7fb99dbee19d83396f6228a2dee489e7d";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(16, "Change Package");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test16_MoveAndRenameClass() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "ad208f6879048c14cd3591806963b8f5d8781276";
        String signature = "src/test/java/functions/CalculatorTest.java;testCalcExtra_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(40, "Move And Rename Class");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test17_ExtractClass() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "23538e589183e66c098a7fb1e0975b6dfd7ac390";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Extract Class");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature, true);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test18_ExtractSubclass() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "f754565cd5c70788378cf8e7810057bd3cf746a9";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Extract Subclass");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test19_ExtractVariable() throws NoParentsException {
       String project = "TestEffortEstimationTutorial";
       String commitId = "fa39bdd59f27c5c49eae0e22880346c1a963bf36";
       String signature = "src/test/java/functions/CalculatorTest.java;testaho_N002#";
       //###############################
       Map<Integer, String> answers = new HashMap<>();
       answers.put(29, "Extract Variable");
       //###############################
       StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
       TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
       verify(m, answers);
   }
   @Test
   public void test20_InlineVariable() throws NoParentsException {
       String project = "TestEffortEstimationTutorial";
       String commitId = "9093b3931dea00c099607d87bc47c4932e8beb49";
       String signature = "src/test/java/functions/CalculatorTest.java;testaho_N002#";
       //###############################
       Map<Integer, String> answers = new HashMap<>();
       answers.put(29, "Inline Variable");
       //###############################
       StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
       TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
       verify(m, answers);
   }
    @Test
    public void test21_ParameterizeVariable() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "3e846ffcddccead218af11bda31f3f16ba228435";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Parameterize Variable");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }

    @Test
    public void test22_RenameVariable() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "7f5fefd37ad1bb8a3786940c128e45bf96ed32ce";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Rename Variable");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test23_RenameParameter() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "2e0bdb9ccd6688176ee6977776612a4a7396c7e7";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Rename Parameter");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }



    @Test
    public void test24_RenameAttribute() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "2e0bdb9ccd6688176ee6977776612a4a7396c7e7";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Rename Attribute");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test25_MoveAndRenameAttributeX() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "89196baddfe20532222e86c9b7875f9440d3416c";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Move And Rename Attribute");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        show(m);

        verify(m, answers);
    }
    @Test
    public void test25_MoveAndRenameAttributeX_1() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "89196baddfe20532222e86c9b7875f9440d3416c";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Move And Rename Attribute");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature, true);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        show(m);

        verify(m, answers);
    }
    @Test
    public void test26_ReplaceVariableWithAttributeX_1() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "440e730e840e4e0b7f94d409260757417d92b6ef";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Replace Variable With Attribute");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature, true);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        show(m);
        verify(m, answers);
    }
    @Test
    public void test26_ReplaceVariableWithAttributeX() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "440e730e840e4e0b7f94d409260757417d92b6ef";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Replace Variable With Attribute");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        show(m);
        verify(m, answers);
    }
    @Test
    public void test27_ReplaceVariable() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "440e730e840e4e0b7f94d409260757417d92b6ef";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Replace Variable");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        show(m);
        verify(m, answers);
    }

    @Ignore
    @Test
   public void test31_SplitVariable() throws NoParentsException {
       String project = "TestEffortEstimationTutorial";
       String commitId = "30ff97ee53aafcd195a041939c722c9a3ab555b3";
       String signature = "src/test/java/functions/CalculatorTest.java;testaho_N002#";
       //###############################
       Map<Integer, String> answers = new HashMap<>();
       answers.put(29, "Split Variable");
       //###############################
       StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
       TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
       verify(m, answers);
   }
    @Test
    public void test34_ChangeVariableType() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "04b2102c2011325df256ff35d1b765295dc5882f";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Change Variable Type");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test35_ChangeParameterType() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "04b2102c2011325df256ff35d1b765295dc5882f";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Change Parameter Type");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test36_ChangeReturnType() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "3491cdd804fcf24b74c50cad64949c9f9f57ccc7";
        String signature = "src/test/java/functions/CalculatorTest.java;testaho_N002#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(28, "Change Return Type");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test37_ChangeAttributeType() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "c2dc484c69b059a9a970f5273e171bf2917db678";
        String signature = "src/test/java/functions/CalculatorTest.java;testCalc3_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(40, "Change Attribute Type");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }

    @Test
    public void test38_ExtractAttribute() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "f69e841e9c5be3a5a956cd3282693ea39cf2faa7";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Extract Attribute");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test39_MoveAndRenameMethod() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "b49f596df425b59fe3f8da5c4fe774b37a29715b";
        String signature = "src/test/java/functions/CalculatorTest.java;testCheck1_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(46, "Move And Rename Method");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test40_MoveAndInlineMethod() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "73e84b3fcbe4a6e46ab0d1dd43873f14afc40c8d";
        String signature = "src/test/java/functions/CalculatorTest.java;testCheck1_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(46, "Move And Inline Method");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test41_AddMethodAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "3e4f5b450756de9ab9f7eced740d038c08d28277";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Add Method Annotation");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test42_RemoveMethodAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "582eb61b9b001f290590f03cbac65ffb39d6875e";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Remove Method Annotation");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test43_ModifyMethodAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "e60aab03ffeaf1e769da4ca58d2b70296b4785dc";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Remove Method Annotation");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test44_AddAttributeAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "ce12fc6deb56d6694a8290c03572f6f0d1f76309";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Modify Annotation");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        System.out.println(m.directRefactorings);
        verify(m, answers);
    }
    @Test
    public void test45_RemoveAttributeAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "e1fa7e4554c77151c7ecaee0a8c97cf07f195efe";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Remove Attribute Annotation");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        System.out.println(m.directRefactorings);
        verify(m, answers);
    }
    @Ignore
    @Test //TODO: This fails because of RMiner
    public void test46_ModifyAttributeAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "d42e6f17cbac7489faf465afc2364ec3d9ce2f9c";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Modify Attribute Annotation");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        System.out.println(m.directRefactorings);
        verify(m, answers);
    }
    @Test
    public void test47_AddClassAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "a5cc33012fb28e4366c82cb019f9f8d8d9380e5a";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Add Class Annotation");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        System.out.println(m.directRefactorings);
        verify(m, answers);
    }
    @Test
    public void test48_RemoveClassAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "0dbb25e7d119f6bc10b601a2df2dde5e18adb4ab";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Remove Class Annotation");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        System.out.println(m.directRefactorings);
        verify(m, answers);
    }
    @Ignore
    @Test //This fails because of Rminer bug
    public void test49_ModifyClassAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "9830db75454fd0aa8ad517aeeeb6e0db1918dee1";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Modify Class Annotation");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        System.out.println(m.directRefactorings);
        verify(m, answers);
    }
    //RMiner cannot handle the following refactoring operations
    @Ignore
    @Test
    public void test50_AddParameterAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "81fd3e6807a0e67c6c51f69ed1e022918e547669";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Add Parameter Annotation");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        System.out.println(m.directRefactorings);
        verify(m, answers);
    }
    @Ignore
    @Test
    public void test51_RemoveParameterAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "0dbb25e7d119f6bc10b601a2df2dde5e18adb4ab";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Remove Parameter Annotation");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        System.out.println(m.directRefactorings);
        verify(m, answers);
    }
    @Ignore
    @Test
    public void test52_ModifyParameterAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "0dbb25e7d119f6bc10b601a2df2dde5e18adb4ab";
        String signature = "src/test/java/functions/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(18, "Modify Parameter Annotation");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        System.out.println(m.directRefactorings);
        verify(m, answers);
    }
    @Ignore // because of RMINER's version
    @Test
    public void test53_RemoveParameter() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "842e720b135593638a33bccac90c16997b69b336";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Add Parameter");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Test
    public void test54_RemoveParameter() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "bb322fd06a8c06662246748326100a99369341d4";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Remove Parameter");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
    @Ignore
    @Test
    public void test55_ReorderParameter() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "cc166937cf514be35546c53cfc036414a0adfbf6";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";
        //###############################
        Map<Integer, String> answers = new HashMap<>();
        answers.put(17, "Reorder Parameter");
        //###############################
        StructureAnalyzer structure = run(new StraightRegistry(project, commitId), signature);
        TestMethodDefinition m = (TestMethodDefinition) structure.getMethod(signature);
        verify(m, answers);
    }
}
