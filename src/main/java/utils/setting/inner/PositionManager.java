package utils.setting.inner;



import utils.file.MyFileNameUtils;

import java.io.Serializable;

/**
 * Return directory name customized in users environment while reading setting file
 */
public class PositionManager implements Serializable {
    PropertyManager propManager;
    ProjectManager pjManager;
    String homeDir;
    String dataDir;

    public PositionManager(PropertyManager propManager, ProjectManager pjManager){
        this.propManager = propManager;
        this.pjManager = pjManager;
        this.homeDir = propManager.getProperty("home_dir");//get home direcotory
        this.dataDir = this.getPath("data_dir");// to get data dir shown in setting file
    }

    public String getHomeDirAbsPath(){
        return homeDir;
    }
    public String getDataDirAbsPath(){
        return dataDir+ this.pjManager.getProjectName()+"/";
    }

    /* COMMON: Absolute path of Files */

    /**
     * replace the variables with information in setting file
     * @param placement
     * @return
     */
    private String setCommonReplacement(String placement){
        placement = MyFileNameUtils.setPlacement(placement, this.getHomeDirAbsPath(), "[HOME_DIR]");
        placement = MyFileNameUtils.setPlacement(placement, this.getDataDirAbsPath(), "[DATA_DIR]");
        placement = MyFileNameUtils.setPlacement(placement, propManager.caller, "[CALLER]");
        placement = MyFileNameUtils.setPlacement(placement, pjManager.getProjectName(), "[PROJECT_NAME]");
        placement = MyFileNameUtils.setPlacement(placement, pjManager.getProjectAbb(), "[PROJECT_ABB]");
//        placement = MyFileNameUtils.setPlacement(placement, pjManager.getProject().cvs.getRepoName(), "[REPO_NAME]");
        return placement;
    }

    private String getPath(String propertyName){
        String placement = propManager.getProperty(propertyName);
        return this.setCommonReplacement(placement);
    }

}
