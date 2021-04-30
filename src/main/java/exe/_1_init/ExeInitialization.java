package exe._1_init;

import beans.commit.Commit;
import beans.other.RefactoringForDatabase;
import modules.git.GitController;
import modules.refactoring.detect.RefactoringMinerController;
import org.refactoringminer.api.Refactoring;
import utils.db.Dao;
import utils.exception.NoParentsException;
import utils.file.MyFileUtils;
import utils.log.MyLogger;
import utils.setting.SettingManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class register the targets that we analyze.
 */
public class ExeInitialization {
	static MyLogger logger = MyLogger.getInstance();
    static Dao<RefactoringForDatabase> refDao = new Dao<>(RefactoringForDatabase.class);
    static Dao<Commit> commitDao = new Dao<>(Commit.class);

    public static void main(String[] args) throws NoParentsException {
        //Read setting file. If the args is null, users can select projects via command line
        SettingManager sm = new SettingManager(args);
        //Delete old files
        MyFileUtils.deleteDirectory(sm.getOutputDir(), true);
		GitController gitX = new GitController(sm, "/x/");
		//collect commits from repositories
		List<Commit> commits = gitX.getAllCommits();
        store(sm, gitX, commits);
    }

    /**
     * This method assigns data store tasks to methods storing each type of data
     * @param sm
     * @param gitX
     * @param commits
     * @throws NoParentsException
     */
    public static void store(SettingManager sm, GitController gitX, List<Commit> commits) throws NoParentsException {
        int i = 1;
        int size = commits.size();
        Set<Commit> commitHasRefactoring = new HashSet<Commit>();
        refDao.init();// to use hibernate
        commitDao.init();// to use hibernate
        for(Commit commit : commits){
            System.out.println("************" + i + "/" + size + "************@" + commit.commitId);
            logger.trace("************" + i + "/" + size + "************@" + commit.commitId);
            try{
                //store commit information
                registerCommit(sm, gitX, commit.commitId);
                //mine refactoring information
                List<Refactoring> refactoringResults = RefactoringMinerController.getRefactoringInstanceAtCommit(gitX.getRepo(), commit.commitId);
                if(!refactoringResults.isEmpty()){
                    logger.trace(commit.commitId + " has Refactoring");
                    commitHasRefactoring.add(commit);
                }
                for (Refactoring rf : refactoringResults) {
                    registerRefactoring(sm, rf, commit.commitId);
                }
            }catch(AssertionError e){
                System.err.println(e);
            }
            i++;
        }
        //Register targets to be analyzed
        RegistryStoreManager.register(sm, commitHasRefactoring);
        refDao.close();
        commitDao.close();
        logger.info("********Complete the program********");
    }

    /**
     * The Refactoring class provided by RefactoringMiner will be transformed into a wrapper class
     * to store it into the database
     * @param sm
     * @param rf
     * @param commitId
     */
    private static void registerRefactoring(SettingManager sm, Refactoring rf, String commitId) {
        RefactoringForDatabase rfd = new RefactoringForDatabase(rf, commitId, sm.getProject().name);
        refDao.insert(rfd);
    }

    /**
     * This method extracts commit information and register it.
     * @param sm
     * @param gitX
     * @param commitId
     * @throws NoParentsException
     */
    private static void registerCommit(SettingManager sm, GitController gitX, String commitId) throws NoParentsException {
        gitX.checkout(commitId, false, true);
        Commit commit = gitX.getCommit(true);
        commit.project = sm.getProject().name;
        commitDao.insert(commit);
    }
}

    