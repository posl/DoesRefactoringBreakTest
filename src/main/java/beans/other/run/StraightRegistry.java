package beans.other.run;

import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * This class is used to register the instance to be built by this tool
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "straight", schema = "run")
public class StraightRegistry extends Registry{
    public StraightRegistry(String name, String commitId) {
        super(name, commitId);
    }
    public StraightRegistry(){
        super();
    }

    @Override
    public boolean isCross() {
        return false;
    }
}
