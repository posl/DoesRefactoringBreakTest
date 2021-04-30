package modules.build.compile;

import modules.build.controller.BuildToolController;
import beans.source.MethodDefinition;
import modules.source.structure.StructureAnalyzer;
import utils.log.MyLogger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class finds compile errors by reading Maven's output
 */
public class CompileErrorFinder {
    static MyLogger logger = MyLogger.getInstance();
    public class ErrorMethod{
        private String path;
        private Integer line;
        private String reason;
        private MethodDefinition definition;


        public void setPath(String path){
            this.path=path;
        }
        public String getPath(){
            return this.path;
        }
        public void setLine(String line){
            this.line=Integer.parseInt(line);
        }
        public Integer getLine(){
            return this.line;
        }
        public void setReason(String reason){
            this.reason=reason;
        }
        public String getReason(){
            return this.reason;
        }
        public void setDefinition(MethodDefinition name){
            this.definition=name;
        }
        public MethodDefinition getDefinition(){
            return this.definition;
        }
        public String toString(){
            return this.path+":"+this.line;
        }

        @Override
        public boolean equals(Object o){
            if (o instanceof ErrorMethod){
                return o.toString().equals(this.toString());
            }
            return false;
        }
        public String getClassName(){
            if(this.definition!=null){
                return this.definition.className;
            }
            return null;
        }
        public String getMethodName(){
            if(this.definition!=null){
                return this.definition.methodName;
            }
            return null;
        }
    }

    /**
     * Read error log provided by Maven
     */
    Pattern pattern = Pattern.compile("\\[ERROR\\]\\s(.+):\\[(\\d+),\\d+\\]\\s(.+)");
    private List<ErrorMethod> getErrorMethodsInLog(List<String> errors) {
        List<ErrorMethod> list = new ArrayList<ErrorMethod>();
        for(String e:errors){
            if(e.equals("[ERROR] COMPILATION ERROR : ")){
                continue;
            }
            Matcher m = pattern.matcher(e);
            if(m.find()){
                if(m.groupCount()!=3){
                    logger.error(e);
                    throw new AssertionError();
                }
                ErrorMethod em = new ErrorMethod();
                em.setPath(m.group(1));
                em.setLine(m.group(2));
                em.setReason(m.group(3));
                if(!list.contains(em)){
                    list.add(em);
                }
            }
        }
        return list;
    }
    /**
     * Specify the method that has errors from maven logs
     */
    private List<ErrorMethod> specifyMethod(BuildToolController mavenX, List<ErrorMethod> li){
        StructureAnalyzer cs = new StructureAnalyzer(mavenX);//every time the method is deleted, it should be scanned
        cs.scan();//to get methods definitions
        logger.trace("Num of Error methods: "+li.size());
        Set<String> failedInitializerFileSet= new HashSet<>();
        Set<String> errorSignatures = new HashSet<>();
        for (ErrorMethod i:li){
            //get method definition
            i.definition = cs.getMethod(i.getPath().replace(mavenX.getHomeDir(), ""), i.getLine());
            if(i.definition!=null) {//inside of the method
                if (i.definition.isTestInitializer()) {//
                    failedInitializerFileSet.add(i.getPath());
                }
                errorSignatures.add(i.definition.getSignature());
            }
        }
        //if test initializers (e.g., before) have errors, regard that all the test methods have compiler errors
        if (failedInitializerFileSet.size()>0){
            for(MethodDefinition md: cs.getAllMethods()){
                if (!md.isTestCase()) continue;//only test code
                if (errorSignatures.contains(md.getSignature())) continue;
                for(String s: failedInitializerFileSet){
                    if (s.contains(md.getFileName())){
                        ErrorMethod em = new ErrorMethod();
                        em.path = s;
                        em.line = md.start;
                        em.definition = md;
                        em.reason = "this method is deleted because the initializers had compile errors";
                        li.add(em);
                    }
                }
            }
        }


        return li;
    }

    /**
     * find and return error methods information
     * @param mavenX
     * @return
     */
    public List<ErrorMethod> getErrorMethods(BuildToolController mavenX){
        List<ErrorMethod> li = this.getErrorMethodsInLog(mavenX.getErrors());
        return specifyMethod(mavenX, li);
    }


}
