package beans.RQ;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Set;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name="rq2", schema = "rq")
public class AddedTestData implements Serializable{
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
	public String commit_id;
	/**
	 * method signature
	 */
	@Id
	@Column(name = "signature", columnDefinition="TEXT")
	public String signature;
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "project",referencedColumnName = "project",updatable=false,nullable=false),
        @JoinColumn(name = "commit_id",referencedColumnName = "commit_id",updatable=false,nullable=false),
		@JoinColumn(name = "signature",referencedColumnName = "signature",updatable=false,nullable=false) 	})
	public Set<Refactoring4AddedTests> refset;
	/**
	 * refactoring type
	 */
	@Column(name = "type", columnDefinition="TEXT")
	public String type;
	/**
	 * github url
	 */
	@Column(name = "url", columnDefinition="TEXT")
	public String url;
	/**
	 * The size of this change
	 */
	@Column(name = "size", columnDefinition = "bigint")
	public Integer size;



	public AddedTestData(String project, String commit_id, String signature, Set<Refactoring4AddedTests> refset, String type, String url){
		this.project = project;
		this.commit_id = commit_id;
		this.signature = signature;
		this.refset = refset;
		this.type = type;
		this.url = url;
		this.size = refset.size();
	}

	public AddedTestData(){
	}
	
}
