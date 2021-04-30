package beans.other.run;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * This class is the base for all registry class.
 * This class is used to store/get data from Database (hibernate)
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Registry implements Serializable {
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
     * The status of build/analysis
     * 0: Waiting
     * 1: Running
     * 2: Complete
     * 3: Normal Terminate due to Exception
     * -1: Error termination
     */
    public int resultCode = 0;

    /**
     * Exception/Error name
     */
    public String resultMessage = null;
    /**
     * Exception/Error message
     */
    @Column(name = "errorMessage", columnDefinition="TEXT")
    public String errorMessage = null;

    /**
     * Date that analysis/build starts
     */
     @Column(name = "start_date")
     public LocalDateTime startDate;
    /**
     * Date that analysis/build ends
     */
     @Column(name = "end_date")
     public LocalDateTime endDate;


    public Registry(String project, String commitId){
        this.project = project;
        this.commitId = commitId;
    }
    public Registry(){
    }
    abstract public boolean isCross();
}
