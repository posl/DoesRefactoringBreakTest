package beans.test.rowdata.test;

import beans.test.rowdata.TestInfo;
import beans.test.rowdata.TestResult;

import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name = "result_pure", schema = "test")//,  uniqueConstraints = { @UniqueConstraint(columnNames = { "test_info_id"}) })
public class TestInfoPure extends TestInfo {
    public TestInfoPure(TestResult.ResultType tr) {
        super(tr);
    }

    public TestInfoPure() {

    }
}
