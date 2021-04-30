package exe._2_run;

import beans.test.result.AbstractJunitTestResult;
import beans.test.result.CrossJunitTestResult;
import beans.test.result.StraightJunitTestResult;
import beans.trace.ExecutionTrace;
import utils.db.Dao;
import utils.log.MyLogger;

import java.util.List;

/**
 * This class provides methods that store test results and execution traces
 */
public class TestResultStorer {
    static MyLogger logger = MyLogger.getInstance();

    /**
     * Store Test results
     * @param result
     */
    public static void storeJunitResult(AbstractJunitTestResult result){
        if(result instanceof StraightJunitTestResult){
            Dao<StraightJunitTestResult> dao = new Dao<>(StraightJunitTestResult.class);
            dao.init();
            StraightJunitTestResult straight = (StraightJunitTestResult) result;
            dao.insert(straight);
            dao.close();
        }else if(result instanceof CrossJunitTestResult){
            Dao<CrossJunitTestResult> dao = new Dao<>(CrossJunitTestResult.class);
            dao.init();
            CrossJunitTestResult cross = (CrossJunitTestResult) result;
            dao.insert(cross);
            dao.close();
        }else{
            logger.error("result.getClass().getName(): "+result.getClass().getName());
            throw new AssertionError();
        }
    }

    /**
     * Store execution traces
     * @param traces
     */
    public static void storeExecutionTraces(List<ExecutionTrace> traces) {
        Dao<ExecutionTrace> dao = new Dao<>(ExecutionTrace.class);
        dao.init();
        for(ExecutionTrace et: traces)
            dao.insert(et);
        dao.close();
    }
}
