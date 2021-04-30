package exe._2_run;

import beans.commit.Commit;
import beans.other.run.Registry;
import beans.trace.ExecutionTrace;
import modules.build.BuildRunner;
import beans.test.result.AbstractJunitTestResult;
import modules.source.structure.StructureAnalyzer;
import modules.git.GitController;
import utils.db.Dao;
import utils.exception.*;
import utils.file.MyFileUtils;
import utils.log.MyLogger;
import utils.setting.SettingManager;
import utils.trace.MyTraceUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public abstract class AbstractRefactoringEffortAnalyzer {
    protected MyLogger logger = MyLogger.getInstance();
    protected SettingManager sm;
    protected GitController git;
    private Dao<Commit> commitDao;
    protected Commit commit;
    protected Registry registry;
    public AbstractRefactoringEffortAnalyzer() throws FinishException {
        System.out.println("******START*******");
        this.setRegistry();
        this.initiate();
    }

    protected void initiate() {
        //read setting file
        this.sm = new SettingManager(new String[]{registry.project});
        //Get commit data
        this.commit = this.getCommit();
        MyFileUtils.deleteDirectory(sm.getOutputDir(), true);
        this.git = new GitController(sm, "/main/");
    }


    private Commit getCommit() {
        commitDao = new Dao<>(Commit.class);
        commitDao.init();
        commitDao.setWhere("commit_id", registry.commitId);
        commitDao.setWhere("project", registry.project);
        List<Commit> list = commitDao.select();
        assert list!=null;
        assert list.size()==1;
        return list.get(0);
    }

    protected void setRegistry() throws FinishException {
        registry = init();
        if(registry==null){
            throw new FinishException();
        }
    }
    /**
     * This method gets a target to be built from database
     * @return
     */
    protected abstract Registry init();
    /**
     * This method store the result of builds
     * @return
     */
    protected abstract void finish(Registry registry);
    public void finish(){
        registry.endDate = LocalDateTime.now();
        finish(registry);
        if(commitDao!=null){
            commitDao.close();
        }
        System.out.println("******FINISH*******");
    }

    AbstractJunitTestResult testResult;
    List<ExecutionTrace> traces;
    StructureAnalyzer analyzer;
    public void analyze() {
        registry.startDate = LocalDateTime.now();
        try {
            BuildRunner buildRunner = this.getBuildRunner();
            buildRunner.deploy();
            analyzer = new StructureAnalyzer(buildRunner.maven);
            analyzer.scan();
            testResult = buildRunner.run();//test run. If compiler errors happen they will be deleted.
            traces = MyTraceUtils.transform(registry, buildRunner.getPassedLines());
            registry.resultCode = 2;
        }catch (DependencyProblemException ppe){
            logger.info("Dependency Problem @" + commit.commitId);
            registry.resultCode = 3;
            registry.resultMessage = "Dependency Problem Exception";
            registry.errorMessage = ppe.getErrors();
        }catch (ProductionProblemException ppe){
            logger.info("Production Problem @" + commit.commitId);
            registry.resultCode = 3;
            registry.resultMessage = "Production Problem Exception";
            registry.errorMessage = ppe.getErrors();
        }catch (NoParentsException pe){
            System.err.println("NoParentsException");
            registry.resultCode = 3;
            registry.resultMessage = "No Parents Exception";
        } catch (IOException e) {
            logger.info("File Not Found Exception @" + commit.commitId);
            registry.resultCode = 3;
            registry.resultMessage = "File Not Found Exception";
        } catch (NoTargetBuildFileException nte) {
            logger.info("NoTargetBuildFileException in " + commit.commitId);
            registry.resultCode = 3;
            registry.resultMessage = "No Build File Exception";
        } catch (TestUnknownFailureException te) {
            logger.error("TestUnknownFailureException @" + commit.commitId);
            logger.error(te);
            registry.resultCode = 3;
            registry.resultMessage = "TestUnknownFailure Error";
        } catch (NoSureFireException nte) {
            logger.info("NoSureFireException in " + commit.commitId);
            registry.resultCode = 3;
            registry.resultMessage = "No Sure-fire Setting Exception";
        } catch (AssertionError ae) {
            logger.error("Program Error" + commit.commitId);
            logger.error(ae);
            registry.resultCode = -1;
            registry.resultMessage = "Program Error";
        } catch (Exception e) {
            logger.error("Anonymous Error in " + commit.commitId);
            logger.error(e);
            registry.resultCode = -1;
            registry.resultMessage = "Anonymous Error";
        }
    }

    /**
     * This method creates a runner that builds and tests
     * @return
     */
    protected abstract BuildRunner getBuildRunner() throws IOException, NoParentsException, NoTargetBuildFileException, DependencyProblemException, ProductionProblemException, NoSureFireException;


    public int getResultCode(){
        return this.registry.resultCode;
    }

    public static void execute(AbstractRefactoringEffortAnalyzer analyzer){
        analyzer.analyze();
        if(analyzer.getResultCode()==2){
            TestResultStorer.storeJunitResult(analyzer.getResult());
            TestResultStorer.storeExecutionTraces(analyzer.getTraces());
        }
        analyzer.finish();
    }
    public AbstractJunitTestResult getResult(){
        return testResult;
    }
    public List<ExecutionTrace> getTraces(){
        return traces;
    }
}
