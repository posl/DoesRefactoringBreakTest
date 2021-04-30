package beans.RQ;

import javax.persistence.*;
import java.io.Serializable;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractRefactoring implements Serializable{
	/**
	 * Refactoring type
	 */
	@Column(name="refactoring", columnDefinition="TEXT")
	String refactoring;
	/**
	 * The ID provided by refactoring miner
	 */
	@Column(name="hash", columnDefinition="TEXT")
	String hash;
	/**
	 * refactoring origin
	 */
	@Column(name="whoMade", columnDefinition="TEXT")
	String whoMade;
	
	public AbstractRefactoring(String refactoring, String hash, String whoMade){
		this.refactoring = refactoring;
		this.hash = hash;
		this.whoMade = whoMade;
	}
	public AbstractRefactoring(){}

	public String getHash(){
		return this.hash;
	}
	public String getRefactoring(){
		return this.refactoring;
	}
	public int hashCode(){
        return Integer.valueOf(hash);
	}
	public boolean equals(Object obj){
        AbstractRefactoring ref = (AbstractRefactoring)obj;
        if(ref.hashCode() == this.hashCode())
			return true;
		else
            return false;
       }
}
