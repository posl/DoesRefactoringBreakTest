package modules.build.controller;

import beans.commit.Commit;
import modules.build.controller.maven.setup.SetElementOfPom;
import org.apache.maven.shared.invoker.InvocationResult;
import utils.exception.NoParentsException;
import utils.exception.NoSureFireException;
import utils.exception.NoTargetBuildFileException;
import utils.log.MyLogger;
import utils.setting.SettingManager;

import java.util.List;

public interface BuildToolController {
    MyLogger logger = MyLogger.getInstance();

    /**
     * check out the target revision
     * @param commitId
     */
    void checkout(String commitId);
    /**
     * check out the parent revision
     * @param commitId
     */
    void checkout(String commitId, boolean parent);

    /**
     * get test code directory path from root
     * @return
     */
    String getTestDir();
    /**
     * get test code directory path from root (if the argument is true)
     * @return
     */
    String getTestDir(boolean absolutePath);
    /**
     * get source code directory path from root
     * @return
     */
    String getSrcDir();
    /**
     * get source code directory path from root (if the argument is true)
     * @param absolutePath
     * @return
     */
    String getSrcDir(boolean absolutePath);
    /**
     * read pom file
     * @throws NoTargetBuildFileException
     */
    void readBuildFile() throws NoTargetBuildFileException;
    /**
     * get target directory path from root
     * @return
     */
    String getTargetDir();
    /**
     * get target directory path from root (if the argument is true)
     * @return
     */
    String getTargetDir(boolean absolutePath);
    /**
     * Build tool execution
     * @param goal
     * @param option
     * @return
     */
    InvocationResult run(String goal, String option);
    /**
     * get the output by surefire
     * @return
     */
    String getSureFireOutputDir();

    /**
     * get commit id using GitController
     * @return
     */
    String getCommitId();

    /**
     * get directory to store JUnit outputs
     * @return
     */
    String getXMLStoreDir();
    /**
     * get errors
     * @return
     */
    List<String> getErrors();

    /**
     * get home directory
     * @return
     */
    String getHomeDir();
    /**
     * get git repository directory
     * @return
     */
    String getRepoDir();
    /**
     * get Commits provided by GitController
     * @return
     */
    Commit getCommit();
    /**
     * get source directory written in pom file
     * @return
     */
    String getSrcDirSetting();
    /**
     * get test directory written in pom file
     * @return
     */
    String getTestDirSetting();
    /**
     * insert plugins
     * @param dynamicAnalyzer
     * @throws NoSureFireException
     */
    void setupExtraPom(SetElementOfPom dynamicAnalyzer) throws NoSureFireException;

    /**
     * get parent commit id
     * @return
     * @throws NoParentsException
     */
    String getParentCommitId() throws NoParentsException;

    String getProject();
    SettingManager getSettingManager();
}
