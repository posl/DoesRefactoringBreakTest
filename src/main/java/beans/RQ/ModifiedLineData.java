package beans.RQ;

import javax.persistence.*;

import exe._4_aggregate.ModifiedLines.LineInfo;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name="rq3", schema = "rq")
public class ModifiedLineData implements Serializable{
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
	public Set<Refactoring4ModifiedLines> refset;
	@Column(name = "impactedlines")
	public Integer impactedLines;
	@Column(name = "changedlines")
	public Integer changedLines;
	@Column(name = "alllines")
	public Integer allLines;
	@Column(name = "allchangedlines")
	public Integer allChangedLines;
	@Column(name = "tracechanged")
	public Integer traceChanged;
	@Column(name = "url", columnDefinition="TEXT")
	public String url;
	@Column(name = "size", columnDefinition = "bigint")
	public Integer size;


	public ModifiedLineData(String project, String commit_id, String signature, Set<Refactoring4ModifiedLines> refset, Map<String, Integer> lineInfo, String url){
		this.project = project;
		this.commit_id = commit_id;
		this.signature = signature;
		this.refset = refset;
		this.impactedLines = lineInfo.get("impactedLines");
		this.changedLines = lineInfo.get("changedLines");
		this.allLines = lineInfo.get("allLines");
		this.allChangedLines = lineInfo.get("allChangedLines");
		this.url = url;
		this.size = refset.size();
	}

	public ModifiedLineData(String project, String commit_id, String signature, LineInfo lineInfo, Integer traceChanged, String url){
		this.project = project;
		this.commit_id = commit_id;
		this.signature = signature;
		this.refset = lineInfo.ref;
		this.impactedLines = lineInfo.affectedLines;
		this.changedLines = lineInfo.affectedChangedLines;
		this.allLines = lineInfo.allLines;
		this.allChangedLines = lineInfo.allChangedLines;
		this.traceChanged = traceChanged;
		this.url = url;
		this.size = refset.size();
	}

	public ModifiedLineData(){
	}
	
}
