package beans.other.run;

import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * This class is used to register the instance to be analyzed by this tool
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "IA", schema = "run")
public class IARegistry extends Registry{

    public int straight_resultcode;
    public int cross_resultcode;

	public IARegistry(String name, String commitId) {
        super(name, commitId);
	}
	public IARegistry(){
        super();
    }
	@Override
    public boolean isCross() {
        return false;
    }
	
}
