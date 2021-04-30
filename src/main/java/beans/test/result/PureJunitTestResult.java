package beans.test.result;

import beans.test.rowdata.TestInfo;
import beans.test.rowdata.TestResult;
import beans.test.rowdata.test.TestInfoPure;
import modules.build.controller.BuildToolController;
import org.w3c.dom.Node;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "pure", schema = "test")
public class PureJunitTestResult extends AbstractJunitTestResult{
//    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER, targetEntity = beans.test.rowdata.test.TestInfoPure.class)
    @JoinColumns({ @JoinColumn(name = "project",updatable=false, nullable=false),
            @JoinColumn(name = "commit_id",updatable=false,nullable=false) })
    public List<TestInfoPure> results;//signature, result


    public PureJunitTestResult(BuildToolController mc){
        super(mc);
        type = Execution.Pure;
        results = new ArrayList<>();
    }
    public PureJunitTestResult(){

    }

    @Override
    protected TestInfo createInstance(Node item, BuildToolController mc) {
        return TestInfo.createInstancePure(item, mc.getProject(), commitId, mc.getTestDir(false));
    }

    @Override
    protected TestInfo createInstance(TestResult.ResultType tr) {
        return new beans.test.rowdata.test.TestInfoPure(tr);
    }

    @Override
    protected <T extends TestInfo> void add(T tr) {
        TestInfoPure tis = (TestInfoPure)tr;
        this.results.add(tis);
    }


    @Override
    public List<? extends TestInfo> getTestResults() {
        return this.results;
    }


}
