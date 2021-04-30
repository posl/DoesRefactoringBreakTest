package modules.refactoring.detect;


import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import utils.log.MyLogger;

import java.util.*;

/**
 * This is the wrapper of Refactoring Miner
 */
public class RefactoringMinerController {
    static MyLogger logger = MyLogger.getInstance();

    /**
     * get refactoring list from the branch
     * key: hash id
     * value: refactoring list
     * @param repo
     * @param branch
     * @return
     */
    public static Map<String, List<Refactoring>> getRefactoringInstance(Repository repo, String branch) {
        Map<String, List<Refactoring>> map = new HashMap<>();


        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
        try {
            //miner.detectAll(repo, "master", new RefactoringHandler() {
            miner.detectAll(repo, branch, new RefactoringHandler() {
                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                    map.put(commitId, refactorings);
                }
            });
        } catch (Exception e) {
            logger.error(e);
            throw new AssertionError();
        }
        return map;

    }

    /**
     * get a list of refactoring in a commit
     * @param repo
     * @param commitId
     * @return
     */
    public static List<Refactoring> getRefactoringInstanceAtCommit(Repository repo, String commitId) {
        List<Refactoring> list = new ArrayList<>();

        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
        try {
            miner.detectAtCommit(repo, commitId, new RefactoringHandler() {
                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                    list.addAll(refactorings);
                }
            });
        } catch (Exception e) {
            logger.error(e);
            throw new AssertionError();
        }
        return list;

    }


}
