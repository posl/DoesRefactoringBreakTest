package beans.test.rowdata.test;

import beans.test.rowdata.TestInfo;
import beans.test.rowdata.TestResult;

import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "result_straight", schema = "test")//,  uniqueConstraints = { @UniqueConstraint(columnNames = { "test_info_id"}) })
public class TestInfoStraight extends TestInfo {
    public TestInfoStraight(TestResult.ResultType tr) {
        super(tr);
    }

    public TestInfoStraight() {

    }
}
