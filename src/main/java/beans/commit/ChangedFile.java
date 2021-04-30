package beans.commit;


import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.eclipse.jgit.patch.FileHeader;
import utils.log.MyLogger;

/**
 * ChangedFile provides information about a changed file
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "file", schema = "commit", uniqueConstraints = { @UniqueConstraint(columnNames = { "change_file_id"}) })
public class ChangedFile implements Serializable {
    @Transient
    MyLogger logger = MyLogger.getInstance();
    @Transient
    private static final long serialVersionUID = 1L;
    /**
     * this ID is automatically generated for each table by hibernate
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="change_file_id", columnDefinition="bigint")
    public long changeFileId;
    /**
     * path after changes
     */
    @Column(name="newPath", columnDefinition="TEXT")
    public String newPath;
    /**
     * path before changes
     */
    @Column(name="oldPath", columnDefinition="TEXT")
    public String oldPath;

    /**
     * The number of lines that are added
     */
    @Column(name="addedLines", columnDefinition="TEXT")
    public int addedLines;
    /**
     * The number of lines that are deleted
     */
    @Column(name="deletedLines", columnDefinition="TEXT")
    public int deletedLines;
    /**
     * The mode of change types. See Class Definition
     */
    public Mode mode;

    /**
     * This method receives FileHeader given by JGit.
     *
     * @param header
     */
    public ChangedFile(FileHeader header) {
        this.newPath = header.getNewPath();
        this.oldPath = header.getOldPath();
        if (!this.newPath.equals("/dev/null") && !this.oldPath.equals("/dev/null")) {
            this.mode = ChangedFile.Mode.MODIFY;
        } else if (this.oldPath.equals("/dev/null")) {
            this.mode = ChangedFile.Mode.ADD;
        } else if (this.newPath.equals("/dev/null")) {
            this.mode = ChangedFile.Mode.DELETE;
        } else {
            logger.error(this.oldPath + ":" + this.newPath);
            throw new Error();
        }
    }

    /**
     * The type of change in File
     * If the file is added: ADD
     * If the file is deleted: DELETE
     * If the file is modified: MODIFY
     * This enum is determined by JGit
     */
    public enum Mode{
        ADD, DELETE, MODIFY
    }

    /**
     * Chunks in this change
     */
    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumns({ @JoinColumn(name = "change_file_id",updatable=false, nullable=false) })
    @Column(name = "chunk")
    public List<Chunk> chunks;

    public void setLines(){
        addedLines = 0;
        deletedLines = 0;
        for(Chunk c: chunks){
            addedLines += c.getAddedLines();
            deletedLines += c.getDeletedLines();
        }
    }
    public ChangedFile(){}
}

