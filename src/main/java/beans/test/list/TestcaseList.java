package beans.test.list;

import java.util.ArrayList;
import java.util.List;

public class TestcaseList {
    /**
     * Commit sha in a target commit
     */
    public String commitId_X;
    /**
     * Commit sha in the parent commit
     */
    public String commitId_X_1;
    /**
     * added test cases
     */
    public List<String> addedSignatures;
    /**
     * deleted test cases
     */
    public List<String> deletedSignatures;
    /**
     * modified test cases
     */
    public List<String> modifiedSignatures;

    public TestcaseList(String target, String previous) {
        this.commitId_X = target;
        this.commitId_X_1 = previous;
        addedSignatures = new ArrayList<>();
        deletedSignatures = new ArrayList<>();
        modifiedSignatures = new ArrayList<>();
    }

}
