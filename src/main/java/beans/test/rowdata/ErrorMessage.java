package beans.test.rowdata;

import modules.build.compile.CompileErrorFinder;
import org.w3c.dom.Element;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
/**
 * This class extracts error message from XML data given by JUnit.
 * For example,
 * <testcase name="testPower_N002" classname="functions.distributions.Calculator2Test" time="0.011">
 * <failure message="expected: &lt;1.0069555500567189&gt; but was: &lt;1.0&gt;" type="org.opentest4j.AssertionFailedError"><![CDATA[org.opentest4j.AssertionFailedError: expected: <1.0069555500567189> but was: <1.0>
 *  at functions.distributions.Calculator2Test.testPower_N002(Calculator2Test.java:19)
 *   ]]></failure>
 * This class is used to store/get data from Database (hibernate)
 */
@Embeddable
public class ErrorMessage implements Serializable {

    /**
     * Error message
     * e.g., failure message=XXXX
     */
    @Column(name = "error_message", columnDefinition="TEXT")
    public String message;
    /**
     * Error type
     * e.g., org.opentest4j.AssertionFailedError
     */
    @Column(name = "error_type", columnDefinition="TEXT")
    public String type;
    /**
     * Error contents
     * e.g., CDATA[XXX
     */
    @Column(name = "error_contents", columnDefinition="TEXT")
    public String contents;
    public ErrorMessage(){}
    public ErrorMessage(CompileErrorFinder.ErrorMethod em) {
        this.message = em.getReason();
        this.type = " COMPILATION ERROR";
    }
//[ERROR] /Users/yutarokashiwa/Documents/200_Development/210_Git/TestEffortEstimationTutorial/src/test/java/functions/distributions/Calculator2Test.java:[18,9] not a statement
    public ErrorMessage(Element child) {
        this.message = child.getAttribute("message");
        this.type = child.getAttribute("type");
        this.contents = child.getTextContent();
    }


    public String toString(){
        return message;
    }
}
