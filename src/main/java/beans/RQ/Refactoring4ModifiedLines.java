package beans.RQ;
import javax.persistence.*;


/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "refactoring_rq3", schema = "rq", uniqueConstraints = { @UniqueConstraint(columnNames = { "id"}) })
public class Refactoring4ModifiedLines extends AbstractRefactoring{
	/**
	 * this ID is automatically generated for each table by hibernate
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", columnDefinition="bigint")
	public long Id;

	public Refactoring4ModifiedLines(String refactoring, String hash, String whoMade){
		super(refactoring, hash, whoMade);
	}
	public Refactoring4ModifiedLines(){}
}
