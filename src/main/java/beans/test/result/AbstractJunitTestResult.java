package beans.test.result;

import beans.source.MethodDefinition;
import beans.test.rowdata.TestInfo;
import beans.test.rowdata.TestResult;
import modules.build.compile.CompileErrorFinder;
import modules.build.controller.BuildToolController;
import org.w3c.dom.Node;
import utils.log.MyLogger;
import utils.program.MyProgramUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@MappedSuperclass
public abstract class AbstractJunitTestResult implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;
    @Transient
    protected MyLogger logger = MyLogger.getInstance();
    @Transient
    public  Execution type;
    /**
     * Commit sha
     */
    @Id
    @Column(name="commit_id")
    public  String commitId;
    /**
     * Project name
     */
    @Id
    @Column(name="project")
    public  String project;



    public AbstractJunitTestResult(BuildToolController mc){
        this.commitId = mc.getCommitId();
        this.project = mc.getProject();
    }
    public AbstractJunitTestResult(){
    }

    /**
     * add test results
     * @param item
     * @param mc
     * @return
     */
    public String add(Node item, BuildToolController mc) {
        TestInfo tr = this.createInstance(item, mc);
        if(!tr.testResult.type.equals(TestResult.ResultType.SKIPPED)){
            this.add(tr);
        }
        return tr.signature;
    }

    protected abstract TestInfo createInstance(Node item, BuildToolController mc);
    protected abstract TestInfo createInstance(TestResult.ResultType tr);

    protected abstract <T extends TestInfo> void add(T tr);

    /**
     * extract compiler error information and put it into this class
     * @param em
     */
    public void add(CompileErrorFinder.ErrorMethod em) {
        TestInfo tr = this.createInstance(TestResult.ResultType.COMPILE_ERROR);
        tr.testResult.setErrorMessage(em);
        MethodDefinition md = em.getDefinition();
        if(md==null){
            //outside of the method
            return;
        }
        tr.setSignature(MyProgramUtils.getSignature(md));
        tr.commitId = this.commitId;
        tr.className = em.getClassName();
        tr.executionTime = null;
        tr.methodName = em.getMethodName();
        this.add(tr);
    }
    
    public abstract List<? extends TestInfo> getTestResults();

    /**
     * Execution type
     * Pure: normal test
     * Straight: normal test with dyanamic execution trace
     * Cross: test the product code of revision X with test code of revision X-1
     */
    public enum Execution {
        Cross, Straight, Pure
    }


    public boolean isStraight(){
        return this.type.equals(Execution.Straight);
    }

    public TestInfo getResult(String sig){
        List<? extends TestInfo> list = getTestResults();
        for(TestInfo t: list){
            if(t.signature.equals(sig)){
                return t;
            }
        }
        return null;
    }
}
