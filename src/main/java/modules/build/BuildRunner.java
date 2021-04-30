package modules.build;

import beans.source.PassedLine;
import beans.test.result.AbstractJunitTestResult;
import modules.build.compile.CompileErrorEliminator;
import modules.build.compile.CompileErrorFinder;
import modules.build.controller.BuildToolController;
import modules.build.controller.maven.MavenController;
import modules.build.controller.maven.setup.SetUpMavenCompilerProperty;
import modules.build.controller.maven.setup.SetUpSureFire;
import modules.git.GitController;
import modules.source.execution_trace.ExecutionTracer;
import modules.source.execution_trace.impl.SeLogger;
import modules.source.structure.StructureAnalyzer;
import modules.test.JunitTestResultManager;
import org.apache.maven.shared.invoker.InvocationResult;
import utils.exception.*;
import utils.file.MyFileUtils;
import utils.log.MyLogger;
import utils.program.MyProgramUtils;
import utils.setting.SettingManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * this class manipulate build tool
 */
public abstract class BuildRunner {
    public final MavenController maven;
    public String commitId;
    static MyLogger logger = MyLogger.getInstance();
    private ExecutionTracer dynamicAnalyzer;
    private JunitTestResultManager trm;
    AbstractJunitTestResult result;
    /**
     * initialize maven repository
     * @param gitMain
     */
    public BuildRunner(SettingManager sm, GitController gitMain, String commitId) throws DependencyProblemException, ProductionProblemException, NoTargetBuildFileException, NoSureFireException {
        this.commitId = commitId;
        assert (gitMain!=null);
        maven = new MavenController(gitMain);
        maven.checkout(commitId);
        maven.readBuildFile();
        this.checkSureFire();
        this.setUpExtraPom();
        maven.writePom();
        this.dependencyCheck();
        this.productionCheck();
        this.setUpDynamicAnalyzer(sm);
    }

    /**
     * set up sure fire plugin
     * @throws NoSureFireException
     */
    private void checkSureFire() throws NoSureFireException {
        maven.setupExtraPom(new SetUpSureFire());
    }
    /**
     * set up dynamic trace tool
     * @throws NoSureFireException
     */
    protected void setUpDynamicAnalyzer(SettingManager sm) {
        dynamicAnalyzer = ExecutionTracer.getInstance(sm.getSetting("tracer"), maven);
    }
    /**
     * set up other plugins
     * @throws NoSureFireException
     */
    protected void setUpExtraPom() throws NoSureFireException {
        maven.setupExtraPom(new SetUpMavenCompilerProperty());
    }

    /**
     * deploy files which is used in only CrossBuildRunner
     * @throws NoSureFireException
     */
    public abstract void deploy() throws IOException;







    /**
     * to run maven
     * @param mc
     * @param goal
     * @return
     */
    private static InvocationResult execute(BuildToolController mc, String goal, String option) {
        //InvocationResult ir0 = mc.run("clean");//Just in case
        InvocationResult ir2 = mc.run(goal, option);
        return ir2;
    }

    public void dependencyCheck() throws DependencyProblemException{
        String errors = "";
        InvocationResult preResult = execute(maven, "test-compile dependency:resolve", "-Drat.skip=true");
        if(preResult.getExitCode() != 0){
            for(String a: maven.getErrors()){
                errors = errors + " " + a;
            }
            // errors = maven.getErrors().get(0).split("¥n")[0];
            throw new DependencyProblemException(errors);
        }
    }

    /**
     * check if the compiler returns pass
     * @return
     */
    public void productionCheck() throws ProductionProblemException {
        InvocationResult preResult = execute(maven, " compile dependency:resolve test-compile", "-Dmaven.test.skip=true -Drat.skip=true");
        if(preResult.getExitCode() != 0){
            String errors = "";
            for(String a: maven.getErrors()){
                errors = errors + " " + a;
            }
            // errors = maven.getErrors().get(0).split("¥n")[0];
            throw new ProductionProblemException(errors);
        }
    }



    protected abstract JunitTestResultManager getTestJunitResultManager() throws NoParentsException;

    private void setPom(ExecutionTracer dynamicAnalyzer) throws NoSureFireException {
        if (dynamicAnalyzer==null){
            return;
        }else if(dynamicAnalyzer instanceof SeLogger){
            maven.setupExtraPom(new SeLogger(maven));
            maven.writePom();
        }
    }

    /**
     * Run tests and return the results. If compiler errors are detected, they will be deleted.
     * @throws IOException
     */
    public AbstractJunitTestResult run() throws IOException, NoParentsException, TestUnknownFailureException, NoSureFireException {
        trm  = this.getTestJunitResultManager();
        this.setPom(dynamicAnalyzer);
        //run
        StructureAnalyzer analyzer = new StructureAnalyzer(maven);
        analyzer.scan();
        Set<String> methods = analyzer.getTestSignature();//method level run
        trm = this.removeCompileError(trm);
        for(String c: methods){//run each method
            //delete the results of tests and SeLogger during previous run
            this.cleanFiles();
            c = MyProgramUtils.transform2MavenSignature(c, maven.getSrcDir(false), maven.getTestDir(false));//メソッドレベル用
            InvocationResult result = execute(maven, "test", "-Dmaven.main.skip=true -Drat.skip=true -Dmaven.test.failure.ignore=true -DfailIfNoTests=false -Dtest="+c);
            trm.recordSucceedTest(c,true);//read test results
            trm.setPassLinesMap(dynamicAnalyzer);
        }
        this.result = trm.getResults();
        return this.result;
    }

    private void cleanFiles() {
        try {
            MyFileUtils.deleteDirectory(this.maven.getSureFireOutputDir());
        }catch (IOException nsf){
            //nothing
        }
        try {
            dynamicAnalyzer.clean();
        }catch (IOException | NullPointerException nsf){
            //nothing
        }
    }

    private JunitTestResultManager removeCompileError(JunitTestResultManager trm) throws TestUnknownFailureException, IOException {
        int attempt=1;
        while(true) {//repeat until the test passed
            logger.trace(attempt+"th attempt");
            InvocationResult result = execute(maven, "test-compile", "-Drat.skip=true");
            if (result.getExitCode() == 0) {
                break;
            } else {//delete test methods that have compiler errors
                List<CompileErrorFinder.ErrorMethod> list = new CompileErrorFinder().getErrorMethods(maven);
                if(list.size()==0){
                    throw new TestUnknownFailureException();//when errors happen except compiler errors
                }
                trm.recordCompileErrorInTest(list);
                CompileErrorEliminator eliminator = new CompileErrorEliminator(list);//Modify source code to eliminate errors
                trm.recordDeletedLines(attempt, eliminator.deleteLines());
                attempt++;
            }
        }
        return trm;
    }

    public Map<String, Map<Integer, List<PassedLine>>> getPassedLines(){
        return trm.getPassedLines();
    }

    public AbstractJunitTestResult getResult() {
        return this.result;
    }
}
