package beans.trace;

import beans.other.run.Registry;
import beans.source.PassedLine;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "trace", schema = "trace",
        indexes = { @Index( name="trace_idx", columnList="project,commit_id,signature,is_cross") }
        )
public class ExecutionTrace implements Serializable {
    /**
     * Project name
     */
    @Id
    @Column(name = "project", columnDefinition="TEXT")
    public String project;
    /**
     * Commit SHA
     */
    @Id
    @Column(name = "commit_id", columnDefinition="TEXT")
    public String commitId;
    /**
     * Method signature (i.e., file path+class+method name+parameter type)
     */
    @Id
    @Column(name = "signature", columnDefinition="TEXT")
    public String signature;
    /**
     * Line number in the file (absolute number)
     */
    @Id
    @Column(name = "line_no")
    public Integer lineNo;
    /**
     * When cross execution, this val is True
     */
    @Id
    @Column(name = "is_cross")
    public boolean isCross;

    /**
     * List of the path exercised by this test
     */
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "path", schema = "trace",
            joinColumns = {
                    @JoinColumn(name = "project", referencedColumnName = "project", nullable = false),
                    @JoinColumn(name = "commit_id", referencedColumnName = "commit_id", nullable = false),
                    @JoinColumn(name = "signature", referencedColumnName = "signature", nullable = false),
                    @JoinColumn(name = "line_no", referencedColumnName = "line_no", nullable = false),
                    @JoinColumn(name = "is_cross", referencedColumnName = "is_cross", nullable = false)
    }
    ,indexes = { @Index( name="path_idx", columnList="project,commit_id,signature,is_cross")}
    )
    @Column(name = "path", columnDefinition="TEXT")
    @OrderColumn(name="invoked_order")
    public List<String> passes;

    public ExecutionTrace(Registry registry, String testSignature, Integer i) {
        this.project = registry.project;
        this.commitId = registry.commitId;
        this.signature = testSignature;
        this.lineNo = i;
        this.isCross = registry.isCross();
        this.passes = new ArrayList<>();
    }
    public ExecutionTrace(){}
    public void add(List<PassedLine> lines) {
        for(PassedLine p: lines){
            passes.add(p.toString());
        }
    }
    public String toString(){
        return this.project+", "+this.commitId+", "+this.signature+", "+this.lineNo+", "+this.passes;
    }
}