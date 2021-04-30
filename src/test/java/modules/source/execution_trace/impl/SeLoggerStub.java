package modules.source.execution_trace.impl;

import beans.source.PassedLine;
import modules.build.controller.maven.MavenController;

import java.io.IOException;
import java.util.Map;

public class SeLoggerStub extends SeLogger{

    public SeLoggerStub(MavenController mc) {
        super(mc);
    }
    public SeLoggerStub(String s){
        super(s);
    }
    public String getArguments(String s){
        return SeLoggerReader.getArguments(s);
    }
    public String getMethodFile(){
        return homeDir+seloggerOutputDir+"/"+"methods.txt";
    }
    public String getDataFile(){
        return homeDir+seloggerOutputDir+"/"+"dataids.txt";
    }
    public String getCallFile(){
        return homeDir+seloggerOutputDir+"/"+"recentdata.txt";
    }
    public Map<String, String>  getMethods(String classesFile, String methodsFile) throws IOException {
        return SeLoggerReader.getMethods(srcDir, testDir, classesFile, methodsFile);
    }
    public Map<String, String> getProcess(String dataIdsFile) throws IOException {
        return SeLoggerReader.getProcess(dataIdsFile);
    }
    public Map<Long, PassedLine> getPasses(String callsFile, Map<String, String> methods, Map<String, String> programs) throws IOException {
        return SeLoggerReader.getPasses(callsFile, methods, programs);
    }
    public String getClassFile() {
        return homeDir+seloggerOutputDir+"/"+"classes.txt";

    }
}
