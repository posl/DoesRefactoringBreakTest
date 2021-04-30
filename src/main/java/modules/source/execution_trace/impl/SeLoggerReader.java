package modules.source.execution_trace.impl;

import beans.source.PassedLine;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import utils.general.MyListUtils;
import utils.log.MyLogger;
import utils.program.MyProgramUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class read the output of SELogger
 */
public class SeLoggerReader {
    public static MyLogger logger = MyLogger.getInstance();

    public static int HEADER_CLASSES_NO = 0;
    public static int HEADER_CLASSES_PATH = 1;//not used
    public static int HEADER_CLASSES_CLASSNAME = 2;

    public static int HEADER_METHODS_CLASS_NO = 0;
    public static int HEADER_METHODS_METHOD_NO = 1;
    public static int HEADER_METHODS_CLASS_NAME = 2;
    public static int HEADER_METHODS_METHOD_NAME = 3;
    public static int HEADER_METHODS_METHOD_ARG_RETURN = 4;

    public static int HEADER_DATA_ID_NO = 0;
    public static int HEADER_DATA_CLASS_NO = 1;
    public static int HEADER_DATA_METHOD_NO = 2;
    public static int HEADER_DATA_LINE_NO = 3;
    public static int HEADER_DATA_TYPE = 5;

    public static int HEADER_CALL_DATA_NO = 0;
    public static int HEADER_CALL_ORDER = 4;

    /**
     * API to get lines where each line of the test execution exercised
     * @param homeDir
     * @param srcDir
     * @param testDir
     * @param seloggerOutputDir
     * @param testSignatures
     * @return
     */
    public static Map<String, Map<Integer, List<PassedLine>>> getPassLinesMap(String homeDir, String srcDir, String testDir, String seloggerOutputDir, List<String> testSignatures) {
        Map<Long, PassedLine> calls = calcPassLinesMap(homeDir, srcDir, testDir, seloggerOutputDir);
        Map<String, Map<Integer, List<PassedLine>>> map = groupByTestLine(testDir, calls, testSignatures);
        return map;
    }

    /**
     * Implements to get lines where each line of the test execution exercised
     * @param testDir
     * @param allCalls
     * @param testSignatures
     * @return
     */
    public static Map<String, Map<Integer, List<PassedLine>>> groupByTestLine(String testDir, Map<Long, PassedLine> allCalls, List<String> testSignatures) {
        Map<String, Map<Integer, List<PassedLine>>> map = new HashMap<>();

        int testLineNo = -1;
        String target = null;
        Set<String> invokedMethods = new HashSet();

        Map<Integer, List<PassedLine>> callsByTestLine = new TreeMap<>();
        List<PassedLine> passedLines = new ArrayList<>();
        for (PassedLine passedLine : allCalls.values()) {
            if (MyProgramUtils.isTestConstructor(passedLine.signature, testDir)){
                if(target!=null){
                    map.put(target, callsByTestLine);
                    target = null;
                    callsByTestLine = new TreeMap<>();
                    passedLines = new ArrayList<>();
                    invokedMethods = new HashSet();
                }
            }

            if(testSignatures.contains(passedLine.signature)){
                if(target==null){
                    target = passedLine.signature;
                    testLineNo = Integer.parseInt(passedLine.lineNo);
                    callsByTestLine.put(testLineNo-1, passedLines);
                    passedLines = new ArrayList<>();
                    invokedMethods.add(passedLine.signature.split(";")[0]);
                }else if(target.equals(passedLine.signature)){
                    assert passedLines !=null;
                    callsByTestLine.put(testLineNo, passedLines);
                    passedLines = new ArrayList<>();
                    testLineNo = Integer.parseInt(passedLine.lineNo);
                }else{//a test method invokes test methods
                    passedLines.add(passedLine);
                }
            }else{
                passedLines.add(passedLine);
            }
            invokedMethods.add(passedLine.signature.split(";")[0]);
        }
        if(testLineNo!=-1){
            callsByTestLine.put(testLineNo, passedLines);
            map.put(target, callsByTestLine);
        }
        return map;
    }


    /**
     * get lines where the test executions exercised
     * @param homeDir
     * @param srcDir
     * @param testDir
     * @param seloggerOutputDir
     * @return
     */
    public static Map<Long, PassedLine> calcPassLinesMap(String homeDir, String srcDir, String testDir, String seloggerOutputDir) {

        try {
            //file read
            String classesFile = homeDir + seloggerOutputDir + "/" + "classes.txt";
            String methodsFile = homeDir + seloggerOutputDir + "/" + "methods.txt";
            String dataIdsFile = homeDir + seloggerOutputDir + "/" + "dataids.txt";
            String callsFile = homeDir + seloggerOutputDir + "/" + "recentdata.txt";
            //read method file
            Map<String, String> methods = getMethods(srcDir, testDir, classesFile, methodsFile);
            //read data file
            Map<String, String> process = getProcess(dataIdsFile);
            //read calls
            Map<Long, PassedLine> passes = getPasses(callsFile, methods, process);

            return passes;
        } catch (IOException e) {
            logger.error(e);
            throw new AssertionError();
        }
    }

    /**
     *
     * @param callsFile
     * @param methods
     * @param programs
     * @return
     * @throws IOException
     */
    public static Map<Long, PassedLine> getPasses(String callsFile, Map<String, String> methods, Map<String, String> programs) throws IOException {
        Map<Long, PassedLine> calls = new TreeMap<>();
        Iterable<CSVRecord> callsCSV = CSVFormat.DEFAULT.parse(new FileReader(callsFile));
        Set<String> unieque = new HashSet<>();
        for (CSVRecord csv : callsCSV) {
            String called_no = csv.get(HEADER_CALL_DATA_NO);
            String[] called_contents = programs.get(called_no).split(":");
            String methodNo = called_contents[0];
            String lineNo = called_contents[1];
            String type = called_contents[2];
            if(lineNo.equals("0")){
                continue;
            }
            PassedLine passedLine = null;
            for (int i = HEADER_CALL_ORDER; i < csv.size(); i += 3) {//patterns that the files has many right side
                Long order = Long.parseLong(csv.get(i));
                String methodName = methods.get(methodNo);
                if(methodName.contains("$")){
                    continue;//to deal with access$100
                }
                passedLine = new PassedLine(methodName, lineNo, type);
                if(!unieque.contains(passedLine.toString())){
                    calls.put(order, passedLine);
                }else{
                    break;
                }
            }
            if(passedLine !=null) {
                unieque.add(passedLine.toString());
            }
        }
        return calls;
    }

    /**
     *
     * @param dataIdsFile
     * @return
     * @throws IOException
     */
    public static Map<String, String> getProcess(String dataIdsFile) throws IOException {
        Iterable<CSVRecord> dataIdsCSV = CSVFormat.DEFAULT.parse(new FileReader(dataIdsFile));
        Map<String, String> programs = new HashMap<>();
        for (CSVRecord line : dataIdsCSV) {
            programs.put(line.get(HEADER_DATA_ID_NO), line.get(HEADER_DATA_METHOD_NO) + ":" + line.get(HEADER_DATA_LINE_NO)+ ":" + line.get(HEADER_DATA_TYPE));
        }
        return programs;
    }

    /**
     *
     * @param srcDir
     * @param testDir
     * @param classesFile
     * @param methodsFile
     * @return
     * @throws IOException
     */
    public static Map<String, String> getMethods(String srcDir, String testDir, String classesFile, String methodsFile) throws IOException {
        //class
        Iterable<CSVRecord> classCSV = CSVFormat.DEFAULT.parse(new FileReader(classesFile));
        Map<String, String> classes = new HashMap<>();
//        Map<String, String> specialCases = new HashMap<>();
        for (CSVRecord csv : classCSV) {
            String className = csv.get(HEADER_CLASSES_CLASSNAME);
            String path = csv.get(HEADER_CLASSES_PATH);
            String srcOrTest;
            if (path.endsWith("test-classes/")) {
                srcOrTest = testDir;
            } else if (path.endsWith("classes/")) {
                srcOrTest = srcDir;
            } else {
                System.out.println(path);
                logger.error(path);
                throw new AssertionError();
            }

            if (className.contains("$")) {
                className = className.replaceFirst("\\$", "\\.java\\$");
//                className = className.replaceAll("\\$[0-9]+","");//無名クラス対応

            } else {
                className += ".java";
            }
            classes.put(csv.get(HEADER_CLASSES_NO), srcOrTest + className);
        }
        //method
        Iterable<CSVRecord> methodCSV = CSVFormat.DEFAULT.parse(new FileReader(methodsFile));
        Map<String, String> methods = new HashMap<>();
        for (CSVRecord csv : methodCSV) {
            //make signature with class
            String no =csv.get(HEADER_METHODS_CLASS_NO);
            String className = classes.get(no);
            String methodName = csv.get(HEADER_METHODS_METHOD_NAME);
            String arguments = getArguments(csv.get(HEADER_METHODS_METHOD_ARG_RETURN));
            logger.trace(className+":"+methodName);
            //to handle constructor
            if (methodName.equals("<init>")||methodName.equals("<clinit>")) {
                methodName = MyProgramUtils.getClassNameFromPath(className);
                methodName = methodName.replace(".java", "");
                //subclass
                if (className.contains("$")) {
                    String[] tmp = className.replace(".java", "").split("/");
                    String c = tmp[tmp.length - 1];
                    String[] a = c.split("\\$");
                    for (int i = 0; i < a.length - 1; i++) {
                        arguments = arguments.replaceFirst(a[i], "");
                        arguments = arguments.replaceFirst("\\$", "");
                    }
                }
            }
//            methodName = methodName.replaceAll("\\$[0-9]+","");
            methodName = methodName.replaceAll("lambda\\$","");


            String signature = MyProgramUtils.getSignature(className, methodName, arguments);
            methods.put(csv.get(HEADER_METHODS_METHOD_NO), signature);
            //HEADER_METHODS_METHOD_ARG_RETURN

        }
        return methods;
    }

    /**
     *
     * @param s
     * @return
     */
    public static String getArguments(String s) {
        Set set = new HashSet();
        List<String> list = new ArrayList<>();

        String contentsInBlanket = s.split("\\(")[1].split("\\)")[0];

        String[] semicolon = contentsInBlanket.split(";");
        for (String semi : semicolon) {
            if (semi.contains("[L")) {
                String brankets = getBrancketNum(semi);
                String[] l = semi.split("\\[+L", 2);
                assert l.length == 2;
                String s1 = getPrimitiveType(l[0]);
                if (!s1.equals("")) {
                    list.add(s1);
                }
                String s2 = getOwnType(l[1]);
                s2 = getSubclass(s2);
                list.add(s2 + brankets);
            } else if (semi.contains("L")) {
                String[] l = semi.split("L", 2);
                assert l.length == 2;
                String s1 = getPrimitiveType(l[0]);
                if (!s1.equals("")) {
                    list.add(s1);
                }
                String s2 = getOwnType(l[1]);
                s2 = getSubclass(s2);
                list.add(s2);
            } else {
                list.add(getPrimitiveType(semi));
            }
        }
        Pattern pattern = Pattern.compile("\\$[0-9]+");
        List<String> removeList = new ArrayList<>();
        for (String arg: list){
            Matcher m = pattern.matcher(arg);
            if(m.find()){
                removeList.add(arg);
            }
        }
        list.removeAll(removeList);
        return MyListUtils.flatten(list);
    }

    /**
     *
     * @param s2
     * @return
     */
    public static String getSubclass(String s2) {
        if(s2.contains("$")){
            String[] tmp = s2.split("\\$");
            s2 = tmp[tmp.length-1];
        }
        return s2;
    }

    public static String getBrancketNum(String semi) {
        String ret = "";
        for (int i = 0; i < semi.length(); i++) {
            if (semi.charAt(i) == '[') {
                ret+="]";
            } else if (semi.charAt(i) == 'L') {
                break;
            } else {
                ret = "";
            }
        }
        return ret;
    }

    private String getArray(String a) {
        String prefix = "";
        if (a.startsWith("[")) {
            prefix = "]";
            a = a.replace("[", "");
        }
        return prefix;
    }

    private static String getOwnType(String a) {
        String[] tmp = a.split("/");
        String type = tmp[tmp.length - 1];
        return type;
    }

    /**
     * Convert SELogger's expression to general expression (e.g., I will be int)
     * @param a
     * @return
     */
    public static String getPrimitiveType(String a) {
        StringJoiner sj = new StringJoiner(",");
        String prefix = "";
        for (int i = 0; i < a.length(); i++) {
            switch (a.charAt(i)) {
                case 'I':
                    sj.add("int" + prefix);
                    prefix = "";
                    break;
                case 'D':
                    sj.add("double" + prefix);
                    prefix = "";
                    break;
                case 'F':
                    sj.add("float" + prefix);
                    prefix = "";
                    break;
                case 'S':
                    sj.add("short" + prefix);
                    prefix = "";
                    break;
                case 'J':
                    sj.add("long" + prefix);
                    prefix = "";
                    break;
                case 'Z':
                    sj.add("boolean" + prefix);
                    prefix = "";
                    break;
                case 'B':
                    sj.add("byte" + prefix);
                    prefix = "";
                    break;
                case 'C':
                    sj.add("char" + prefix);
                    prefix = "";
                    break;
                case '[':
                    prefix += "]";
                    break;
            }
        }

        return sj.toString();
    }
}
