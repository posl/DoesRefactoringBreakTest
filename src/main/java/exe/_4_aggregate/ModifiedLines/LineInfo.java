package exe._4_aggregate.ModifiedLines;

import beans.RQ.Refactoring4ModifiedLines;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LineInfo {
	public Integer allLines;
	public Integer allChangedLines;
	public Integer affectedLines;
	public Integer affectedChangedLines;
	public Set<Refactoring4ModifiedLines> ref;
	public LineInfo(Set<Refactoring4ModifiedLines> ref, Integer affectedLines, Integer affectedChangedLines, Integer allLines, Integer allChangedLines){
		this.ref = ref;
		this.affectedLines = affectedLines;
		this.affectedChangedLines = affectedChangedLines;
		this.allLines = allLines;
		this.allChangedLines = allChangedLines;
	}

    /**
     * Get test methods which are present in X_1 and X
     * @param sigX_1_ref
     * @param sigX_ref
     * @return
     */
	public static Map<String, LineInfo> getDiff(Map<String, LineInfo> sigX_1_ref, Map<String, LineInfo> sigX_ref) {
        Map<String, LineInfo> set = new HashMap<String, LineInfo>();
        Set<String> casesX_1 = sigX_1_ref.keySet();
        Set<String> casesX = sigX_ref.keySet();
        for(String c: casesX_1){
            if(casesX.contains(c)){
                Set<Refactoring4ModifiedLines> ref = sigX_1_ref.get(c).ref;
                ref.addAll(sigX_ref.get(c).ref);
                if(ref.size() != 0){
					//affect, affectedChanged, all, allChanged
                    LineInfo value = new LineInfo(ref, sigX_1_ref.get(c).affectedLines + sigX_ref.get(c).affectedLines, sigX_1_ref.get(c).affectedChangedLines + sigX_ref.get(c).affectedChangedLines, sigX_1_ref.get(c).allChangedLines + sigX_ref.get(c).allLines, sigX_1_ref.get(c).allChangedLines + sigX_ref.get(c).allChangedLines);
                    set.put(c, value);
                }
            }
        }
        return set;
    }
}
