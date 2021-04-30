package beans.test.result;

import beans.test.rowdata.TestInfo;
import beans.test.rowdata.TestResult;
import beans.test.rowdata.test.TestInfoStraight;
import modules.build.controller.BuildToolController;
import org.w3c.dom.Node;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "straight", schema = "test")
public class StraightJunitTestResult extends AbstractJunitTestResult{
//    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER, targetEntity = beans.test.rowdata.test.TestInfoStraight.class)
    @JoinColumns({ @JoinColumn(name = "project",updatable=false, nullable=false),
            @JoinColumn(name = "commit_id",updatable=false,nullable=false) })
    public List<TestInfoStraight> results;//signature, result


    public StraightJunitTestResult(BuildToolController mc){
        super(mc);
        type = Execution.Straight;
        results = new ArrayList<>();
    }
    public StraightJunitTestResult(){

    }

    @Override
    protected TestInfo createInstance(Node item, BuildToolController mc) {
        return TestInfo.createInstanceStraight(item, mc.getProject(), commitId, mc.getTestDir(false));
    }

    @Override
    protected TestInfo createInstance(TestResult.ResultType tr) {
        return new beans.test.rowdata.test.TestInfoStraight(tr);
    }

    @Override
    protected <T extends TestInfo> void add(T tr) {
        TestInfoStraight tis = (TestInfoStraight)tr;
        this.results.add(tis);
    }


    @Override
    public List<? extends TestInfo> getTestResults() {
        return this.results;
    }


}
