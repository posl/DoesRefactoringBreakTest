package modules.test;

import beans.source.PassedLine;
import beans.test.result.AbstractJunitTestResult;
import beans.test.rowdata.TestInfo;
import modules.build.compile.CompileErrorEliminator;
import modules.build.compile.CompileErrorFinder;
import modules.build.controller.BuildToolController;
import modules.source.execution_trace.ExecutionTracer;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.file.MyFileReadWriteUtils;
import utils.log.MyLogger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Read JUnit's output and record test results
 */
public abstract class JunitTestResultManager {
    public AbstractJunitTestResult result;

    protected static MyLogger logger = MyLogger.getInstance();
    /**
     * The paths of the outputs by JUnit
     */
    protected final String outputDataDir;
    /**
     * Build tool controller to build and run tests
     */
    protected final BuildToolController mc;
    /**
     * The lines that are exercised by test methods
     */
    protected Map<String, Map<Integer, List<PassedLine>>> passLinesMap;//testSignature, <testLine, <signature, lineNo>>
    /**
     * Deleted lines due to compile errors
     */
    protected Map<String, Map<String, List<CompileErrorEliminator.Line>>> deletedLinesMap;
    /**
     * to run method by method
     */
    List<String> testQueue = new ArrayList<>();

    public JunitTestResultManager(BuildToolController mc) {
        this.outputDataDir=mc.getSureFireOutputDir();
        this.mc=mc;
        deletedLinesMap = new LinkedHashMap<>();
        passLinesMap = new HashMap<>();
    }


    public Map<String, Map<Integer, List<PassedLine>>> getPassedLines(){
        return passLinesMap;
    }




    public Map<Integer, List<PassedLine>> getPassLines(String signature) {
        return passLinesMap.get(signature);
    }

    /**
     * Read result's XML file given by JUnit and record the results of methods.
     * @param c
     * @param copy
     */
    public void recordSucceedTest(String c, boolean copy) {
        List<String> li = MyFileReadWriteUtils.getFileList(this.outputDataDir, ".xml");
        if(copy){
            storeXMLFiles(c, li);
        }
        if (li==null){
            return;
        }

        for(String fileName: li){
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
            for(int i=0;i<nodeList.getLength();i++){
                String added = result.add(nodeList.item(i), mc);
                testQueue.add(added);
            }
        }
    }

    /**
     * copy the output file
     * @param m
     * @param li
     */
    private void storeXMLFiles(String m, List<String> li) {
        try {
            if(li==null){
                FileUtils.touch(new File(this.mc.getXMLStoreDir()+this.result.commitId+"/"+this.result.type +"/no_methods_"+m+".txt"));
            }else {
                FileUtils.copyDirectory(new File(this.outputDataDir), new File(this.mc.getXMLStoreDir() + this.result.commitId+"_"+m+"/"+this.result.type));
            }
        } catch (IOException e) {
            logger.error(e);
            throw new AssertionError();
        }
    }

    /**
     * add the compiler error to result variable
     * @param list
     */
    public void recordCompileErrorInTest(List<CompileErrorFinder.ErrorMethod> list) {
        for (CompileErrorFinder.ErrorMethod em:list) {
            result.add(em);
        }
    }

    /**
     * record deleted lines
     * @param attempt
     * @param removedLines
     */
    public void recordDeletedLines(int attempt, Map<String, List<CompileErrorEliminator.Line>> removedLines) {
        deletedLinesMap.put(this.result.commitId+"#"+attempt, removedLines);
    }


    /**
     * show lines (for debug)
     */
    public void showDeletedLines() {
        for (String key1:deletedLinesMap.keySet()){
            logger.trace("******"+key1+"******");
            Map<String, List<CompileErrorEliminator.Line>> val1 = deletedLinesMap.get(key1);
            for (String key2:val1.keySet()){
                logger.trace("-----"+key2+"-----");
                List<CompileErrorEliminator.Line> val2 = val1.get(key2);
                for(CompileErrorEliminator.Line line:val2){
                    if(line.delete){
                        System.out.println(line.contents);
                    }
                }
            }
        }
    }

    /**
     * set the test methods that passed
     * @param dynamicAnalyzer
     */
    public void setPassLinesMap(ExecutionTracer dynamicAnalyzer) {
        if (dynamicAnalyzer==null) return;
        System.out.println(testQueue);
        if(testQueue.size()>0){
            this.passLinesMap.putAll(dynamicAnalyzer.getPassLinesMap(testQueue));
            testQueue.removeAll(passLinesMap.keySet());
        }
    }


    public List<? extends TestInfo> getTestResults() {
        return result.getTestResults();
    }
    public AbstractJunitTestResult getResults() {
        return result;
    }

    public boolean contains(String m) {
        return result.getResult(m)!=null;
    }
}
