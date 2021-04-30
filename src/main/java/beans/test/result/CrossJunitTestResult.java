package beans.test.result;


import beans.test.rowdata.TestInfo;
import beans.test.rowdata.TestResult;
import beans.test.rowdata.test.TestInfoCross;
import modules.build.controller.BuildToolController;
import org.w3c.dom.Node;
import utils.exception.NoParentsException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name="cross", schema = "test")
public class CrossJunitTestResult extends AbstractJunitTestResult{
//    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER, targetEntity = beans.test.rowdata.test.TestInfoCross.class)
    @JoinColumns({ @JoinColumn(name = "project",updatable=false, nullable=false),
            @JoinColumn(name = "commit_id",updatable=false,nullable=false) })
    public List<TestInfoCross> results;//signature, result

    /**
     * Commit sha in the parent commit
     */
    protected String parentCommitId;

    public CrossJunitTestResult(BuildToolController mc) throws NoParentsException {
        super(mc);
        type = Execution.Cross;
        parentCommitId = mc.getParentCommitId();
        results = new ArrayList<>();
    }
    public CrossJunitTestResult(){

    }

    @Override
    protected TestInfo createInstance(Node item, BuildToolController mc) {
        return TestInfo.createInstanceCross(item, mc.getProject(), commitId, mc.getTestDir(false));
    }

    @Override
    protected TestInfo createInstance(TestResult.ResultType tr) {
        return new TestInfoCross(tr);
    }

    @Override
    protected <T extends TestInfo> void add(T tr) {
        this.results.add((TestInfoCross) tr);
    }

    @Override
    public List<? extends TestInfo> getTestResults() {
        return this.results;
    }

}
