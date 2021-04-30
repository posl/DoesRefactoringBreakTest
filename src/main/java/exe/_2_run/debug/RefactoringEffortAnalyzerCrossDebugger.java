package exe._2_run.debug;

import exe._2_run.RefactoringEffortAnalyzerCross;
import utils.exception.FinishException;

public class RefactoringEffortAnalyzerCrossDebugger {
    protected static int i = 1;
    protected static int DEBUG_SKIP_UNTIL = 0;



    public static void main(String[] args) throws FinishException {
        while (true) {
            if (i < DEBUG_SKIP_UNTIL){
                continue;
            }
            RefactoringEffortAnalyzerCross.main(null);
        }
    }



}
