package beans.commit;

import beans.other.run.Registry;
import org.eclipse.jgit.revwalk.RevCommit;
import utils.bug.MyBugReportUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "commit", schema = "commit")
public class Commit implements Serializable {
//    @Transient
    /**
     * Project name
     */
    @Id
    @Column(name = "project")
    public String project;
    /**
     * Commit sha
     */
    @Id
    @Column(name = "commit_id")
    public String commitId;
    /**
     * Name of committer
     */
    @Column(name = "commiter_name")
    public String committerName;
    /**
     * E-mail address of committer
     */
    @Column(name = "commiter_email")
    public String committerEmail;
    /**
     * Date of commit
     */
    @Column(name = "commit_date")
    public LocalDateTime commitDate;

    /**
     * changed files in this commit
     */
    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumns({ @JoinColumn(name = "project",updatable=false, nullable=false),
            @JoinColumn(name = "commit_id",updatable=false,nullable=false) })
    public List<ChangedFile> changedFileList;

    /**
     * parents commits.
     * When the size of list is more than 1, this is a merge commit.
     * When the size of list is 0, this is the first commit in this repository.
     */
    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "parent", schema = "commit",joinColumns = {@JoinColumn(name = "project", nullable = false),@JoinColumn(name = "commit_id", nullable = false)})
    @Column(name = "parent_commit_id")
    public List<String> parentCommitIds;

    /**
     * Bug reports fixed by this commit.
     * Note that this bug reports is roughly detected by this tools using simple regrex.
     * This study does not use this information.
     */
    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "fixed", schema = "commit",joinColumns = {@JoinColumn(name = "project", nullable = false),@JoinColumn(name = "commit_id", nullable = false)})
    @Column(name = "bug_id")
    public Set<String> fixedReports;
    /**
     * Commit message
     */
    @Column(name = "commit_comment", columnDefinition="TEXT")
    public String commitComment;
    /**
     * Total added lines in this commit.
     */
    @Column(name = "added_lines")
    public Integer addedLines;
    /**
     * Total deleted lines in this commit.
     */
    @Column(name = "deleted_lines")
    public Integer deletedLines;
    /**
     * Total number of changed files in this commit.
     */
    @Column(name = "changed_files")
    public Integer changedFiles;
    /**
     * Boolean if this commit is a merge commit.
     */
    @Column(name = "isMerge_Commit")
    public boolean isMergeCommit;

    public Commit(){
    }

    /**
     * This constructor copy the contents of RevCommit by JGit to this class to store the data.
     * @param revCommit
     */
    public Commit(RevCommit revCommit){
        changedFileList = new ArrayList<>();
        parentCommitIds = new ArrayList<>();
        this.commitId = revCommit.name();
        this.commitDate = LocalDateTime.ofInstant(revCommit.getAuthorIdent().getWhen().toInstant(),
                ZoneId.systemDefault());
        this.commitComment = revCommit.getShortMessage();
        this.committerName = revCommit.getCommitterIdent().getName();
        this.committerEmail = revCommit.getCommitterIdent().getEmailAddress();
        for (RevCommit parent : revCommit.getParents()) {
            this.parentCommitIds.add(parent.getId().name());
        }
        this.fixedReports = MyBugReportUtil.searchIssue(this.commitComment);
        this.isMergeCommit = isMergeCommit();
    }

    public Commit(Registry registry) {
        this.commitId = registry.commitId;
        this.project = registry.project;
    }

    public boolean isMergeCommit(){
        return this.parentCommitIds.size() > 1;
    }

    /**
     * Calculate the number of total lines in this commit
     */
    public void setLines() {
        addedLines = 0;
        deletedLines = 0;
        changedFiles = changedFileList.size();
        for(ChangedFile cf: changedFileList){
            addedLines += cf.addedLines;
            deletedLines += cf.deletedLines;
        }
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof Commit){
            Commit c = (Commit) o;
            return this.commitId.equals(c.commitId)&&this.project.equals(c.project);
        }
        return false;
    }
    @Override
    public int hashCode(){
        return Objects.hash(project, commitId);
    }
}
