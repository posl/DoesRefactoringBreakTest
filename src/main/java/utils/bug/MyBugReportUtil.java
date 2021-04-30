package utils.bug;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBugReportUtil {
    public static String[] bugPatterns = {"bug\\s*", "bugs\\s*", "fix\\s*", "fixed\\s*", "fixes\\s*", "defect\\s*", "fixes #"};//TODO: this is not specialized into each project

    /**
     * extract bug report number
     * @param commitComment
     * @return
     */
    public static Set<String> searchIssue(String commitComment) {
        Set<String> bugIds = new HashSet<String>();
        // Usual pattern
        String target = commitComment.toLowerCase();
        for (String pattern : bugPatterns) {//find with regrex
            Matcher m1 = Pattern.compile(pattern.toLowerCase() + "[0-9]+").matcher(target);
            while (m1.find()) {
                Matcher m2 = Pattern.compile("[0-9]+").matcher(m1.group());// if multiple patterns
                while (m2.find()) {
                    String match = m2.group();
                    Long.parseLong(match);// check  in case
                    bugIds.add(match);
                }
            }
        }
        return bugIds;
    }
}
