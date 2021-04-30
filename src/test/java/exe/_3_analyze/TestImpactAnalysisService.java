package exe._3_analyze;

import beans.other.RefactoringForDatabase;
import beans.refactoring.Refactoring2;
import beans.source.MethodDefinition;
import beans.source.TestMethodDefinition;
import beans.trace.ExecutionTrace;
import modules.source.structure.StructureAnalyzer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.refactoringminer.api.Refactoring;
import util.TestUtilities;
import utils.db.Dao;
import utils.exception.FinishException;
import utils.exception.NoParentsException;

import java.util.*;

@Ignore//This test needs Database in lab
public class TestImpactAnalysisService {
    @BeforeClass
    public static void before(){
        Dao.profileName = "test";
    }

    @Ignore
    @Test
    public void N001() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "53d2c478232a414a89b9642de81b718d597c44fb";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;

        //check start
        System.out.println("analyzer.getTestSignature(): "+analyzer.getTestSignature());
        for(String s: analyzer.getTestSignature()){
            TestMethodDefinition test = analyzer.getTestMethod(s);
            int i = 1;
            for(Set<Refactoring2> set: test.directRefactorings.values()){
                if(i==5){
                    Assert.assertEquals(set.size(),1);
                    for(Refactoring2 r: set){
                        Assert.assertEquals(r.refactoring.getRefactoringType().getDisplayName(),"Change Return Type");
                    }
                }
                i++;
            }
        }
    }




    @Ignore
    @Test
    public void N002() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "53d2c478232a414a89b9642de81b718d597c44fb";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        //TODO: Assert
    }



    @Ignore
    @Test
    public void layer1Check() throws Exception {
        String commitId = "b6303ad968a44b9b8191fc4c2af03b44f9bd4d62";
        String project = "TestEffortEstimationTutorial";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        List<String> targets = Arrays.asList("src/test/java/functions/distributions/Calculator2Test.java;testPower_N001#", "src/test/java/functions/distributions/Calculator2Test.java;testPower_N002#");
        for(String sig: targets){
            TestMethodDefinition md1 = analyzer.getTestMethod(sig);
            for(Integer key: md1.directRefactorings.keySet()){
                Set<Refactoring2> refs = md1.directRefactorings.get(key);
                for(Refactoring2 r: refs){
                    Assert.assertEquals(1, (int) r.layer);
                }
            }
        }
    }

    @Ignore
    @Test
    public void layer2CheckOutside() throws Exception {
        String commitId = "3e515c0aecf605041b4297d28726e8728149aae8";
        String project = "TestEffortEstimationTutorial";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        List<String> targets = Arrays.asList("src/test/java/functions/CalculatorTest.java;testLayer_N001#");
        TestMethodDefinition md1 = analyzer.getTestMethod(targets.get(0));
        Set<Refactoring2> refs = md1.directRefactorings.get(68);
        System.out.println(refs);
        Assert.assertEquals(1, refs.size());
        for(Refactoring2 r: refs){
            System.out.println(r.refactoring.toString());
            Assert.assertEquals(2, (int) r.layer);
            Assert.assertEquals("Rename Method", r.refactoring.getName());
        }
    }
    @Ignore
    @Test
    public void layer2CheckInside() throws Exception {
        String commitId = "3d379fde8fc7e1de88155ff05c8b8a1f9a8b108e";
        String project = "TestEffortEstimationTutorial";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        List<String> targets = Arrays.asList("src/test/java/functions/CalculatorTest.java;testLayer_N002#");
        TestMethodDefinition md1 = analyzer.getTestMethod(targets.get(0));
        Set<Refactoring2> refs = md1.directRefactorings.get(80);
        System.out.println(refs);
        Assert.assertEquals(1, refs.size());
        for(Refactoring2 r: refs){
            System.out.println(r.refactoring.toString());
            Assert.assertEquals(2, (int) r.layer);
            Assert.assertEquals("Change Variable Type", r.refactoring.getName());
        }
    }
    @Ignore
    @Test
    public void refactoringsCheck1() throws Exception {
        String commitId = "b6303ad968a44b9b8191fc4c2af03b44f9bd4d62";
        String project = "TestEffortEstimationTutorial";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        String target = "src/test/java/functions/distributions/Calculator2Test.java;testPower_N002#";
        TestMethodDefinition tmd1 = analyzer.getTestMethod(target);
        Assert.assertEquals(1, tmd1.directRefactorings.get(20).size());
        Set<Refactoring2> ref = tmd1.directRefactorings.get(20);
        for (Refactoring2 r2 : ref) {
            Integer hash = r2.refactoring.hashCode();
            Refactoring r = TestUtilities.getRefactoring(project, commitId, hash);
            Assert.assertEquals("Change Parameter Type", r.getName());
            break;
        }
        tmd1.directRefactorings.remove(20);
        for (Set<Refactoring2> a : tmd1.directRefactorings.values()) {
            Assert.assertEquals(0, a.size());
        }

    }


    @Ignore
    @Test
    public void refactoringsCheck2() throws Exception {

        String commitId = "b6303ad968a44b9b8191fc4c2af03b44f9bd4d62";
        String project = "TestEffortEstimationTutorial";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        String target = "src/test/java/functions/distributions/Calculator2Test.java;testPower_N001#";
        TestMethodDefinition tmd1 = analyzer.getTestMethod(target);

        Assert.assertEquals(1, tmd1.directRefactorings.get(13).size());
        Set<Refactoring2> ref = tmd1.directRefactorings.get(13);
        for(Refactoring2 r2: ref){
            Integer hash = r2.refactoring.hashCode();
            Refactoring r = TestUtilities.getRefactoring(project, commitId, hash);
            Assert.assertEquals("Change Parameter Type", r.getName());
            break;
        }
        tmd1.directRefactorings.remove(13);
        for (Set<Refactoring2> a: tmd1.directRefactorings.values()){
            Assert.assertEquals(0, a.size());
        }
    }

    @Ignore
    @Test
    public void refactoringsCheck3() throws NoParentsException, FinishException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "dd16ecf9e89a2a5d9914cc4fef0006c9219a10bf";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        MethodDefinition md1 = analyzer.getTestMethod("src/test/java/functions/distributions/Calculator2Test.java;testPower_N002#");
        MethodDefinition md2 = analyzer.getTestMethod("src/test/java/functions/distributions/Calculator2Test.java;testPower_N001#");
    }

    @Ignore
    @Test
    public void annotationMapping() throws Exception {
        String project = "commons-text";
        String commitId = "61cbf0afe04d86a546e7094513328c9f7a7363ae";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        List<String> methods = analyzer.getSignatures("src/main/java/org/apache/commons/text/translate/NumericEntityUnescaper.java");
        for(String m: methods){
            System.out.println(m);
        }

    }

    @Ignore
    @Test
    public void values() throws Exception {
        String project = "commons-text";
        String commitId = "61cbf0afe04d86a546e7094513328c9f7a7363ae";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        MethodDefinition method = analyzer.getMethod("src/main/java/org/apache/commons/text/lookup/DefaultStringLookup.java;values#");
        Assert.assertNull(method);

    }

    @Ignore
    @Test
    public void setUpBug() throws Exception {
        String project = "commons-text";
        String commitId = "165c8e60fbc9c6a54f2c516774af4258ca9d0f52";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        MethodDefinition method = analyzer.getMethod("src/test/java/functions/distributions/Calculator2Test.java;testPower_N002#");
        Assert.assertNull(method);
    }

    @Ignore
    @Test
    public void testChangeVariableType() throws Exception {
        String project = "TestEffortEstimationTutorial";
        String commitId = "bf8d6ad0d1dae2e407680db01db6e16884559e29";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        TestMethodDefinition method = analyzer.getTestMethod("src/test/java/functions/CalculatorTest.java;testAdd_N001#");
        Assert.assertNotNull(method);
        Set<Refactoring2> refs = method.directRefactorings.get(11);
        Assert.assertEquals(1, refs.size());
        Assert.assertEquals("Change Variable Type", ((Refactoring2) refs.toArray()[0]).refactoring.getName());

    }

    @Ignore
    @Test
    public void testLayer2() throws Exception {
        String project = "TestEffortEstimationTutorial";
        String commitId = "0f16fa29d42bbb0e2a4f0357b770b238de106a96";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        TestMethodDefinition method = analyzer.getTestMethod("src/test/java/functions/distributions/Calculator2Test.java;testPower_N001#");
        for(Set<Refactoring2> s: method.directRefactorings.values()){
            for(Refactoring2 r: s){
                System.out.println(r.refactoring.toString());
            }
        }
        List<Refactoring2> refs1 = new ArrayList<>(method.directRefactorings.get(11));
        Assert.assertEquals(0, refs1.size());

        List<Refactoring2> refs2 = new ArrayList<>(method.directRefactorings.get(12));
        Assert.assertEquals(0, refs2.size());

        List<Refactoring2> refs3 = new ArrayList<>(method.directRefactorings.get(13));
        Assert.assertEquals(1, refs3.size());
        Assert.assertEquals((Integer) 1, refs3.get(0).layer);
        Assert.assertEquals("Move Class", refs3.get(0).refactoring.getName());

        List<Refactoring2> refs4 = new ArrayList<>(method.directRefactorings.get(14));
        Assert.assertEquals(1, refs4.size());
        Assert.assertEquals((Integer) 1, refs4.get(0).layer);
        Assert.assertEquals("Move Class", refs4.get(0).refactoring.getName());
    }

    @Ignore
    @Test
    public void testRefactoringInTest() throws NoParentsException {
        String commitId = "d37eb1ecdc483a14115a624198adaaaf4d2ad612";
        String project = "TestEffortEstimationTutorial";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        List<String> targets = Arrays.asList("src/test/java/functions/CalculatorTest.java;testAdd_N001#");
        TestMethodDefinition md1 = analyzer.getTestMethod(targets.get(0));
        Set<Refactoring2> refs = md1.directRefactorings.get(21);
        List<Refactoring2> list = new ArrayList<>(refs);
        Assert.assertEquals(0, list.size());
//        仕様変更
//        Assert.assertEquals(2, list.size());
//        Refactoring2 r1=list.get(0);
//        Assert.assertEquals(1, (int)r1.layer);
//        Assert.assertEquals("Add Method Annotation", r1.refactoring.getName());
//
//        Refactoring2 r2=list.get(1);
//        Assert.assertEquals(1, (int)r2.layer);
//        Assert.assertEquals("Change Variable Type", r2.refactoring.getName());
    }

    @Ignore//use local database
    @Test
    public void testJavapoetN001() throws Exception {
        String project = "javapoet";
        String commitId = "e8cd8f4881897b00043c8114aac7537ab11c4b16";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        Collection<String> signatures =  analyzer.getTestSignature();
        for(String sig : signatures){
            System.out.println(sig);
            TestMethodDefinition method = analyzer.getTestMethod(sig);
            if(!(method == null)){
                Map<Integer, Set<Refactoring2>> refs = method.directRefactorings;
                if(refs.isEmpty()){
                    continue;
                }
                System.out.println(refs);
                for(Integer line : refs.keySet()){
                    System.out.println(line);
                    System.out.println(refs.get(line));
                }
            }
        }
    }

    @Ignore//use local database
    @Test
    public void testChangeParameter() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "8fccdf79ce37cc42a738127f259c80d1cfe65542";

        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        List<String> targets = Arrays.asList("src/test/java/functions/CalculatorTest.java;testLayer_N001#");
        TestMethodDefinition md1 = analyzer.getTestMethod(targets.get(0));
        Set<Refactoring2> refs = md1.directRefactorings.get(76);
        List<Refactoring2> list = new ArrayList<>(refs);
        Assert.assertEquals(3, list.size());
        Refactoring2 r1 = list.get(0);
        Assert.assertEquals("Change Variable Type", r1.refactoring.getName());
        Refactoring2 r2 = list.get(1);
        Assert.assertEquals("Change Attribute Type", r2.refactoring.getName());
        Refactoring2 r3 = list.get(2);
        Assert.assertEquals("Change Variable Type", r3.refactoring.getName());

    }
    @Ignore//TODO: Refactoring Miner cannot handle this
    @Test
    public void testAddAttributeAnnotation() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "764abebed72cbdd7ee15ef7aba7ad769cbfba3d3";

        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        List<String> targets = Arrays.asList("src/test/java/functions/CalculatorTest.java;testLayer_N001#");
        TestMethodDefinition md1 = analyzer.getTestMethod(targets.get(0));
        Set<Refactoring2> refs = md1.directRefactorings.get(76);
        List<Refactoring2> list = new ArrayList<>(refs);
        Assert.assertEquals(1, list.size());
        Refactoring2 r1 = list.get(0);
        Assert.assertEquals("Add Attribute Annotation", r1.refactoring.getName());
    }

    @Ignore //too much time
    @Test
    public void testInvokedOrder() throws NoParentsException {
        String project = "joda-beans";
        String commitId = "5a7dae78d2bffa1f8d373a5ea359bf555d9c704c";
        String signature = "src/test/java/org/joda/beans/test/TestBeanAssert.java;test_bean_twoFields#";
        Dao<ExecutionTrace> pathDao = new Dao<>(ExecutionTrace.class);
        pathDao.init();
        pathDao.setWhere("project", project);
        pathDao.setWhere("commit_id", commitId);
        pathDao.setWhere("signature", signature);
        pathDao.setWhere("is_cross", false);
        //pathDao.setOrderBy("invoked_order");
        List<ExecutionTrace> list = pathDao.select();

        ExecutionTrace line103 = list.get(7);
        System.out.println(line103);
        for(String pass :line103.passes){
            System.out.println(pass);
        }
        pathDao.close();

    }
    @Ignore //too much time
    @Test
    public void testRefactoredCommit() throws NoParentsException {
        System.out.println("a");
        String project = "jsoup";
        String commitId = "f9307ec96a894191e5d3782601ddb49fbfc53ea6";
        Dao<RefactoringForDatabase> refactoringDao = new Dao<>(RefactoringForDatabase.class);
        refactoringDao.init();
        refactoringDao.setWhere("project", project);
        refactoringDao.setWhere("commit_id", commitId);
        List<RefactoringForDatabase> refactorings =  refactoringDao.select();
        System.out.println(refactorings);
        Boolean isRefactoredCommit = refactorings.isEmpty();
        System.out.println(isRefactoredCommit);
        refactoringDao.close();

    }
    @Ignore //too much time
    @Test
    public void testJsoupExtractSubClass() throws NoParentsException {
        String project = "jsoup";
        String commitId = "f71712ba5d28df09c9a5b6e3c8a37f05f5e3372d";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        List<String> targets = Arrays.asList("src/test/java/org/jsoup/nodes/NodeTest.java;ownerDocument#");
        TestMethodDefinition md1 = analyzer.getTestMethod(targets.get(0));
        Set<Refactoring2> refs = md1.directRefactorings.get(154);
        List<Refactoring2> list = new ArrayList<>(refs);
        Assert.assertEquals(1, list.size());
        Refactoring2 r1 = list.get(0);
        Assert.assertEquals("Extract Subclass", r1.refactoring.getName());
    }

    @Ignore
    @Test //use local database
    public void testJsoupwhoMade() throws NoParentsException {
        String project = "jsoup";
        String commitId = "426ffe7870b937ef9cfa25ebe20e7a478493da99";
        ImpactAnalysisService service = TestUtilities.getService(project, commitId);
        StructureAnalyzer analyzer = service.structureAnalyzerX;
        List<String> targets = Arrays.asList("src/test/java/org/jsoup/nodes/ElementTest.java;testNextElementSiblings#");
        TestMethodDefinition md1 = analyzer.getTestMethod(targets.get(0));
        Set<Refactoring2> refs = md1.directRefactorings.get(154);
        List<Refactoring2> list = new ArrayList<>(refs);
        Assert.assertEquals(1, list.size());
        Refactoring2 r1 = list.get(0);
        Assert.assertEquals("Extract Subclass", r1.refactoring.getName());
    }



}