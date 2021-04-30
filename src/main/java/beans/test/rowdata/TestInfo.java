package beans.test.rowdata;

import beans.test.rowdata.test.TestInfoCross;
import beans.test.rowdata.test.TestInfoStraight;
import beans.test.rowdata.test.TestInfoPure;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import utils.log.MyLogger;
import utils.program.MyProgramUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TestInfo implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;
    @Transient
    MyLogger logger = MyLogger.getInstance();
    /**
     * this ID is automatically generated for each table by hibernate
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="test_info_id", columnDefinition="bigint")
    public long test_info_id;
    /**
     * Project name
     */
    @Transient
    @Column(name = "project")
    public String project;
    /**
     * Commit SHA
     */
    @Transient
    @Column(name = "commit_id")
    public String commitId;

    /**
     * Method signature (i.e., file path+class+method name+parameter type)
     */
    @Column(name = "signature")
    public String signature;

    @Transient
    public String signatureParameterized;

    /**
     * class name
     */
    @Column(name = "className")
    public String className;

    /**
     * method name
     */
    @Column(name = "methodName")
    public String methodName;

    /**
     * execution time measured by JUnit
     */
    @Column(name = "executionTime")
    public Double executionTime;
    /**
     * test result
     */
    @Embedded
    public TestResult testResult;

    public static TestInfo createInstanceStraight(Node node, String project, String commitId, String testPath) {
        TestInfo tr = new TestInfoStraight();
        setContents(tr, node, project, commitId, testPath);
        return tr;
    }
    public static TestInfo createInstanceCross(Node node, String project, String commitId, String testPath) {
        TestInfo tr = new TestInfoCross();
        setContents(tr, node, project, commitId, testPath);
        return tr;
    }
    public static TestInfo createInstancePure(Node node, String project, String commitId, String testPath) {
        TestInfo tr = new TestInfoPure();
        setContents(tr, node, project, commitId, testPath);
        return tr;
    }

    /**
     * extract data from XML file provided by JUnit and set the data in this class
     * @param tr
     * @param node
     * @param project
     * @param commitId
     * @param testPath
     */
    private static void setContents(TestInfo tr, Node node, String project, String commitId, String testPath) {
        tr.setTestResult(node);
        Element e = (Element) node;

        if(tr.testResult.getType().equals(TestResult.ResultType.SKIPPED)){
            return;
        }
        tr.project =project;
        tr.commitId =commitId;

        String[] className = e.getAttribute("classname").split("\\.");
        tr.className= className[className.length-1];
        tr.setMethodName(e);
        testPath = MyProgramUtils.addLastSlash(testPath);
        String path =  testPath + e.getAttribute("classname").replaceAll("\\.", "/")+".java";
        String arguments = getArguments(e);
        tr.setSignature(MyProgramUtils.getSignature(path, tr.methodName, arguments));


        tr.executionTime=Double.parseDouble(e.getAttribute("time"));
    }

    /**
     * extract method name
     * @param e
     * @return
     */
    private static String getArguments(Element e) {
        String methodName = e.getAttribute("name");
        if(methodName.contains("{")){
            methodName = methodName.split("\\{")[1].split("}")[0];
            methodName = methodName.replaceAll(" ", "").replaceAll("\\[", "");
            return methodName;
        }else {
            return "";
        }

    }

    private void setTestResult(Node node) {
        this.testResult = new TestResult(node);
    }

    public String toString(){
        return testResult.toString()+": "+methodName+"#"+className+"@"+ commitId;
    }
    public TestInfo(TestResult.ResultType rs){
        this.testResult = new TestResult(rs);
    }
    public TestInfo(){
    }

    public String getSignatureParameterized() {
        return signatureParameterized;
    }
    public String getSignature() {
        return signature;
    }

    /**
     * This is for parameterized test (but is not used in this study)
     * @param signature
     */
    public void setSignature(String signature) {
        this.signatureParameterized = signature;
        String sig = signature;

        String regex = "\\[[0-9]+\\]#";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(sig);
        if(m.find()){
            String parameterizedNumber =m.group();
            sig = sig.replace(parameterizedNumber, "#");
        }

        this.signature = sig;
    }

    /**
     * extract name from XML file
     * @param e
     */
    public void setMethodName(Element e) {
        this.methodName = e.getAttribute("name").split("\\{")[0];
        if(this.methodName.contains(".")){
            List<String> l = new ArrayList<String>();
            for(String s:e.getAttribute("name").split("\\.")){
                l.add(s);
            }
            this.methodName = l.get(l.size() - 1);
        }
    }

    /**
     * check if this result is compiler error
     * @return
     */
    public boolean isCompileError(){
        return testResult.type.equals(TestResult.ResultType.COMPILE_ERROR);
    }

}
