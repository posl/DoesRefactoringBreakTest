package modules.source.execution_trace.impl;

import beans.source.PassedLine;
import modules.build.controller.BuildToolController;
import modules.build.controller.maven.setup.SetUpSureFire;
import modules.source.execution_trace.ExecutionTracer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import utils.file.MyFileUtils;
import utils.general.MyListUtils;
import utils.program.MyProgramUtils;
import utils.setting.SettingManager;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeLogger extends SetUpSureFire implements ExecutionTracer {
    public static String seloggerFile = "selogger-0.2.1.jar";
    /**
     * top directory in the repository
     */
    public String homeDir;
    /**
     * source code directory shown by pom file
     */
    public String srcDir;
    /**
     * test code directory shown by pom file
     */
    public String testDir;

    /**
     * output directory for selogger
     */
    public String seloggerOutputDir = "selogger-output";
    String except = ",e=org/junit,e=com/intellij,e=jdk/nashorn,e=jdk/";
    private String weave = "";//",weave=CALL";/
    public static String bufferSize = "6000";
    public static String commonMemorySize = "8g";
    public static String maxMemorySize = "12g";

    /**
     * read setting file
     * @param mc
     */
    public SeLogger(BuildToolController mc) {
        homeDir = mc.getHomeDir();
//        targetDir = mc.getTargetDir();
        srcDir = mc.getSrcDir(false);
        srcDir = MyProgramUtils.addLastSlash(srcDir);
        testDir = mc.getTestDir(false);
        testDir = MyProgramUtils.addLastSlash(testDir);
        SettingManager sm = mc.getSettingManager();
        String seloggerDir = sm.propManager.getProperty("seloggerDir");
        seloggerFile = sm.propManager.getProperty("seloggerFile");
//        seloggerOutputDir = targetDir+"/"+sm.propManager.getProperty("seloggerOutputDir");
        seloggerOutputDir = sm.propManager.getProperty("seloggerOutputDir");
        except = sm.propManager.getProperty("except");
        String excpt = sm.propManager.getProperty("selogger_exception");
        if(excpt!=null){
            except+=excpt;
        }
        weave = sm.propManager.getProperty("weave");
        bufferSize = sm.propManager.getProperty("bufferSize");
        commonMemorySize = sm.propManager.getProperty("commonMemorySize");
        maxMemorySize = sm.propManager.getProperty("maxMemorySize");

        File original = new File(seloggerDir + seloggerFile);


        File copied = new File(homeDir + seloggerFile);
        try {
            FileUtils.copyFile(original, copied);
        } catch (IOException e) {
            logger.error(e);
            System.out.println(e);
            throw new AssertionError();
        }
    }


    /**
     * this is for testing
     * TODO: make a stub for test
     * @param dir
     */
    public SeLogger(String dir) {
        //this is for test
        homeDir = "";
        srcDir = "src/main/java/";
        testDir = "src/test/java/";
        seloggerOutputDir = "src/test/resources/" + dir;
    }

    /**
     * to handle the existing setting in the pom file
     * @param conf
     */
    @Override
    public void setInheritanceOptions(Xpp3Dom conf) {
        Xpp3Dom x = conf.getChild("argLine");
        if(x==null){
            x = new Xpp3Dom("argLine");
        }
        String val = x.getValue();
        if(val==null){
            addChild(conf, "argLine", this.getSeloggerCommand());
        }else if(val.contains("-Xmx") || val.contains("-Xms")){
            //TODO: extract contents except -Xms and Xmx
            addChild(conf, "argLine", this.getSeloggerCommand());
        }else{
            addChild(conf, "argLine", val + " " + this.getSeloggerCommand());
        }
    }






    @Override
    public void clean() throws IOException {
        MyFileUtils.deleteDirectory(seloggerOutputDir);
    }
    @Override
    public Map<String, Map<Integer, List<PassedLine>>> getPassLinesMap(List<String> testSignatures) {//testSignature, anySignature, line (in the file)
        return SeLoggerReader.getPassLinesMap(homeDir, srcDir, testDir, seloggerOutputDir, testSignatures);
    }


    public String getSeloggerCommand() {
        return "-Xms"+this.commonMemorySize+ " -Xmx"+this.maxMemorySize +  " -javaagent:" + seloggerFile + "="+"format=nearomni,size="+this.bufferSize + weave + except;
    }

}
