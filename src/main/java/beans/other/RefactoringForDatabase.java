package beans.other;

import javax.persistence.*;

import beans.other.code_range.LeftCodeRange4Database;
import beans.other.code_range.RightCodeRange4Database;
import org.refactoringminer.api.Refactoring;
import gr.uom.java.xmi.diff.CodeRange;

import org.refactoringminer.api.RefactoringType;

import java.io.Serializable;
import java.util.*;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "refactoring", schema = "refactoring")
public class RefactoringForDatabase implements Serializable{

	@Id
    @Column(name = "commit_id")
	public String commitId;
	/**
	 * Project name
	 */
	@Id
    @Column(name = "project")
	public String project;
	/**
	 * this hash is provided by Refactoring Miner
	 */
	@Id
	@Column(name = "hash")
	public int hash;
	@Column(name = "RefactoringType")
	@Enumerated(EnumType.STRING)
	public RefactoringType RefactoringType;
	/**
	 * Changes in the previous file
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "commit_id", referencedColumnName = "commit_id", updatable=false,nullable=false),
		@JoinColumn(name = "project", referencedColumnName = "project", updatable=false, nullable=false),
		@JoinColumn(name = "hash", referencedColumnName = "hash", updatable=false,nullable=false) 	}
	)
	public List<LeftCodeRange4Database> leftside = new ArrayList<>();
	/**
	 * Changes in the new file
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "commit_id", referencedColumnName = "commit_id", updatable=false,nullable=false),
		@JoinColumn(name = "project", referencedColumnName = "project", updatable=false, nullable=false),
		@JoinColumn(name = "hash", referencedColumnName = "hash", updatable=false,nullable=false) 	}
	)
	public List<RightCodeRange4Database> rightside = new ArrayList<>();

	public RefactoringForDatabase(Refactoring rf, String commitId, String project){
		this.commitId = commitId;
		this.project = project;
		this.RefactoringType = rf.getRefactoringType();
		this.hash = rf.hashCode();
		for(CodeRange cr : rf.leftSide()){
			LeftCodeRange4Database crd = new LeftCodeRange4Database(cr);
			this.leftside.add(crd);
		}
		 for(CodeRange cr : rf.rightSide()){
		 	RightCodeRange4Database crd = new RightCodeRange4Database(cr);
		 	this.rightside.add(crd);
		 }
	}
	public RefactoringForDatabase(){
	}

}