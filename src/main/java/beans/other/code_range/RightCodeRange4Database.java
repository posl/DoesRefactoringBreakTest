package beans.other.code_range;

import gr.uom.java.xmi.diff.CodeRange;

import javax.persistence.*;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "right_code_range", schema = "refactoring")
public class RightCodeRange4Database extends CodeRange4Database {
    /**
     * this ID is automatically generated for each table by hibernate
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", columnDefinition="bigint")
    public long id;
    public RightCodeRange4Database(CodeRange cr) {
        super(cr);
    }
}
