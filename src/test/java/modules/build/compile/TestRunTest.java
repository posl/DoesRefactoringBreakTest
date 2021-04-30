package modules.build.compile;

import beans.source.PassedLine;
import beans.test.result.AbstractJunitTestResult;
import beans.test.rowdata.TestInfo;
import modules.build.BuildRunner;
import modules.build.BuildRunnerCross;
import modules.build.BuildRunnerStraight;
import modules.git.GitController;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import utils.exception.*;
import utils.setting.SettingManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRunTest {
    public BuildRunner run(String project, String commitId, boolean isCross) throws IOException, NoParentsException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, TestUnknownFailureException, NoSureFireException {
        SettingManager sm = new SettingManager(new String[]{project});
        GitController git = new GitController(sm, "/test/");
        BuildRunner buildRunner;
        if (isCross){
            buildRunner = new BuildRunnerCross(sm, git, commitId);
        }else{
            buildRunner = new BuildRunnerStraight(sm, git, commitId);
        }
        buildRunner.deploy();
        AbstractJunitTestResult testResult = buildRunner.run();
        return buildRunner;
    }
    @Ignore//too much time
    @Test
    public void commons_text_N001() throws IOException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, NoParentsException, TestUnknownFailureException, NoSureFireException {
        String project = "commons-text";
        String commitId = "cc9dc64ac8d7b3dc629557187d0a9fcec44f03ee";
        run(project, commitId, false);
    }

    @Ignore//too much time
    @Test(expected = DependencyProblemException.class)
    public void javapoet_N011() throws IOException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, NoParentsException, TestUnknownFailureException, NoSureFireException {
        String project = "javapoet";
        String commitId = "2fc51db0fcee90f3c9f342ff1caf6ff073a39ad9";
        run(project, commitId, false);

    }

    @Ignore//too much time
    @Test(expected = DependencyProblemException.class)
    public void javapoet_N012() throws IOException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, NoParentsException, TestUnknownFailureException, NoSureFireException {
        String project = "javapoet";
        String commitId = "0a6ee12960724854a0da33b171bcb06c1121c3e5";
        run(project, commitId, false);
    }

    /**
     * Dependency Error pattern
     * @throws IOException
     * @throws NoTargetBuildFileException
     * @throws ProductionProblemException
     * @throws DependencyProblemException
     * @throws NoParentsException
     */
    @Ignore//too much time
    @Test(expected = DependencyProblemException.class)
    public void javapoet_E021() throws IOException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, NoParentsException, TestUnknownFailureException, NoSureFireException {
        String project = "javapoet";
        String commitId = "2ddbb90789ae9c8eea9d8264b964748c76703a81";
        run(project, commitId, false);
    }

    @Ignore//too much time
    @Test
    public void javapoet_N013() throws IOException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, NoParentsException , TestUnknownFailureException, NoSureFireException {
        String project = "javapoet";
        String commitId = "aed50f772034c1235482ecc98fb2c9be794a0449";
        run(project, commitId, true);
    }
    @Ignore//too much time
    @Test
    public void jsoup_N001() throws IOException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, NoParentsException , TestUnknownFailureException, NoSureFireException {
        String project = "jsoup";
        String commitId = "9297a22afcf1396c54539e0f225d048f794783ab";
        run(project, commitId, false);
    }
    @Ignore//too much time
    @Test
    public void jsoup_N002() throws IOException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, NoParentsException , TestUnknownFailureException, NoSureFireException {
        String project = "jsoup";
        String commitId = "ebd2a773d67e8d1d042607fd2458f92e18b9203b";
        run(project, commitId, false);
    }
    @Ignore//too much time
    @Test
    public void jsoup_N003() throws IOException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, NoParentsException , TestUnknownFailureException, NoSureFireException {
        String project = "jsoup";
        String commitId = "105f7bdcdbfffc879d0737cf857c02b6823b1b73";
        run(project, commitId, true);
    }

    @Test//too much time
    public void tutorial_N001() throws IOException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, NoParentsException , TestUnknownFailureException, NoSureFireException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "53d2c478232a414a89b9642de81b718d597c44fb";
        Map<String, String> answers = new HashMap<>();
        answers.put("src/test/java/CalculatorTest.java;testDivided_N001#", "PASS");
        answers.put("src/test/java/CalculatorTest.java;testMinos_N001#", "PASS");
        answers.put("src/test/java/CalculatorTest.java;testTimes_N001#", "PASS");
        answers.put("src/test/java/CalculatorTest.java;testAdd_N001#", "PASS");
        BuildRunner runner = run(project, commitId, false);
        Assert.assertEquals(4, runner.getResult().getTestResults().size());
        for(TestInfo t: runner.getResult().getTestResults()){
            System.out.println(t.signature);
            String res = answers.get(t.signature);
            Assert.assertEquals(res, t.testResult.type.name());
        }
        System.out.println(runner.getPassedLines());
        //one time check
        Map<Integer, List<PassedLine>> passes = runner.getPassedLines().get("src/test/java/CalculatorTest.java;testAdd_N001#");
        List<PassedLine> list = passes.get(10);
        Assert.assertEquals("src/main/java/Calculator.java;plus#int", list.get(0).signature);
        Assert.assertEquals("7", list.get(0).lineNo);
        Assert.assertEquals("src/main/java/Calculator.java;plus#int", list.get(1).signature);
        Assert.assertEquals("8", list.get(1).lineNo);

    }
    @Test
    public void tutorial_N002() throws IOException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, NoParentsException , TestUnknownFailureException, NoSureFireException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "53d2c478232a414a89b9642de81b718d597c44fb";
        Map<String, String> answers = new HashMap<>();
        answers.put("src/test/java/CalculatorTest.java;testDivided_N001#", "PASS");
        answers.put("src/test/java/CalculatorTest.java;testMinos_N001#", "PASS");
        answers.put("src/test/java/CalculatorTest.java;testTimes_N001#", "PASS");
        answers.put("src/test/java/CalculatorTest.java;testAdd_N001#", "PASS");
        BuildRunner runner = run(project, commitId, false);
        Assert.assertEquals(4, runner.getResult().getTestResults().size());
        for(TestInfo t: runner.getResult().getTestResults()){
            System.out.println(t.signature);
            String res = answers.get(t.signature);
            Assert.assertEquals(res, t.testResult.type.name());
        }
        System.out.println(runner.getPassedLines());
        //one-time check
        Map<Integer, List<PassedLine>> passes = runner.getPassedLines().get("src/test/java/CalculatorTest.java;testAdd_N001#");
        List<PassedLine> list = passes.get(10);
        Assert.assertEquals("src/main/java/Calculator.java;plus#int", list.get(0).signature);
        Assert.assertEquals("7", list.get(0).lineNo);
        Assert.assertEquals("src/main/java/Calculator.java;plus#int", list.get(1).signature);
        Assert.assertEquals("8", list.get(1).lineNo);

    }
    @Test
    public void Tutorial_N003() throws IOException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, NoParentsException , TestUnknownFailureException, NoSureFireException {
        String project = "TestEffortEstimationTutorial";
        String commitId = "2833f1d5157877675801271a326bb2ba9b9b43ca";
        run(project, commitId, true);
    }
    @Ignore//too much time
    @Test
    public void guice_N001() throws IOException, NoTargetBuildFileException, ProductionProblemException, DependencyProblemException, NoParentsException , TestUnknownFailureException, NoSureFireException {
        String project = "guice";
        String commitId = "338d0039c1e30038f22f0d5544842c1e87406a8a";
        run(project, commitId, false);
    }
    @Ignore//too much time
    @Test
    public void testGetSignatureFromFileName001() throws Exception {
        String project = "guice";
        String commitId = "338d0039c1e30038f22f0d5544842c1e87406a8a";

        run(project, commitId, false);


    }
    @Ignore//too much time
    @Test
    public void testCross() throws Exception {
        String project = "commons-io";
        String commitId = "72d53cf242788510de904c0c239d8804fc73c2dd";

        run(project, commitId, true);
        Map<String, String> answers = new HashMap<>();
        answers.put("src/test/java/CalculatorTest.java;testDivided_N001#", "PASS");
        answers.put("src/test/java/CalculatorTest.java;testMinos_N001#", "PASS");
        answers.put("src/test/java/CalculatorTest.java;testTimes_N001#", "PASS");
        answers.put("src/test/java/CalculatorTest.java;testAdd_N001#", "PASS");
        BuildRunner runner = run(project, commitId, true);
        Assert.assertEquals(4, runner.getResult().getTestResults().size());
        for(TestInfo t: runner.getResult().getTestResults()){
            System.out.println(t.signature);
            String res = answers.get(t.signature);
            Assert.assertEquals(res, t.testResult.type.name());
        }

    }

    @Test
    public void testInitializerErrorHandling() throws Exception{
        String project = "TestEffortEstimationTutorial";
        String commitId = "4593d8031e377d259bd22c8cdf6688b953eb72a1";
        BuildRunner runner = run(project, commitId, true);
        for(TestInfo t: runner.getResult().getTestResults()){
            if (t.getSignature().contains("CalculatorTest")){
                Assert.assertEquals("COMPILE_ERROR", t.testResult.type.name());
            }else{
                Assert.assertEquals("PASS", t.testResult.type.name());

            }
        }
    }
}
