package beans.commit;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;

import javax.persistence.*;
import java.util.Iterator;
/**
 * Chunk is a block of a change.
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "chunk", schema = "commit", uniqueConstraints = { @UniqueConstraint(columnNames = { "chunk_id"}) })
public class Chunk {
    /**
     * this ID is automatically generated for each table by hibernate
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chunk_id", columnDefinition="bigint")
    public long chunk_id;
    /**
     * JGit's edit type including
     *         INSERT,
     *         DELETE,
     *         REPLACE,
     *         EMPTY;
     */
    @Column(name="type", columnDefinition="TEXT")
    private  String type;
    /**
     * The line number where this chunk starts from in the file before change
     */
    @Column(name="oldStartNo")
    private  int oldStartNo;
    /**
     * The line number where this chunk end by in the file before change
     */
    @Column(name="oldEndNo")
    private  int oldEndNo;
    /**
     * The line number where this chunk starts from in the file before change
     */
    @Column(name="newStartNo")
    private  int newStartNo;
    /**
     * The line number where this chunk ends by in the file before change
     */
    @Column(name="newEndNo")
    private  int newEndNo;
    /**
     * The number of added lines
     */
    @Column(name="addedLines")
    private  int addedLines;
    /**
     * The number of deleted lines
     */
    @Column(name="deletedLines")
    private  int deletedLines;

    /**
     * To store this to database,
     * this constructor transports the contents of Edit class provided by JGit to this class.
     *
     * @param edit
     */
    public Chunk(Edit edit) {
        this.type = edit.getType().toString();
        this.oldStartNo = edit.getBeginA();
        this.oldEndNo = edit.getEndA();
        this.newStartNo = edit.getBeginB();
        this.newEndNo = edit.getEndB();
        this.addedLines = edit.getLengthB();
        this.deletedLines = edit.getLengthA();
    }

    public int getNewEndNo() {
        return newEndNo;
    }

    public int getNewStartNo() {
        return newStartNo;
    }

    public int getOldEndNo() {
        return oldEndNo;
    }

    public int getOldStartNo() {
        return oldStartNo;
    }

    public String getType() {
        return type;
    }

    public int getAddedLines() {
        return addedLines;
    }

    public int getDeletedLines() {
        return deletedLines;
    }
    public Chunk(){}
}
