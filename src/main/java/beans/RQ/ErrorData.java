package beans.RQ;

import javax.persistence.*;

import beans.test.rowdata.TestInfo;

import java.io.Serializable;
import java.util.Set;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name="rq1", schema = "rq")
public class ErrorData implements Serializable {
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
	@Id
	@Column(name = "signature", columnDefinition="TEXT")
	public String signature;
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "project",referencedColumnName = "project",updatable=false,nullable=false),
        @JoinColumn(name = "commit_id",referencedColumnName = "commit_id",updatable=false,nullable=false),
		@JoinColumn(name = "signature",referencedColumnName = "signature",updatable=false,nullable=false) 	})
	public Set<Refactoring4TestResults> refset;
	@Column(name = "result", columnDefinition="TEXT")
	public String result;
	@Column(name = "errorlog", columnDefinition="TEXT")
	public String errorLog;
	@Column(name = "url", columnDefinition="TEXT")
	public String url;
	@Column(name = "size", columnDefinition = "bigint")
	public Integer size;




	public ErrorData(String project, String commit_id, TestInfo test, Set<Refactoring4TestResults> refset, String url){
		this.project = project;
		this.commit_id = commit_id;
		this.signature = test.signature;
		this.refset = refset;
		this.result = test.testResult.type.name();
		if(test.testResult.errorMessage != null)
			this.errorLog = test.testResult.errorMessage.contents;
		else
			this.errorLog = null;
		this.url = url;
		this.size = refset.size();
	}

	public ErrorData(){
	}

}
