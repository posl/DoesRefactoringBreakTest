package exe._3_analyze.store;

import beans.other.run.Registry;
import beans.source.MethodDefinition;
import modules.source.structure.StructureAnalyzer;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * This class manages a list of MethodDefinition4DB.
 */
@Entity
@Table(name = "ia", schema = "ia")
public class Methods4DB implements Serializable {
    /**
     * project name
     */
    @Id
    @Column(name="project")
    public String project;
    /**
     * commit id
     */
    @Id
    @Column(name="commit_id")
	public String commitId;
    /**
     * Revision
     * X: Target to be analyzed
     * X-1: the parent of the target
     */
    @Id
    @Column(name="type")
    public String type;//X or X_1

    /**
     * List of method information
     */
    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumns({ @JoinColumn(name = "project",referencedColumnName = "project",updatable=false,nullable=false),
            @JoinColumn(name = "commit_id",referencedColumnName = "commit_id",updatable=false,nullable=false),
            @JoinColumn(name = "type",referencedColumnName = "type",updatable=false,nullable=false) })
    public List<MethodDefinition4DB> methods;

    //To store
    public Methods4DB(Registry registry, StructureAnalyzer sa, String type){
        this.project = registry.project;
        this.commitId = registry.commitId;
        this.type = type;
        this.methods = new ArrayList<>();
        for (MethodDefinition md: sa.getAllMethods()){
            this.methods.add(new MethodDefinition4DB(md));
        }

    }
    //To select
    public Methods4DB(){
        //This is for hibernate
    }

}
