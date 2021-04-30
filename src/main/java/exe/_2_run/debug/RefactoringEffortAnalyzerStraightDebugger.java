package exe._2_run.debug;

import exe._2_run.RefactoringEffortAnalyzerStraight;
import utils.exception.FinishException;

public class RefactoringEffortAnalyzerStraightDebugger {
    protected static int i = 1;
    protected static int DEBUG_SKIP_UNTIL = 0;



    public static void main(String[] args) throws FinishException {

        // System.out.println(r.commitId);

        while (true) {
            if (i < DEBUG_SKIP_UNTIL){
                continue;
            }
            RefactoringEffortAnalyzerStraight.main(null);
        }
    }



}
