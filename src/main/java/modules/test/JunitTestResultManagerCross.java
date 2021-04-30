package modules.test;

import beans.test.result.CrossJunitTestResult;
import modules.build.controller.BuildToolController;
import utils.exception.NoParentsException;
/**
 * TODO: we might no longer use this class? JUnitTestResultManager is enough?
 */
public class JunitTestResultManagerCross extends JunitTestResultManager{
    public JunitTestResultManagerCross(BuildToolController mc) throws NoParentsException {
        super(mc);
        this.result = new CrossJunitTestResult(mc);
    }
}
