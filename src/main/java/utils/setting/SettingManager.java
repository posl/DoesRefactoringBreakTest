package utils.setting;


import utils.log.MyLogger;
import utils.setting.inner.*;

import java.io.Serializable;

/**
 * This class manage three managers about property, position (directory), project.
 */
public class SettingManager implements Serializable {
    /**
     * This class loads setting file.
     */
    public PropertyManager propManager;
    /**
     * This class returns directly information using PropertyManager.
     */
    public PositionManager posManager;
    /**
     * This class returns project information using PropertyManager.
     */
    protected ProjectManager pjManager;
    String arg;
    MyLogger logger = MyLogger.getInstance();


    public SettingManager(String[] args){
        if(args!=null&&args.length==1){
            arg=args[0];
        }
        String[] fullPath = new Exception().getStackTrace()[1].getClassName().split("\\.");
        String executedMethodName = fullPath[fullPath.length-1];
        logger.trace("executed by "+executedMethodName);
        propManager = new PropertyManager(executedMethodName);
        pjManager = new ProjectManager(propManager, arg);
        posManager = new PositionManager(propManager, pjManager);
    }
    public Project getProject(){
        return pjManager.getProject();
    }

    public String getOutputDir(){
        return this.posManager.getDataDirAbsPath();
    }

    public String getSetting(String set){
        return propManager.getProperty(set);
    }

    public String getBranch() {
        return propManager.getProperty("branch");
    }

    public String getLogDir() {
        return "logs";
    }

    public static String repoDir="repos";
    public String getRepoDir() {
        return repoDir;
    }
}
