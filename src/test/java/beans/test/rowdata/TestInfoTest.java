package beans.test.rowdata;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class TestInfoTest {

    @Test
    public void readN001(){
        String fileName = "src/test/resources/TestInfo/TEST-org.apache.commons.text.similarity.ParameterizedLevenshteinDistanceTest.xml";
        NodeList nodeList = getNodeList(fileName);
        TestInfo tr = TestInfo.createInstanceStraight(nodeList.item(0), "apache-commons","0b55205025fcec1bf5b6317e79dba63a7bed33b3", "");
        Assert.assertEquals("org/apache/commons/text/similarity/ParameterizedLevenshteinDistanceTest.java;test#Integer,CharSequence,CharSequence,Integer", tr.getSignature());
    }
    @Test
    public void readN002(){
        String fileName = "src/test/resources/TestInfo/TEST-org.apache.commons.text.diff.ReplacementsFinderTest.xml";
        NodeList nodeList = getNodeList(fileName);

        TestInfo tr = TestInfo.createInstanceStraight(nodeList.item(0), "apache-commons", "0b55205025fcec1bf5b6317e79dba63a7bed33b3", "");
        Assert.assertEquals("org/apache/commons/text/diff/ReplacementsFinderTest.java;testReplacementsHandler#String,String,int,Character],Character]", tr.getSignature());
    }

    @Test
    public void readN003(){
        String fileName = "src/test/resources/TestInfo/TEST-org.apache.commons.text.TextStringBuilderTest.xml";
        NodeList nodeList = getNodeList(fileName);
        TestInfo tr = TestInfo.createInstanceStraight(nodeList.item(0), "apache-commons","0b55205025fcec1bf5b6317e79dba63a7bed33b3", "");
        Assert.assertEquals("org/apache/commons/text/TextStringBuilderTest.java;testAsTokenizer#", tr.getSignature());
    }


    private NodeList getNodeList(String fileName) {
        File xml = new File(fileName);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        Document document = null;
        try {
            documentBuilder = factory.newDocumentBuilder();
            document = documentBuilder.parse(xml);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            throw new AssertionError(fileName);
        }
        Element root = document.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("testcase");
        return nodeList;
    }
}
