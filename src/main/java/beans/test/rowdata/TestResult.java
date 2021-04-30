package beans.test.rowdata;

import modules.build.compile.CompileErrorFinder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.log.MyLogger;

import javax.persistence.*;
import java.io.Serializable;
/**
 *
 * This class is used to store/get data from Database (hibernate)
 */
@Embeddable
public class TestResult implements Serializable {

    @Transient
    MyLogger logger = MyLogger.getInstance();
    /**
     * Test result type
     * e.g., PASS, Compiler error
     */
    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    public ResultType type;
    /**
     * error information in XML file (when error happens)
     */
    @Embedded
    public ErrorMessage errorMessage;

    public TestResult(){}
    public void setErrorMessage(CompileErrorFinder.ErrorMethod em) {
        this.errorMessage = new ErrorMessage(em);

    }


    public TestResult(ResultType type) {
        this.type = type;
    }

    /**
     * extract test results from xml file
     * @param e
     */
    public TestResult(Node e){
        NodeList node = e.getChildNodes();
        int numNodes = node.getLength();
        if (numNodes == 0) {
            //System.out.println(ResultType.PASS);
            this.type = ResultType.PASS;
            return;
        }
        for (int i = 0; i < node.getLength(); i++) {
            //System.out.println(i + ": " + node.item(i).toString());
            if(this.setErrorMessage(node.item(i))){
                return;
            }
        }
        throw new AssertionError();
    }

    private boolean setErrorMessage(Node item) {
        if (item.getNodeType() == Node.ELEMENT_NODE) {
            Element child = (Element) item;
            String name = child.getTagName();
            //System.out.println(name);
            switch (name) {
                case "failure":
                    this.type = ResultType.FAIL;
                    break;
                case "error":
                    this.type = ResultType.RUNTIME_ERROR;
                    break;
                case "skipped":
                    this.type = ResultType.SKIPPED;
                    break;
                default:
                    logger.error(name);
                    throw new AssertionError();
            }
            errorMessage = new ErrorMessage(child);
            return true;
        }
        return false;
    }

    public String toString(){
        String prefix="";
        if(this.errorMessage!=null){
            prefix = "("+this.errorMessage.type+")";
        }
        return this.type+prefix;
    }
    public ResultType getType(){
        return type;
    }
    public void setType(String type){
        this.type = ResultType.valueOf(type);
    }

    /**
     * Test result types
     */
    public enum ResultType {
        COMPILE_ERROR,
        RUNTIME_ERROR,
        FAIL,
        PASS,
        SKIPPED
    }

}
