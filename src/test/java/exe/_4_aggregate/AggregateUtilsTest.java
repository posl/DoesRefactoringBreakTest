package exe._4_aggregate;

import utils.setting.SettingManager;
import exe._4_aggregate.AggregateUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.refactoringminer.api.Refactoring;

import beans.RQ.Refactoring4TestResults;
import beans.RQ.Refactoring4ModifiedLines;

public class AggregateUtilsTest {
	public AggregateUtilsTest(){
	}
	@Test
	public void getRefactoring4TestResultsTest001(){
		Map<Integer, String> refactorings = new HashMap<Integer, String>();
		refactorings.put(23, ",Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKey#String");
		refactorings.put(25, ",Extract Method@298408682-src/main/java/org/jsoup/nodes/Attributes.java;getIgnoreCase#String,Extract Method@1524205871-src/main/java/org/jsoup/nodes/Attributes.java;checkNotNull#String,Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKeyIgnoreCase#String");
		refactorings.put(26, ",Extract Method@298408682-src/main/java/org/jsoup/nodes/Attributes.java;getIgnoreCase#String,Extract Method@1524205871-src/main/java/org/jsoup/nodes/Attributes.java;checkNotNull#String,Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKeyIgnoreCase#String");
		refactorings.put(27, ",Extract Method@298408682-src/main/java/org/jsoup/nodes/Attributes.java;getIgnoreCase#String,Extract Method@1524205871-src/main/java/org/jsoup/nodes/Attributes.java;checkNotNull#String,Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKeyIgnoreCase#String");
		refactorings.put(29, ",Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKey#String");

		Set<Refactoring4ModifiedLines> ans = AggregateUtils.getRefactoring4ModifiedLines(refactorings);
		Assert.assertEquals(3, ans.size());
		Assert.assertEquals(298408682, ans.toArray()[0].hashCode());
		Assert.assertEquals(1524205871, ans.toArray()[1].hashCode());
		Assert.assertEquals(1905294360, ans.toArray()[2].hashCode());
	}

	@Test
	public void isTestMethodTest001(){
		String pass = "src/test/java/spark/Base64Test.java;Base64Test#@8";
		Boolean isTestMethod = isTestMethod(pass);
		Assert.assertEquals(true, isTestMethod);
	}
	@Test
	public void isTestMethodTest002(){
		String pass = "src/main/java/spark/Base64.java;Base64#@24";
		Boolean isTestMethod = isTestMethod(pass);
		Assert.assertEquals(false, isTestMethod);
	}
	@Test
	public void addAllTest001(){
		Map<Integer, String> refactoringsX = new HashMap<Integer, String>();
		refactoringsX.put(23, ",Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKey#String");
		refactoringsX.put(25, ",Extract Method@298408682-src/main/java/org/jsoup/nodes/Attributes.java;getIgnoreCase#String,Extract Method@1524205871-src/main/java/org/jsoup/nodes/Attributes.java;checkNotNull#String,Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKeyIgnoreCase#String");
		refactoringsX.put(26, ",Extract Method@298408682-src/main/java/org/jsoup/nodes/Attributes.java;getIgnoreCase#String,Extract Method@1524205871-src/main/java/org/jsoup/nodes/Attributes.java;checkNotNull#String,Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKeyIgnoreCase#String");
		refactoringsX.put(27, ",Extract Method@298408682-src/main/java/org/jsoup/nodes/Attributes.java;getIgnoreCase#String,Extract Method@1524205871-src/main/java/org/jsoup/nodes/Attributes.java;checkNotNull#String,Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKeyIgnoreCase#String");
		refactoringsX.put(29, ",Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKey#String");

		Set<Refactoring4ModifiedLines> ansX = AggregateUtils.getRefactoring4ModifiedLines(refactoringsX);
		Map<Integer, String> refactoringsX_1 = new HashMap<Integer, String>();
		refactoringsX_1.put(23, ",Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKey#String");
		refactoringsX_1.put(25, ",Extract Method@298408611-src/main/java/org/jsoup/nodes/Attributes.java;getIgnoreCase#String,Extract Method@1524205871-src/main/java/org/jsoup/nodes/Attributes.java;checkNotNull#String,Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKeyIgnoreCase#String");
		refactoringsX_1.put(26, ",Extract Method@298408611-src/main/java/org/jsoup/nodes/Attributes.java;getIgnoreCase#String,Extract Method@1524205871-src/main/java/org/jsoup/nodes/Attributes.java;checkNotNull#String,Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKeyIgnoreCase#String");
		refactoringsX_1.put(27, ",Extract Method@298408611-src/main/java/org/jsoup/nodes/Attributes.java;getIgnoreCase#String,Extract Method@1524205871-src/main/java/org/jsoup/nodes/Attributes.java;checkNotNull#String,Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKeyIgnoreCase#String");
		refactoringsX_1.put(29, ",Extract Attribute@1905294360-src/main/java/org/jsoup/nodes/Attributes.java;indexOfKey#String");

		Set<Refactoring4ModifiedLines> ansX_1 = AggregateUtils.getRefactoring4ModifiedLines(refactoringsX_1);
		ansX_1.addAll(ansX);
		Assert.assertEquals(4, ansX_1.size());
		Assert.assertEquals(298408682, ansX_1.toArray()[0].hashCode());
		Assert.assertEquals(1524205871, ansX_1.toArray()[1].hashCode());
		Assert.assertEquals(1905294360, ansX_1.toArray()[2].hashCode());
		Assert.assertEquals(298408611, ansX_1.toArray()[3].hashCode());
	}


	private static Set<Refactoring4TestResults> getRefactoring4TestResults(Map<Integer, String> refactorings) {
		return AggregateUtils.getRefactoring4TestResults(refactorings);
	}
	private static Boolean isTestMethod(String pass){
		return AggregateUtils.isTestMethod(pass);
	}

}
