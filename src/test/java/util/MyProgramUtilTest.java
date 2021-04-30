package util;

import org.junit.Assert;
import org.junit.Test;
import utils.program.MyProgramUtils;

public class MyProgramUtilTest {
    @Test
    public void removeUnRelevantFolderName(){
        String target = "core/test/com/google/inject/ErrorHandlingTest.java";
        String reg = "${project.basedir}/test/";
        String ans = MyProgramUtils.removeUnRelevantDirectoryName(reg, target);
        Assert.assertEquals("com/google/inject/ErrorHandlingTest.java", ans);
    }
    @Test
    public void removeUnRelevantFolderName2(){
        String target = "test/org/apache/commons/io/test/ThrowOnCloseWriter.java";
        String reg = "${project.basedir}/test/";
        String ans = MyProgramUtils.removeUnRelevantDirectoryName(reg, target);
        Assert.assertEquals("org/apache/commons/io/test/ThrowOnCloseWriter.java", ans);
    }
}
