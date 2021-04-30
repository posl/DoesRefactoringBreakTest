package exe._4_aggregate;

import beans.RQ.Refactoring4ModifiedLines;
import beans.RQ.Refactoring4TestResults;
import exe._3_analyze.TeaDBUtil;
import exe._3_analyze.store.MethodDefinition4DB;
import exe._3_analyze.store.Methods4DB;
import exe._4_aggregate.ModifiedLines.LineInfo;
import org.apache.commons.lang3.StringUtils;
import utils.setting.SettingManager;

import java.util.*;

public class AggregateUtils {
	private AggregateUtils(){
	}

    /**
     * Get a map that shows refactoring edits for each test method
     * @param sm
     * @param method
     * @param type
     * @return
     */
	public static Map<String, Set<Refactoring4TestResults>> getSignature4TestResults(SettingManager sm, Methods4DB method, String type){
        Map<String, Set<Refactoring4TestResults>> refs = new HashMap<String, Set<Refactoring4TestResults>>();
        List<MethodDefinition4DB> mdlist = TeaDBUtil.getTestCase(sm, method.commitId, type);
        for(MethodDefinition4DB md : mdlist) {
            Set<Refactoring4TestResults> refSet = AggregateUtils.getRefactoring4TestResults(md.directRefactorings);
            if(type.equals("X")){
                refs.put(md.anotherSignature, refSet);
            }else{
                refs.put(md.signature, refSet);
            }
        }
        return refs;
	}


    /**
     * Get a map of line info (i.e., modified lines) in a method
     * @param sm
     * @param method
     * @param type
     * @return
     */
	public static Map<String, LineInfo> getSignature4ModifiedLines(SettingManager sm, Methods4DB method, String type){
        Map<String, LineInfo> refs = new HashMap<String, LineInfo>();
        List<MethodDefinition4DB> mdlist = TeaDBUtil.getTestCase(sm, method.commitId, type);
        for(MethodDefinition4DB md : mdlist) {
            Set<Refactoring4ModifiedLines> refSet = AggregateUtils.getRefactoring4ModifiedLines(md.directRefactorings);
            Map<String, Integer> lineInfo = getLineInfo(md);
            LineInfo value = new LineInfo(refSet, lineInfo.getOrDefault("impactedLines", 0), lineInfo.getOrDefault("changedLines", 0), lineInfo.getOrDefault("allLines", 0), lineInfo.getOrDefault("allChangedLines", 0));
            if(type.equals("X")){
                refs.put(md.signature, value);
            }else{
                refs.put(md.anotherSignature, value);
            }
        }
        return refs;
    }

    /**
     * Get a set of refactoring edits in a test method
     * @param refactorings
     * @return
     */
	public static Set<Refactoring4TestResults> getRefactoring4TestResults(Map<Integer, String> refactorings) {
        Set<Refactoring4TestResults> rwmSet = new HashSet<Refactoring4TestResults>();
        for(String refstrings : refactorings.values()){
			String[] refs = refstrings.split(",");
			for(int i = 0 ; i < refs.length ;i++){
				if(i == 0 || !refs[i].contains("@"))
					continue;
				else{
					String[] refData = setRefData(refs[i]);
					Refactoring4TestResults rwm = new Refactoring4TestResults(refData[0], refData[1], refData[2]);
					rwmSet.add(rwm);
                    }
				}
			}
        return rwmSet;
	}


    /**
     * Get a refactoring edits in modified lines
     * @param refactorings
     * @return
     */
	public static Set<Refactoring4ModifiedLines> getRefactoring4ModifiedLines(Map<Integer, String> refactorings) {
        Set<Refactoring4ModifiedLines> rwmSet = new HashSet<Refactoring4ModifiedLines>();
        for(String refstrings : refactorings.values()){
            String[] refs = refstrings.split(",");
            for(int i = 0 ; i < refs.length ;i++){
                if(i == 0 || !refs[i].contains("@"))
                    continue;
				else{
					String[] refData = setRefData(refs[i]);
					Refactoring4ModifiedLines rwm = new Refactoring4ModifiedLines(refData[0], refData[1], refData[2]);
					rwmSet.add(rwm);
				}
            }
        }
        return rwmSet;
    }

    /**
     * change row refactoring data to type, hash, whomade
     * @param ref
     * ( {refactoringType}@hash-whomade )
    */
	public static String[] setRefData(String ref){
		String r = "";
        String hash = "";
		String whoMade = "";
       
		if(ref.contains("@-")){
			r = ref.split("@")[0];
			String[] tmp = ref.split("@")[1].split("-");
			if(tmp.length == 2){
				hash = "-" + tmp[1];
				whoMade = null;
			}else{
				hash = "-" + tmp[1];
				whoMade = tmp[2];
			}
		}else{
			r = ref.split("@")[0];
			String[] tmp = ref.split("@")[1].split("-");
			if(tmp.length == 1){
				hash = tmp[0];
				whoMade = null;
			}else{
				hash = tmp[0];
				whoMade = tmp[1];
			}
		}
		String[] refData = {r, hash, whoMade};
		return refData;
    }

    /**
     * Check test methods or not
     * @param pass
     * @return
     */
    public static Boolean isTestMethod(String pass){
		if(pass.contains("/test/")){
			return true;
		}
		return false;
    }

    /**
     * Get changed lines
     * @param md
     * @return
     */
    public static Map<String, Integer> getLineInfo(MethodDefinition4DB md) {
        Map<String, Integer> infoMap = new HashMap<String, Integer>();
        Map<Integer, String> ref = md.directRefactorings;
        Map<Integer, Boolean> isChanged = md.changedLines;
        for(Integer line : isChanged.keySet()){
            Integer impactedLines = infoMap.getOrDefault("impactedLines", 0);
            Integer changedLines = infoMap.getOrDefault("changedLines", 0);
            Integer allLines = infoMap.getOrDefault("allLines", 0);
            Integer allChangedLines = infoMap.getOrDefault("allChangedLines", 0);
            //all lines
            allLines += 1;
            infoMap.put("allLines", allLines);
            //all chenged lines
            if(isChanged.get(line)){
                allChangedLines += 1;
                infoMap.put("allChangedLines", allChangedLines);
            }
            //all affected lines
            if(!ref.isEmpty()){
                String impacted =  ref.getOrDefault(line, null);
                if(!StringUtils.isEmpty(impacted)){
                    impactedLines += 1;
                    infoMap.put("impactedLines", impactedLines);
                    //all changed lines which affected
                    if(isChanged.get(line)){
                        changedLines += 1;
                        infoMap.put("changedLines", changedLines);
                    }
                }
            }
        }
        return infoMap;
	}
	
}
