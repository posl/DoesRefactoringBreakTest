package utils.general;

import java.util.List;
import java.util.StringJoiner;

public class MyListUtils {
    /**
     * convert list to String
     * @param arguments
     * @return
     */
    public static String flatten(List<String> arguments){
        StringJoiner sj = new StringJoiner(",");
        arguments.forEach(sj::add);
        return sj.toString();
    }
}
