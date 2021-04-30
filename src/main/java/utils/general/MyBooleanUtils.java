package utils.general;

import java.util.ArrayList;
import java.util.List;

public class MyBooleanUtils {
    /**
     * find lines that are true for both list
     * @param left
     * @param right
     * @return
     */
    public static List<Boolean> getAND(List<Boolean> left, List<Boolean> right){
        assert (left.size()==right.size());
        List<Boolean> and = new ArrayList<>();
        for (int i=0;i<left.size();i++){
            and.add(left.get(i).equals(right.get(i)));
        }
        return and;
    }
}
