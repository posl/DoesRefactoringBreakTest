package exe._4_aggregate.ModifiedLines;

import beans.RQ.ModifiedLineData;
import beans.RQ.Refactoring4ModifiedLines;
import beans.other.run.IARegistry;
import beans.trace.ExecutionTrace;
import exe._3_analyze.TeaDBUtil;
import exe._3_analyze.store.MethodDefinition4DB;
import exe._3_analyze.store.Methods4DB;
import exe._4_aggregate.AggregateUtils;
import utils.db.Dao;
import utils.db.RegistryDao;
import utils.setting.SettingManager;

import java.util.*;

public class ModifiedLinesAggregator {
	/**
     * Mesure the number of changes in the broken test code
     * @param args
     */
    public static void main(String[] args) {
        //Get target project name
        RegistryDao<IARegistry> iaregistryDao = new RegistryDao<>(IARegistry.class);
        iaregistryDao.init();
        IARegistry iaregistry =  iaregistryDao.getProjectFromRegistry();
        String project = iaregistry.project;
        //Get all methods
        SettingManager sm = new SettingManager(new String[]{project});
        List<Methods4DB> methodXList = TeaDBUtil.getMethodsX(sm);
        Dao<ModifiedLineData> modifiedLineDatadao = new Dao<>(ModifiedLineData.class);
        modifiedLineDatadao.init();
        for(Methods4DB methods : methodXList){
            System.out.println("*** commitId :" + methods.commitId + "*********");
            //get a test methods signature and line information
            Map<String, LineInfo> sigX_ref = AggregateUtils.getSignature4ModifiedLines(sm, methods, "X");
            if(sigX_ref == null){
                continue;
            }
            Map<String, LineInfo> sigX_1_ref = AggregateUtils.getSignature4ModifiedLines(sm, methods, "X_1");
            if(sigX_1_ref == null){
                continue;
            }
            //get modified test methods
            Map<String, LineInfo> modified = LineInfo.getDiff(sigX_1_ref, sigX_ref);
            for(MethodDefinition4DB mdX: methods.methods) {
                LineInfo value = modified.getOrDefault(mdX.signature, new LineInfo(new HashSet<Refactoring4ModifiedLines>(), 0, 0, 0, 0));
                if(value.ref.size() == 0)
                    continue;
                if(value.affectedLines == 0)
                    continue;
                String url = sm.getProject().url;
                //get number of modified lines of other test code executed by the test method.
				Integer traceChanged = getChangedLinesInPassedTestMethod(sm, mdX, methods, methods.commitId);
                url = url + "/commit/" + methods.commitId;
                ModifiedLineData data = new ModifiedLineData(project, methods.commitId, mdX.signature, value, traceChanged, url);
                modifiedLineDatadao.insert(data);
            }
        }
        modifiedLineDatadao.close();
        System.out.println("*******FINISH*********");
    }
    
    public static Map<String, MethodDefinition4DB> getMdMap(List<MethodDefinition4DB> mdXlist){
        Map<String, MethodDefinition4DB> mdMap = new HashMap<String, MethodDefinition4DB>();
        for(MethodDefinition4DB mdX : mdXlist){
            if(mdX.isInTest){
                mdMap.put(mdX.signature, mdX);
            }
        }
        return mdMap;
    }

    /**
     * Get other test code executed by the test method
     * @param mdX
     * @param commitId
     * @return
     */
	public static Set<String> getPassedTestMethod(MethodDefinition4DB mdX, String commitId) {
		Set<String> passedTestMethod = new HashSet<String>();
		Dao<ExecutionTrace> tracedao = new Dao<>(ExecutionTrace.class);
        tracedao.init();
        tracedao.setWhere("commit_id", commitId);
		tracedao.setWhere("signature", mdX.signature);
        tracedao.setWhere("is_cross", false);
		List<ExecutionTrace> traces = tracedao.select();
		for(ExecutionTrace trace : traces){
			List<String> passes = trace.passes;
			for(String pass : passes){
                String[] sig_line = pass.split("@");
				if(AggregateUtils.isTestMethod(pass) && !sig_line[0].equals(mdX.signature)){
					passedTestMethod.add(sig_line[0]);
				}
			}
		}
		tracedao.close();
		return passedTestMethod;
    }


    /**
     * Get the number of modified lines in passed test methods
     * @param sm
     * @param mdX
     * @param methods
     * @param commitId
     * @return
     */
	public static Integer getChangedLinesInPassedTestMethod(SettingManager sm, MethodDefinition4DB mdX, final Methods4DB methods, String commitId){
        Set<String> passed = getPassedTestMethod(mdX, commitId);
        Integer count = 0;
        Map<String, MethodDefinition4DB> mdXMap = getMdMap(methods.methods);
        //remove data from only one set
        mdXMap.keySet().retainAll(passed);
		for(String pass : mdXMap.keySet()){
            MethodDefinition4DB md = mdXMap.get(pass);		
                Map<Integer, Boolean> isChangedX = md.changedLines;
                for(Integer key : isChangedX.keySet()){
                    if(isChangedX.get(key)){
                        count += 1;
                    }
                }   
                MethodDefinition4DB mdX_1 = TeaDBUtil.getMdByAnotherSignature(sm, commitId, "X_1", pass);
                Map<Integer, Boolean> isChangedX_1 = mdX_1.changedLines;
                for(Integer key : isChangedX_1.keySet()){
                    if(isChangedX_1.get(key)){
                        count += 1;
                    }
                }
			}
		return count;
    }
    
	
}
