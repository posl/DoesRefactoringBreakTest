package modules.source.execution_trace.impl;

import beans.other.run.CrossRegistry;
import beans.other.run.Registry;
import beans.source.PassedLine;
import beans.source.TestMethodDefinition;
import beans.test.rowdata.TestInfo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import util.RefactoringEffortAnalyzerCrossStub;
import util.SpecifierStub;
import utils.exception.FinishException;
import utils.exception.NoParentsException;
import utils.program.MyProgramUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SeLoggerTest {

	@Test
    public void testGetPassLinesMap_N001() {

        SeLogger seLogger = new SeLogger("SeLogger/getPassLinesMap");
        List<String> list = new ArrayList<>();
        list.add("src/test/java/functions/CalculatorTest.java;testAdd_N001#");
        list.add("src/test/java/functions/CalculatorTest.java;testAdd_N002#");
        list.add("src/test/java/functions/CalculatorTest.java;testMinus_N001#");
        list.add("src/test/java/functions/CalculatorTest.java;testMinus_N002#");
        list.add("src/test/java/functions/CalculatorTest.java;testTimes_N001#");
        list.add("src/test/java/functions/CalculatorTest.java;testTimes_N002#");
        list.add("src/test/java/functions/distributions/Calculator2Test.java;testPower_N001#");
        list.add("src/test/java/functions/distributions/Calculator2Test.java;testPower_N002#");
        list.add("src/test/java/functions/distributions/Calculator2Test.java;special_N001#");

        Map<String, Map<Integer, List<PassedLine>>> map = seLogger.getPassLinesMap(list);
        for(String sig: list){
            Map<Integer, List<PassedLine>> exp = map.get(sig);
            Assert.assertNotNull(exp);
        }
    }

    @Test
    public void testGetPassLinesMap_N002() {

        List<String> testSignatures = new ArrayList<>();
        testSignatures.add("src/test/java/functions/distributions/Calculator2Test.java;all#");
        SeLogger seLogger = new SeLoggerStub("SeLogger/getPassLinesMap2");
        Map<String, Map<Integer,List<PassedLine>>> map = seLogger.getPassLinesMap(testSignatures);
        System.out.println(map.keySet());
        for(String sig: testSignatures){
            System.out.println(sig);
            Map<Integer, List<PassedLine>> exp = map.get(sig);
            Assert.assertNotNull(exp);
        }
    }

    @Test
    public void getArgumentsTest() throws IOException {
        String[] answers = {"Double", "int", "double", "float", "short",
                "long", "boolean", "byte", "char", "byte]",
                "Integer]", "Integer]", "Object", "", "",
                "Double,Double", "String]", "SubClass", "Double,Double,Double,Double", "int,double", "int,double,Double"};
        SeLoggerStub seLogger = new SeLoggerStub("SeLogger/getArgumentsTest");
        String methodsFile = seLogger.getMethodFile();

        Iterable<CSVRecord> methodCSV = CSVFormat.DEFAULT.parse(new FileReader(methodsFile));
        int i = 0;
        for (CSVRecord csv : methodCSV) {
            //make signature with class name
            String arguments = seLogger.getArguments(csv.get(SeLoggerReader.HEADER_METHODS_METHOD_ARG_RETURN));
            Assert.assertEquals(answers[i], arguments);
            i++;
        }

    }

    @Test
    public void ecda7763441660a4984f20a8f11a035591e28969() throws IOException {
        SeLoggerStub seLogger = new SeLoggerStub("SeLogger/ecda7763441660a4984f20a8f11a035591e28969");
        String methodsFile = seLogger.getMethodFile();
        String classesFile = seLogger.getClassFile();
        String dataIdsFile = seLogger.getDataFile();
        String callsFile = seLogger.getCallFile();



        Map<String, String> methods = seLogger.getMethods(classesFile, methodsFile);
        System.out.println(methods);
        //data
        Map<String, String> programs = seLogger.getProcess(dataIdsFile);
        //call
        Map<Long, PassedLine> calls = seLogger.getPasses(callsFile, methods, programs);
        System.out.println(calls);

    }

    @Test
    public void test24f3a020a4f3ad00bc07b4b6784841d66a4eed64() throws FinishException, NoParentsException {
        String commitId = "24f3a020a4f3ad00bc07b4b6784841d66a4eed64";
        String project = "TestEffortEstimationTutorial";
        Registry registry = new CrossRegistry(project, commitId);
        RefactoringEffortAnalyzerCrossStub analyzer = new RefactoringEffortAnalyzerCrossStub(registry);
        analyzer.analyze();
        Assert.assertEquals(analyzer.getTraces().size(), 0);
    }

    @Test
    public void N003()  {//39737d42f581d8e69af4b1b023c5ea39780ea90a
        SeLoggerStub seLogger = new SeLoggerStub("SeLogger/N003");
        List<String> list = Arrays.asList(
                "src/test/java/functions/distributions/Calculator2Test.java;special_N001#",
                "src/test/java/functions/distributions/Calculator2Test.java;testPower_N001#",
                "src/test/java/functions/distributions/Calculator2Test.java;testPower_N002#",
                "src/test/java/functions/distributions/Calculator2Test.java;all#"
        );
        Map<Integer, String> ans4testPower_N002 = new TreeMap<>();
        ans4testPower_N002.put(19, "[src/test/java/functions/distributions/Calculator2Test.java;Calculator2Test#@9, src/test/java/functions/distributions/Calculator2Test.java;Calculator2Test#@10]");
        ans4testPower_N002.put(20, "[src/main/java/functions/distributions/Calculator2.java;Calculator2#@7, src/main/java/functions/Calculator.java;Calculator#@9, src/main/java/functions/Calculator.java;Calculator#@10, src/main/java/functions/Calculator.java;Calculator#@11, src/main/java/functions/Calculator.java;Calculator#@12]");
        ans4testPower_N002.put(21, "[src/main/java/functions/Calculator.java;plus#Double@17, src/main/java/functions/Calculator.java;plus#Double@18, src/main/java/functions/Calculator.java;isNull#Double,Double@75, src/main/java/functions/Calculator.java;isNull#Double,Double@76, src/main/java/functions/Calculator.java;isNull#Double,Double@78, src/main/java/functions/Calculator.java;plus#Double@19, src/main/java/functions/Calculator.java;plus#Double@21, src/main/java/functions/Calculator.java;plus#Double@22]");
        ans4testPower_N002.put(22, "[src/main/java/functions/distributions/Calculator2.java;power#Double@9, src/main/java/functions/distributions/Calculator2.java;power#Double@10, src/main/java/functions/distributions/Calculator2.java;power#Double@11]");
        ans4testPower_N002.put(23, "[src/main/java/functions/distributions/Calculator2.java;getAnswer#@15, src/main/java/functions/Calculator.java;getAnswer#@69]");
        ans4testPower_N002.put(24, "[src/main/java/functions/Calculator.java;aho#@72, src/main/java/functions/Calculator.java;aho#@73]");

        Map<Integer, String> ans4special_N001 = new TreeMap<>();
        ans4special_N001.put(27, "[src/test/java/functions/distributions/Calculator2Test.java;Calculator2Test#@9, src/test/java/functions/distributions/Calculator2Test.java;Calculator2Test#@10]");
        ans4special_N001.put(28, "[src/main/java/functions/special/B.java;echo#@5, src/main/java/functions/special/B.java;echo#@6]");

        Map<String, Map<Integer, List<PassedLine>>> map = seLogger.getPassLinesMap(list);

        Map<Integer, List<PassedLine>> a1 = map.get("src/test/java/functions/distributions/Calculator2Test.java;special_N001#");
        Assert.assertEquals(ans4special_N001.size(), a1.size());
        for(Integer key: a1.keySet()){
            System.out.println(key);
            String act = a1.get(key).toString();
            String ans = ans4special_N001.get(key);
            Assert.assertEquals(ans, act);
        }
        Map<Integer, List<PassedLine>> a2 = map.get("src/test/java/functions/distributions/Calculator2Test.java;testPower_N002#");
        Assert.assertEquals(ans4testPower_N002.size(), a2.size());
        for(Integer key: a2.keySet()){
            System.out.println(key);
            String act = a2.get(key).toString();
            String ans = ans4testPower_N002.get(key);
            Assert.assertEquals(ans, act);
        }


    }
    public void N004(){//dbc4e0be8521fc3c8bf079017b927c6b136dca83

    }
    @Test
    public void twoByteDeletion() {

        String reg = "\\W";
        String en = "hello".replaceAll(reg, "");
        Assert.assertEquals ("hello", en);

        String ja = "こんにちは".replaceAll(reg, "");
        Assert.assertEquals ("", ja);

        String ch = "你好".replaceAll(reg, "");
        Assert.assertEquals ("", ch);

        String al = "سپین".replaceAll(reg, "");
        Assert.assertEquals ("", al);

        String other = "႗၇𝟐?𑇕?５৮௫7꤆𝟖?".replaceAll(reg, "");
        Assert.assertEquals ("7", other);

    }

    @Test
    public void doubleDimension() throws IOException {
        SeLoggerStub seLogger = new SeLoggerStub("SeLogger/doubleDimension");
        String methodsFile = seLogger.getMethodFile();
        String classesFile = seLogger.getClassFile();

        Map<String, String> methods = seLogger.getMethods(classesFile, methodsFile);
        System.out.println(methods);
        Assert.assertEquals("src/main/java/functions/Calculator.java;aaa1#int]]", methods.get("10"));
        Assert.assertEquals("src/main/java/functions/Calculator.java;aaa12#Double]]", methods.get("22"));

    }

    @Test
    public void N002() throws IOException {
        SeLoggerStub seLogger = new SeLoggerStub("SeLogger/N002");
        String methodsFile = seLogger.getMethodFile();
        String classesFile = seLogger.getClassFile();

        Map<String, String> methods = seLogger.getMethods(classesFile, methodsFile);
        System.out.println(methods);
        Assert.assertEquals(
                "src/main/java/org/apache/commons/text/RandomStringGenerator.java;RandomStringGenerator#int,int,Set,TextRandomProvider,List",
                methods.get("1894"));

    }



    @Test
    public void checkSeloggerContainsSignature() throws Exception {
        String project = "TestEffortEstimationTutorial";
        String commitId = "cceeb8d5fec9bfc3b1014ccb5cb8ed962e3e572c";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for (final TestInfo a : specifier.result.getTestResults()) {
            if(MyProgramUtils.isConstructor(a.getSignature())) continue;
            TestMethodDefinition tmd = (TestMethodDefinition) specifier.analyzer.getMethod(a.getSignature());
            Assert.assertNotNull(tmd);
        }
    }
    @Test
    public void checkPassLinesMapContainsSignature() throws Exception {
        String project = "TestEffortEstimationTutorial";
        String commitId = "cceeb8d5fec9bfc3b1014ccb5cb8ed962e3e572c";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinespass = specifier.buildRunner.getPassedLines().get(s);
            for(Integer i : eachLinespass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinespass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void checkPassLines() throws Exception {
        String project = "joda-beans";
        String commitId = "e5234b9277a93e567fd9b0b8e04c1a5e81b44aba";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }

    @Ignore
    @Test
    public void checkSizeBehavior() throws Exception {
        String project = "joda-beans";
        String commitId = "787e5f2fb9b29cccb4542a3ee0e4db7b69d1ce32";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        Map<String,  Map<Integer, Integer>> passedLineSizewithSig50 = new HashMap<String, Map<Integer, Integer>>();
        // Map<String,  Map<Integer, Integer>> passedLineSizewithSig60 = new HashMap<String, Map<Integer, Integer>>();
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            Map<Integer, Integer> passedLineSize = new HashMap<Integer, Integer>();
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer line : eachLinesPass.keySet()){
                passedLineSize.put(line, eachLinesPass.get(line).size());
            }
            passedLineSizewithSig50.put(s, passedLineSize);
        }
        System.out.println(passedLineSizewithSig50);
    }
    @Ignore
    @Test
    public void checkSureFireCorrectN001() throws Exception {
        String project = "joda-beans";
        String commitId = "0df15f7f09e93a042a173f35ec51afa93ac6f7ca";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void checkSureFireCorrectN002() throws Exception {
        String project = "joda-beans";
        String commitId = "34a1b291ad260f456ef2a1e2eaaebc7d09484fd5";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void checkSureFireCorrect2() throws Exception {
        String project = "joda-beans";
        String commitId = "34a1b291ad260f456ef2a1e2eaaebc7d09484fd5";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }

    @Ignore
    @Test
    public void checkSureFireCorrect3() throws Exception {
        String project = "joda-beans";
        String commitId = "41be9f9dad2ef3bee1dd730da2c0077b9d6dc6cc";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }

    @Ignore
    @Test
    public void testSeLoggerInjsoupN001() throws Exception {
        String project = "jsoup";
        String commitId = "d65510c8ed0f10561372838b1c15b9d8af658d8b";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testSeLoggerInjsoupN002() throws Exception {
        String project = "jsoup";
        String commitId = "5f0714329e2763d330460efee8ccd7f69acc8e7c";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testSeLoggerInjsoupN003() throws Exception {
        String project = "jsoup";
        String commitId = "140b48a58568c9614ad91773598b56891bb70bac";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testSeLoggerInjunit4N004() throws Exception {
        String project = "junit4";
        String commitId = "467c3f8efe1a87e3029df282e4df60ad98bc4142";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testSeLoggerInguice4N001() throws Exception {
        String project = "guice";
        String commitId = "7d9991e6354f9a97c191c09e21a8e62f60ae9ce9";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testSeLoggerInguice4N002() throws Exception {
        String project = "guice";
        String commitId = "338d0039c1e30038f22f0d5544842c1e87406a8a";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testSeLoggerIncommonsioN001() throws Exception {
        String project = "commons-io";
        String commitId = "b803066005e1244932146aa904f05b420ca689a3";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testSeLoggerIncommonsioN002() throws Exception {
        String project = "commons-io";
        String commitId = "68a73b54d6fd08ea2951ea1911e035a2390119bc";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testSeLoggerIncommonsioN003() throws Exception {
        String project = "commons-io";
        String commitId = "01f92b184933bf8f333676f0b872cecb8e23466d";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testcommonsloggingIncommonsioN001() throws Exception {
        String project = "commons-logging";
        String commitId = "5063d2387588e98605dc6d1b9f5206499a09b592";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testcommonsloggingIncommonsioN002() throws Exception {
        String project = "commons-logging";
        String commitId = "ae02acf389b6475017147d991bfafcc446a3c8a9";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testInswaggercoreN001() throws Exception {
        String project = "swagger-core";
        String commitId = "e555f472fa523c40dd0f4addeffad85ece98c447";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testInpippoN001() throws Exception {
        String project = "pippo";
        String commitId = "6e5ce99a12ebd246e5c8330697f24f36aae0a4f0";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testIntruthN001() throws Exception {
        String project = "truth";
        String commitId = "db853e680c150ef9907ef936874d15a6fe29031e";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testIntraccarN001() throws Exception {
        String project = "traccar";
        String commitId = "5b3ee0a9666a9c7fe3b3c04d1fe637b7d7b935da";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testIntraccarN002() throws Exception {
        String project = "traccar";
        String commitId = "457c6a29077ebb143e40c29fdc37863b8d46357a";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testIntraccarN003() throws Exception {
        String project = "traccar";
        String commitId = "9cc1c29ec08cdc4369b010ac17b270bf3c3d7ead";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testIntraccarN004() throws Exception {
        String project = "traccar";
        String commitId = "b7d48127e60bcaa5d01f45d8df5203f28f9a1667";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testInSpoonN001() throws Exception {
        String project = "spoon";
        String commitId = "adb7890225e8915470606f88c5ce87f1869e0368";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testInRestheartN001() throws Exception {
        String project = "restheart";
        String commitId = "254dbd3fe8fd57876a1796bd29c1e83c7bdd30ef";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        System.out.println(specifier.analyzer.getTestSignature());
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }
    @Ignore
    @Test
    public void testInLog4jN001() throws Exception {
        String project = "log4j";
        String commitId = "c6b4fcb791c4d0f46974a1515f317858e6eeab55";

        SpecifierStub specifier = new SpecifierStub(project, commitId);
        System.out.println(specifier.analyzer.getTestSignature());
        for(String s: specifier.analyzer.getTestSignature()){
            System.out.println(s);
            Map<Integer, List<PassedLine>> eachLinesPass = specifier.buildRunner.getPassedLines().get(s);
            if(eachLinesPass==null) continue;//This is due to abstract method
            for(Integer i : eachLinesPass.keySet()){
                System.out.println(" "+i);
                List<PassedLine> passedLines = eachLinesPass.get(i);
                for(PassedLine s2 : passedLines){
                    System.out.println("  "+s2.toString());
                    if(MyProgramUtils.isConstructor(s2.signature)) continue;
                    Assert.assertTrue(specifier.analyzer.hasMethod(s2.signature));
                }
            }
        }
    }



    
}