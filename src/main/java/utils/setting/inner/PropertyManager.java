package utils.setting.inner;


import utils.file.MyFileReadWriteUtils;
import utils.log.MyLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

/**
 * Setting file reader
 */
public class PropertyManager implements Serializable {
    MyLogger logger = MyLogger.getInstance();
    private static final String HOME_DIR = "home_dir";
    private static final String SETTINGS_DIR = "resource_settings_dir";
    private static final String PROJECTS_INI_DIR = "projects_ini_dir";
    private static final String PROGRAMS_INI_DIR = "programs_ini_dir";

    private Properties properties;
    public Project project;
    public String caller;
    public PropertyManager(String executedMethodName){
        this.caller = executedMethodName;
        this.properties = new Properties();

        this.addProperty(HOME_DIR, System.getProperty("user.dir")+"/");
        this.addProperty(SETTINGS_DIR, this.getProperty(HOME_DIR)+ "/settings");
        //
        this.addProperty(PROJECTS_INI_DIR, this.getProperty(SETTINGS_DIR)+ "/projects");
        this.addProperty(PROGRAMS_INI_DIR, this.getProperty(SETTINGS_DIR)+ "/programs");

        this.loadProperty(this.getProperty(SETTINGS_DIR), "ini");
        showProperties();
    }
    private void addProperty(String key, String value){
        this.properties.setProperty(key, value);
    }
    public String getProperty(final String key){
        return this.properties.getProperty(key);
    }

    public void showProperties() {
        for (String s:this.properties.stringPropertyNames()) {
            System.out.print(s);
            System.out.print(" = ");
            System.out.println(this.getProperty(s));
        }
    }

    private void loadProperty(String dir, String ext){
        List<String> list = MyFileReadWriteUtils.getFileList(dir, ext);
        for(String path : list){
            Properties property = new Properties();
            try {
                property.load(new FileInputStream(new File(path)));
                properties.putAll(property);
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error();
            }
        }
    }

    public String getProjectName(){
        return project.name;
    }

    public Project loadProjectProperties(String name){
        try {
            String propPath = this.getProperty(PROJECTS_INI_DIR)+"/"+name +".pj";
            Properties prop = new Properties();
            prop.load(new FileInputStream(new File(propPath)));
            this.properties.putAll(prop);
            Project project = new Project(prop);
            return project;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        } catch (IOException e) {
            logger.error(e);
            throw new AssertionError();
        }
    }

    public List<String> getProjectList() {
        List<String> list = MyFileReadWriteUtils.getFileList(this.getProperty(PROJECTS_INI_DIR),".pj");
        for(int i=0;i<list.size();i++){
            String path = list.get(i);
            String name = new File(path).getName().replace(".pj","");
            list.set(i, name);
        }
        return list;
    }
    public String getSpecificProject(String name) {
        return this.getProperty(PROJECTS_INI_DIR)+"/"+name+".pj";
    }
}
