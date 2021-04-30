package modules.test;

import beans.test.result.PureJunitTestResult;
import modules.build.controller.BuildToolController;
/**
 * TODO: we might no longer use this class? JUnitTestResultManager is enough?
 */
public class JunitTestResultManagerPure extends JunitTestResultManager{
    public JunitTestResultManagerPure(BuildToolController mc) {
        super(mc);
        this.result = new PureJunitTestResult(mc);
    }
}