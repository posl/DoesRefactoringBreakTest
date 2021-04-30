package modules.test;

import beans.test.result.StraightJunitTestResult;
import modules.build.controller.BuildToolController;

/**
 * TODO: we might no longer use this class? JUnitTestResultManager is enough?
 */
public class JunitTestResultManagerStraight extends JunitTestResultManager{
    public JunitTestResultManagerStraight(BuildToolController mc) {
        super(mc);
        this.result = new StraightJunitTestResult(mc);
    }
}
