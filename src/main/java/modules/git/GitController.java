package modules.git;

import beans.commit.Commit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.refactoringminer.api.GitService;
import utils.exception.NoParentsException;
import utils.file.MyFileUtils;
import utils.log.MyLogger;
import utils.setting.SettingManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitController {
    MyLogger logger = MyLogger.getInstance();
    /**
     * project name
     */
    public String project;
    /**
     * target id of the commit to be cloned
     */
    public String commitId;
    /**
     * repository directory path to be cloned
     */
    private final String repoDir;
    /**
     * GitHub's url (Other service is also acceptable)
     */
    private final String url;
    /**
     * The Implement of JGit provided by Refactoring Miner
     */
    private final GitService git;
    /**
     * This is a class in JGit, which is used to provide
     * some functionalities that refactoring miner does not have
     */
    private Repository repo;

    public SettingManager sm;

    public GitController(SettingManager sm, String dir){
        this.repoDir = sm.posManager.getHomeDirAbsPath() +sm.getRepoDir()+dir+sm.getProject().name+"/";
        this.url = sm.getProject().url;
        this.git = new GitServiceImpl2();
        this.init();
        this.sm = sm;
        this.project = sm.getProject().name;
    }
    private void init(){
        try {
            this.deleteRepo();
            this.repo = git.cloneIfNotExists(this.repoDir, this.url);
        } catch (Exception e) {
            logger.error(e);
            throw new AssertionError();
        }
    }
    public Repository getRepo(){
        return this.repo;
    }

    /**
     * hard reset to HEAD
     */
    protected void hardReset(){
        try {
            new Git(this.repo).reset().setMode(ResetCommand.ResetType.HARD).call();
        } catch (GitAPIException e) {
            logger.error(e);
            throw new AssertionError();
        }
    }
    /**
     * hard reset to a revision
     */
    protected void hardReset(String hash){
        try {
            new Git(this.repo).reset().setRef(hash).setMode(ResetCommand.ResetType.HARD).call();
        } catch (GitAPIException e) {
            logger.error(e);
            throw new AssertionError();
        }
    }

    /**
     * check out a revision
     * when parent is true, the parent revision will be checkout.
     * If the id is the first revision, NoParentsException will be thrown.
     * When ignoreNoParents is true, this exception won't be thrown.
     * @param id
     * @param parent
     * @param ignoreNoParents
     * @throws NoParentsException
     */
    public void checkout(String id, boolean parent, boolean ignoreNoParents)throws NoParentsException{
        RevCommit commit = this.findObjectId(id, ignoreNoParents);
        if (parent) {
            commit = this.findObjectId(commit.getParent(0).getName());
        }
        this.commitId = commit.getName();
        try {
            this.hardReset(commit.getId().getName());
            git.checkout(this.repo, commit.getId().getName());
        } catch(CheckoutConflictException ce){
            System.out.println(ce.getMessage());
            this.deleteRepo();
            try{
                this.repo = git.cloneIfNotExists(this.repoDir, this.url);
                this.hardReset(commit.getId().getName());
                git.checkout(this.repo, commit.getId().getName());
                System.err.println("CheckoutConflictException");
            }catch (Exception e){
                System.err.println("Cannot clone");
                throw new AssertionError();
            }
        } catch (Exception e){
            logger.error(e);
            throw new AssertionError();
        }
    }

    public void checkout(String id, boolean ignoreNoParents)throws NoParentsException{
        this.checkout(id, false, ignoreNoParents);
    }
    public void checkout(String id)throws NoParentsException{
        this.checkout(id, false, false);
    }

    /**
     * get RevCommit in JGit based on id in String
     * @param commitId
     * @return
     * @throws NoParentsException
     */
    protected RevCommit findObjectId(String commitId)throws NoParentsException {
        return findObjectId(commitId, false);
    }
    protected RevCommit findObjectId(String commitId, boolean ignoreNoParents)throws NoParentsException{
        try {
            ObjectId evalCommitId = this.repo.resolve(commitId);
            try (RevWalk walk = new RevWalk(this.repo)) {
                RevCommit evalCommit = walk.parseCommit(evalCommitId);
                if(evalCommit.getParentCount() == 0){
                    if(ignoreNoParents){
                        walk.dispose();
                        return evalCommit;
                    }
                    throw new NoParentsException();
                }
                walk.parseCommit(evalCommit.getParent(0).getId());
                walk.dispose();
                return evalCommit;
            }
        } catch (IOException ex) {
            logger.error("Unable to obtain " + commitId + " commit!");
            logger.error(ex);
            throw new AssertionError();
        }
    }
    public String getRepoDir(){
        return this.repoDir;
    }

    /**
     * create a Commit from RevCommit
     * @param ignoreNoParents
     * @return
     * @throws NoParentsException
     */
    public Commit getCommit(boolean ignoreNoParents)throws NoParentsException{
        RevCommit commit = this.findObjectId(commitId, ignoreNoParents);
        return ((GitServiceImpl2)this.git).getCommit(repo, commit);
    }
    public Commit getCommit()throws NoParentsException{
        return getCommit(false);
    }

    public void deleteRepo(){
        try {
            MyFileUtils.deleteDirectory(this.repoDir);
        } catch (IOException e) {
        }
    }

    /**
     * get parent's commit id
     * @return
     * @throws NoParentsException
     */
    public String getParentCommitId() throws NoParentsException {
        RevCommit commit = this.findObjectId(commitId);
        return commit.getParent(0).getName();
    }

    /**
     * get a list of all the commits from all branches
     * @return
     */
    public List<Commit> getAllCommits() {
        List<Commit> commits = null;
        try {
            commits = ((GitServiceImpl2)git).getAllCommits(repo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return commits;
    }

    /**
     * receive a list of hash ids and returns a list of commits
     * @param targetsId
     * @return
     * @throws NoParentsException
     */
    public List<Commit> getCommits(List<String> targetsId) throws NoParentsException {
        List<Commit> commits = new ArrayList<>();
        for(String id: targetsId){
            commits.add(getCommit(id));
        }
        return commits;
    }
    /**
     * receive a hash id and returns a commit
     * @param targetsId
     * @return
     * @throws NoParentsException
     */
    public Commit getCommit(String targetsId) throws NoParentsException {
        RevCommit commit = this.findObjectId(targetsId, true);
        return ((GitServiceImpl2)this.git).getCommit(repo, commit);
    }
}
