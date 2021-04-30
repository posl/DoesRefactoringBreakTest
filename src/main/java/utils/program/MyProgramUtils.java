package utils.program;

import beans.source.MethodDefinition;
import com.atlassian.clover.api.registry.MethodSignatureInfo;
import com.atlassian.clover.api.registry.ParameterInfo;
import com.atlassian.clover.registry.entities.TestCaseInfo;
import gr.uom.java.xmi.decomposition.AbstractStatement;
import gr.uom.java.xmi.decomposition.VariableDeclaration;
import utils.general.MyListUtils;

import java.util.*;

public class MyProgramUtils {
    /**
     * to create method signature from String
     * @param relativePath
     * @param method
     * @param arguments
     * @return
     */
    public static String getSignature(String relativePath, String method, String arguments){
        return relativePath+";"+method+"#"+arguments;
    }

    /**
     * to create method signature from string and lists
     * @param relativePath
     * @param method
     * @param arguments
     * @return
     */
    public static String getSignature(String relativePath, String method, List<String> arguments){
        String args = "";
        return getSignature(relativePath, method, args);
    }

    /**
     * to create method signature from MethodDefinition
     * @param md
     * @return
     */
    public static String getSignature(MethodDefinition md) {
        String arguments = MyListUtils.flatten(md.arguments);
        return getSignature(md.getFileName(), md.getMethodName(), arguments);
    }

    /**
     * Extract the file name
     * @param signature
     * @param removeJava
     * @return
     */
    public static String getFileNameFromSignature(String signature, boolean removeJava) {
        String tmp = signature.split(";")[0];
        String[] arr = tmp.split("/");
        String fileName = arr[arr.length-1];
        if(removeJava){
            return fileName.replace(".java","");
        }
        return fileName;
    }
    /**
     * Extract the class name
     * @param signature
     * @return
     */
    public static String getClassNameFromSignature(String signature) {
        String tmp = signature.split(";")[0];
        String[] arr = tmp.split("/");
        String fileName = arr[arr.length-1];
        if(fileName.contains("$")){
            String[] tmp2 = fileName.split("\\$");
            fileName = tmp2[tmp2.length-1];
        }
        return fileName.replace(".java","");
    }

    /**
     * Extract the method name
     * @param signature
     * @param arguments
     * @return
     */
    public static String getMethodNameFromSignature(String signature, boolean arguments) {
        String tmp = signature.split(";")[1];
        if(arguments){
            return tmp;
        }
        return tmp.split("#")[0];
    }
    /**
     * to create method signature
     */
    public static String getSignature(String filePath, MethodSignatureInfo method) {
        List<String> list = new ArrayList<>();
        for(ParameterInfo p: method.getParameters()){
            list.add(p.getType());
        }
        String arguments = MyListUtils.flatten(list);
        return getSignature(filePath, method.getName(), arguments);
    }
    /**
     * to create method signature
     */
    public static String getSignature(String filePath, TestCaseInfo testCaseInfo) {
        return getSignature(filePath, testCaseInfo.getTestName(), "");
    }
    /**
     * to get class name
     */
    public static String getClassNameFromPath(String path) {
        String[] tmp = path.split("/");
        String className = tmp[tmp.length-1];
        if(className.contains("$")){
            tmp = className.split("\\$");
            className = tmp[tmp.length-1];
        }
        return className;

    }

    /**
     * Check if this method is a constructor
     */
    public static boolean isConstructor(String signature) {
        String fileName = MyProgramUtils.getClassNameFromSignature(signature);
        String methodName = MyProgramUtils.getMethodNameFromSignature(signature, false);
        return fileName.equals(methodName);
    }
    /**
     * Check if this method is a constructor in test code
     */
    public static boolean isTestConstructor(String sig, String testDir) {
        if(sig.startsWith(testDir)){
            return isConstructor(sig);
        }
        return false;
    }
    /**
     * Get sub class name
     */
    public static String getSubClass(String packageName, String tmpClassName) {
        boolean flg = false;
        StringJoiner sj = new StringJoiner("$");
        for(String s: packageName.split("\\.")){
            if(flg){
                sj.add(s);
            }
            if(s.equals(tmpClassName)){
                flg = true;
            }
        }
        return sj.toString();
    }

    /**
     * to create field signature
     * @param filePath
     * @param lineNo
     * @return
     */
    public static String getFieldSignature(String filePath, Integer lineNo) {
        return filePath+"#"+lineNo;
    }
    /**
     * to create variable signature
     */
    public static String getVariablePath(List<String> imports, String type, String v) {
        String type_ = type.split("\\.")[0];
        for(String i: imports){
            String[] arrI = i.split("\\.");
            String lastI = arrI[arrI.length-1];
            if(lastI.equals(type_)){
                return i+"."+v;
            }
        }
        return null;
    }
    /**
     * Update scopes of variables
     */
    public static void updateScopes(AbstractStatement s, Map<Integer, Set<String>> scopes, Set<String> localVariables) {
        int lineNo = s.getLocationInfo().getStartLine();
        Set<String> scope = scopes.get(lineNo);
        if(scope!=null){
            localVariables.removeAll(scope);
        }
        for(VariableDeclaration v: s.getVariableDeclarations()){
            String[] arr = v.getScope().toString().split("[:\\-]");
            assert arr.length == 4;
            int expireLineNo = Integer.parseInt(arr[2]);
            Set<String> vars = scopes.getOrDefault(expireLineNo, new HashSet<>());
            vars.add(v.getVariableName());
            scopes.put(expireLineNo, vars);
            localVariables.add(v.getVariableName());
        }
    }


    /**
     * To get qualified name
     */
    public static String getQualifiedName(String fileName, String srcDir, String testDir) {
        srcDir = MyProgramUtils.addLastSlash(srcDir);
        fileName = MyProgramUtils.removeUnRelevantDirectoryName(srcDir, fileName);
        fileName = MyProgramUtils.removeUnRelevantDirectoryName(testDir, fileName);
        fileName = fileName.replace(".java", "");
        fileName = fileName.replaceAll("/", "\\.");
        return fileName;
    }
    /**
     * Append slash after directory's name
     */
    public static String addLastSlash(String dir) {
        if (dir==null||dir.equals("")) return "";
        String s = dir.substring(dir.length() - 1);
        if(s.endsWith("/")){
            return dir;
        }
        return dir +"/";
    }

    public static String transform2MavenSignature(String m, String srcDir, String testDir) {
        m = getQualifiedName(m, srcDir, testDir);
        String a = m.split("\\#")[0];
        a = a.replace(";", "#");
        return a;
    }

    public static String removeUnRelevantDirectoryName(String reg, String target){
        reg = reg.replaceAll("\\$\\{.+\\}/", "");
        reg = MyProgramUtils.addLastSlash(reg);
        String[] tmp = target.split(reg, 2);
        if(tmp.length == 1){
            return target;
        }else if(tmp.length == 2) {
            return tmp[1];
        }else{
            System.out.println("reg: "+reg);
            System.out.println("target: "+target);
            System.out.println("length: "+tmp.length);
            throw new AssertionError();
        }
    }
}
