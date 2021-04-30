package modules.source.structure;

import beans.source.MethodDefinition;
import modules.build.controller.BuildToolController;
import modules.build.controller.maven.MavenController;
import modules.git.GitController;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import utils.exception.NoParentsException;
import utils.setting.SettingManager;

import java.beans.Transient;
import java.util.*;

import static util.TestUtilities.getStructureAnalyzer;

public class StructureAnalyzerTest {


    @Test
    public void checkGenerics() throws NoParentsException {
        String[] args = {"TestEffortEstimationTutorial"};
        SettingManager sm = new SettingManager(args);
        GitController gitX = new GitController(sm, "/x/");
        gitX.checkout("dc139e40f994b297d18227d21d9cd164751f1deb");
        BuildToolController mavenX = new MavenController(gitX);
        StructureAnalyzer analyzerX = new StructureAnalyzer(mavenX);
        analyzerX.scan();
        Assert.assertTrue(analyzerX.hasMethod("src/main/java/functions/Calculator.java;tmp#Object"));
    }

    @Test
    public void checkSignatures() throws NoParentsException {
        String[] args = {"TestEffortEstimationTutorial"};
        SettingManager sm = new SettingManager(args);
        GitController gitX = new GitController(sm, "/x/");
        gitX.checkout("cceeb8d5fec9bfc3b1014ccb5cb8ed962e3e572c");
        BuildToolController mavenX = new MavenController(gitX);
        StructureAnalyzer analyzerX = new StructureAnalyzer(mavenX);
        analyzerX.scan();
        Assert.assertEquals(7, analyzerX.getAllFiles().size());
        Assert.assertTrue(analyzerX.hasFile("src/test/java/functions/CalculatorTest.java"));
        Assert.assertTrue(analyzerX.hasFile("src/test/java/functions/distributions/Calculator2Test.java"));
        Assert.assertTrue(analyzerX.hasFile("src/main/java/OpenCloverController.java"));
        Assert.assertTrue(analyzerX.hasFile("src/main/java/functions/special/B.java"));
        Assert.assertTrue(analyzerX.hasFile("src/main/java/functions/Calculator.java"));
        Assert.assertTrue(analyzerX.hasFile("src/main/java/functions/distributions/Calculator2.java"));
        Assert.assertTrue(analyzerX.hasFile("src/main/java/CloverRead.java"));
    }

    @Test
    public void checkMethods() throws NoParentsException {
        String[] args = {"TestEffortEstimationTutorial"};
        SettingManager sm = new SettingManager(args);
        GitController gitX = new GitController(sm, "/x/");
        gitX.checkout("cceeb8d5fec9bfc3b1014ccb5cb8ed962e3e572c");
        BuildToolController mavenX = new MavenController(gitX);
        StructureAnalyzer analyzerX = new StructureAnalyzer(mavenX);
        analyzerX.scan();

        Assert.assertEquals(23, analyzerX.getSignatures("src/main/java/functions/Calculator.java").size());
        Assert.assertEquals(6, analyzerX.getSignatures("src/test/java/functions/CalculatorTest.java").size());
        Assert.assertEquals(4, analyzerX.getSignatures("src/test/java/functions/distributions/Calculator2Test.java").size());
        Assert.assertEquals(4, analyzerX.getSignatures("src/main/java/OpenCloverController.java").size());
        Assert.assertEquals(1, analyzerX.getSignatures("src/main/java/functions/special/B.java").size());
        Assert.assertEquals(3, analyzerX.getSignatures("src/main/java/functions/distributions/Calculator2.java").size());
        Assert.assertEquals(1, analyzerX.getSignatures("src/main/java/CloverRead.java").size());
    }

    @Test
    public void checkSubclass() throws NoParentsException {
        String[] args = {"TestEffortEstimationTutorial"};
        Map<String, List<String>> answers = new HashMap<>();
        answers.put("src/main/java/OpenCloverController.java",
                Arrays.asList(
                        "src/main/java/OpenCloverController.java;OpenCloverController#",
                        "src/main/java/OpenCloverController.java;getPath#String",
                        "src/main/java/OpenCloverController.java;getTestCaseInfo#String",
                        "src/main/java/OpenCloverController.java;setMap#"));
        answers.put("src/main/java/CloverRead.java",
                Collections.singletonList(
                        "src/main/java/CloverRead.java;main#String]"));
        answers.put("src/main/java/functions/Calculator.java",
                Arrays.asList(
                        "src/main/java/functions/Calculator.java;Calculator#",
                        "src/main/java/functions/Calculator.java;plus#Double",
                        "src/main/java/functions/Calculator.java;minus#Double",
                        "src/main/java/functions/Calculator.java;times#Double",
                        "src/main/java/functions/Calculator.java;divided#Double",
                        "src/main/java/functions/Calculator.java;aaa1#int",
                        "src/main/java/functions/Calculator.java;aaa2#double",
                        "src/main/java/functions/Calculator.java;aaa3#float",
                        "src/main/java/functions/Calculator.java;aaa5#short",
                        "src/main/java/functions/Calculator.java;aaa6#long",
                        "src/main/java/functions/Calculator.java;aaa4#boolean",
                        "src/main/java/functions/Calculator.java;aaa7#byte",
                        "src/main/java/functions/Calculator.java;aaa8#char",
                        "src/main/java/functions/Calculator.java;aaa9#byte]",
                        "src/main/java/functions/Calculator.java;aaa10#Integer]",
                        "src/main/java/functions/Calculator.java;aaa11#Integer]",
                        "src/main/java/functions/Calculator.java;aaa12#Object",
                        "src/main/java/functions/Calculator.java;getAnswer#",
                        "src/main/java/functions/Calculator.java;aho#",
                        "src/main/java/functions/Calculator.java;isNull#Double,Double",
                        "src/main/java/functions/Calculator.java;isStaticNull#Double,Double",
                        "src/main/java/functions/Calculator.java;aaa#Double,Double,Double,Double",
                        "src/main/java/functions/Calculator.java;main#String]",
                        "src/main/java/functions/Calculator.java$SubClass;SayHello#"
                ));


        SettingManager sm = new SettingManager(args);
        GitController gitX = new GitController(sm, "/x/");
        gitX.checkout("8c64ccc68f96a7abffe5de057c10f9e6554d0b73");
        BuildToolController mavenX = new MavenController(gitX);
        StructureAnalyzer analyzerX = new StructureAnalyzer(mavenX);
        analyzerX.scan();
        for(String key: answers.keySet()){
            List<String> ans = answers.get(key);
            List<String> act = analyzerX.getSignatures(key);
            Assert.assertEquals(ans.size(), act.size());
            System.out.println(act);
            for(String a: ans){
                System.out.println(a);
                Assert.assertTrue(act.contains(a));
            }
        }

    }
    @Test
    public void checkMethods2() throws NoParentsException {
        String[] args = {"TestEffortEstimationTutorial"};
        SettingManager sm = new SettingManager(args);
        GitController gitX = new GitController(sm, "/x/");
        gitX.checkout("24f3a020a4f3ad00bc07b4b6784841d66a4eed64");
        BuildToolController mavenX = new MavenController(gitX);
        StructureAnalyzer analyzerX = new StructureAnalyzer(mavenX);
        analyzerX.scan();

    }
    @Test
    public void checkAnotherSignature() throws NoParentsException {
        String[] args = {"TestEffortEstimationTutorial"};
        SettingManager sm = new SettingManager(args);
        GitController gitX = new GitController(sm, "/x/");
        gitX.checkout("46404c26c70d2615f7f6dc27ccfe5273d3464491");
        BuildToolController mavenX = new MavenController(gitX);
        StructureAnalyzer sa = new StructureAnalyzer(mavenX);
        sa.scan();
        for(String sig: sa.getAllSignatures()){
            System.out.println("-----------------");
            MethodDefinition md = sa.getMethod(sig);
            System.out.println(md.signature);
//            System.out.println(md.anotherSignature);
        }
    }
    @Ignore
    @Test
    public void testtt() throws NoParentsException {
        String[] args = {"javapoet"};
        SettingManager sm = new SettingManager(args);
        GitController gitX = new GitController(sm, "/test/");
        gitX.checkout("b1e1a088321da708d3299138fc55c0a9976a6291", true, true);
        BuildToolController mavenX = new MavenController(gitX);
        StructureAnalyzer analyzerX = new StructureAnalyzer(mavenX);
        analyzerX.scan();
        for(MethodDefinition md: analyzerX.getAllMethods()){
            System.out.println(md.signature+":"+md.start+"-"+md.end);
        }
    }

    @Transient
    public void testChangeParameter() throws NoParentsException {
        String[] args = {"TestEffortEstimationTutorial"};
        SettingManager sm = new SettingManager(args);
        GitController gitX = new GitController(sm, "/x/");
        gitX.checkout("8fccdf79ce37cc42a738127f259c80d1cfe65542");
        BuildToolController mavenX = new MavenController(gitX);
        StructureAnalyzer analyzerX = new StructureAnalyzer(mavenX);
        analyzerX.scan();
        for(MethodDefinition md: analyzerX.getAllMethods()){
            System.out.println(md.signature);
            for(Set<String> a: md.usedVariablesWithoutLocals.values()){
                if(a.size()>0){
                    System.out.println("  "+a);
                }
            }
        }
        System.out.println("--------");
        System.out.println(analyzerX.fieldsPosition.values());

    }

    @Test
    public void testSetField_N001() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "57846b464acc55b9a95dcb5e29ead2f0e992edb4";
        String signature = "src/main/java/function/Calculator.java;add#Double";

        StructureAnalyzer analyzerX = getStructureAnalyzer(project, commitId);
        MethodDefinition md = analyzerX.getMethod(signature);
        System.out.println(md.signature);
        Set<String> usedVals = md.usedVariablesWithoutLocals.get(19);
        Assert.assertEquals(1, usedVals.size());
        System.out.println(usedVals);
        Assert.assertTrue(usedVals.contains("function.Calculator.answer"));
    }

    @Test
    public void testSetField_N002() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "89196baddfe20532222e86c9b7875f9440d3416c";
        String signature = "src/main/java/function/Calculator.java;plus#Double";

        StructureAnalyzer analyzerX = getStructureAnalyzer(project, commitId);
        MethodDefinition md = analyzerX.getMethod(signature);
        System.out.println(md.signature);
        Set<String> usedVals = md.usedVariablesWithoutLocals.get(11);
        Assert.assertEquals(1, usedVals.size());
        System.out.println(usedVals);
        Assert.assertTrue(usedVals.contains("function.Base.intval"));
    }


    @Ignore
    @Test
    public void tess() throws NoParentsException {
        String project = "javapoet";
        String commitId = "c5b6b36b2e98b59f0711c1bfc8486c32eb3b482a";

        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/x/");
        gitX.checkout(commitId);
        BuildToolController mavenX = new MavenController(gitX);
        StructureAnalyzer analyzerX = new StructureAnalyzer(mavenX);
        analyzerX.scan();
        for(MethodDefinition md: analyzerX.getAllMethods()){
            System.out.println(md.signature);
            for(Set<String> a: md.usedVariablesWithoutLocals.values()){
                if(a.size()>0){
                    System.out.println("  "+a);
                }
            }
        }
        System.out.println("--------");
        System.out.println(analyzerX.fieldsPosition.values());


    }
    @Ignore
    @Test
    public void checkGenerics2() throws NoParentsException {
        String project = "joda-beans";
        String commitId = "1eec635172a5c053ff34e4f7c95c0c05ef93fcc2";
        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/x/");
        gitX.checkout(commitId);
        BuildToolController mavenX = new MavenController(gitX);
        StructureAnalyzer analyzerX = new StructureAnalyzer(mavenX);
        analyzerX.scan();
        List<String> sigs = analyzerX.getSignatures("src/main/java/org/joda/beans/impl/direct/DirectBeanBuilder.java");
        MethodDefinition md = analyzerX.getMethod(sigs.get(0));
        System.out.println(md.generics);
    }
    @Test
    public void testSetChangeX_1_N001() throws NoParentsException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "57846b464acc55b9a95dcb5e29ead2f0e992edb4";
        String signature = "src/test/java/function/CalculatorTest.java;testAdd_N001#";

        StructureAnalyzer analyzerX_1 = getStructureAnalyzer(project, commitId, true);
        MethodDefinition md = analyzerX_1.getMethod(signature);
        Assert.assertNotNull(md);
        Assert.assertFalse(md.changedLines.get(15));
        Assert.assertTrue(md.changedLines.get(16));
        Assert.assertFalse(md.changedLines.get(17));


    }


}
