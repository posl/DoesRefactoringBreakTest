package exe._3_analyze;

import beans.commit.Commit;
import beans.other.run.IARegistry;
import beans.refactoring.Refactoring2;
import beans.source.TestMethodDefinition;
import beans.test.result.StraightJunitTestResult;
import beans.test.rowdata.TestInfo;
import beans.test.rowdata.test.TestInfoCross;
import beans.test.rowdata.test.TestInfoStraight;
import beans.trace.ExecutionTrace;
import exe._3_analyze.store.MethodDefinition4DB;
import exe._3_analyze.store.Methods4DB;
import modules.source.structure.StructureAnalyzer;
import utils.db.Dao;
import utils.db.RegistryDao;
import utils.setting.SettingManager;

import java.util.List;
import java.util.Set;

/**
 * This class provides utilities to get data from database
 */
public class TeaDBUtil {
    /**
     * get StraightJunitTestResult from database
     * @param project
     * @param commitId
     * @return
     */
    public static List<StraightJunitTestResult> getTests(String project, String commitId) {
        Dao<StraightJunitTestResult> testDao = new Dao<>(StraightJunitTestResult.class);
        testDao.init();
        testDao.setWhere("project", project);
        testDao.setWhere("commit_id", commitId);
        List<StraightJunitTestResult> l = testDao.select();
        testDao.close();
        return l;
    }

    /**
     * get impact analysis registry data with result code 0
     * @param sm
     * @return
     */
    public static List<IARegistry> getRegistries(SettingManager sm) {
        Dao<IARegistry> registryDao = new RegistryDao<>(IARegistry.class);
        registryDao.init();
        registryDao.setWhere("project", sm.getProject().name);
        registryDao.setWhere("resultCode", "0");
        List<IARegistry> list = registryDao.select();
        registryDao.close();
        return list;
    }
    /**
     * get impact analysis registries
     * @param sm
     * @return
     */
    public static IARegistry getRegistry(SettingManager sm) {
        RegistryDao<IARegistry> registryDao = new RegistryDao<>(IARegistry.class);
        registryDao.init();
        registryDao.setWhere("project", sm.getProject().name);
        IARegistry iaregistry = registryDao.getOneFromMany();
        registryDao.close();
        return iaregistry;
    }

    /**
     * get execution pathes
     * @param tr
     * @param t
     * @return
     */
    public static List<ExecutionTrace> getPath(StraightJunitTestResult tr, TestInfo t) {
        Dao<ExecutionTrace> pathDao = new Dao<>(ExecutionTrace.class);
        pathDao.init();
        pathDao.setWhere("project", tr.project);
        pathDao.setWhere("commit_id", tr.commitId);
        pathDao.setWhere("signature", t.signature);
        pathDao.setWhere("is_cross", false);
        //pathDao.setOrderBy("invoked_order");
        List<ExecutionTrace> list = pathDao.select();
        pathDao.close();
        return list;
    }
    /**
     * get execution paths
     * @return
     */
    public static List<TestInfoCross> getTestResult(String project, String commit_id, MethodDefinition4DB method){
        Dao<TestInfoCross> testdao = new Dao<>(TestInfoCross.class);
        testdao.init();
        testdao.setWhere("project", project);
        testdao.setWhere("commit_id", commit_id);
        testdao.setWhere("signature", method.signature);
        List<TestInfoCross> results = testdao.select();
        testdao.close();
        return results;
    }

    /**
     * Check if the method in the parent commit passed tests
     * @param project
     * @param commit_id
     * @param method
     * @return
     */
    public static Boolean isParentPass(String project, String commit_id, MethodDefinition4DB method){
        Dao<Commit> commitdao = new Dao<>(Commit.class);
        commitdao.init();
        commitdao.setWhere("project", project);
        commitdao.setWhere("commit_id", commit_id);
        List<Commit> commit = commitdao.select();
        String parentCommit = "";
        if(!commit.isEmpty()){
            List<String> parentCommits = commit.get(0).parentCommitIds;
            //assertEquals(parentCommits.size(), 1);
            parentCommit = parentCommits.get(0);
        }
        commitdao.close();
        Dao<TestInfoStraight> testdao = new Dao<>(TestInfoStraight.class);
        testdao.init();
        testdao.setWhere("project", project);
        testdao.setWhere("commit_id", parentCommit);
        testdao.setWhere("signature", method.signature);
        List<TestInfoStraight> results = testdao.select();
        testdao.close();
        if(results.size() != 0){
            TestInfoStraight result = results.get(0);
            if(!result.testResult.getType().name().equals("PASS")){
                return false;
            }
        }else {
            return false;
        }
        return true;
    }

    /**
     * show refactorings for all methods
     * @param structureAnalyzer
     */
    public static void show(StructureAnalyzer structureAnalyzer){
        for(String s: structureAnalyzer.getTestSignature()){
            TestMethodDefinition tm = (TestMethodDefinition) structureAnalyzer.getMethod(s);
            System.out.println(tm.signature+"->");
            for(Integer i: tm.directRefactorings.keySet()){
                Set<Refactoring2> list = tm.directRefactorings.get(i);
                System.out.println("   "+i+":");
                for(Refactoring2 r: list){
                    System.out.print(r.refactoring.getRefactoringType().getDisplayName()+", ");
                }
                System.out.println();
            }
        }
    }

    /**
     * get methods in the parent commit
     * @param sm
     * @return
     */
    public static List<Methods4DB> getMethodsX_1(SettingManager sm){
        Dao<Methods4DB> mdDao = new Dao<>(Methods4DB.class);
        mdDao.init();
        mdDao.setWhere("project", sm.getProject().name);
        mdDao.setWhere("type", "X_1");
        List<Methods4DB> list = mdDao.select();
        mdDao.close();
        return list;
    }
    /**
     * get methods in the target commit
     * @param sm
     * @return
     */
    public static List<Methods4DB> getMethodsX(SettingManager sm){
        Dao<Methods4DB> mdDao = new Dao<>(Methods4DB.class);
        mdDao.init();
        mdDao.setWhere("project", sm.getProject().name);
        mdDao.setWhere("type", "X");
        List<Methods4DB> list = mdDao.select();
        mdDao.close();
        return list;
    }

    /**
     * get methods in the target commit
     * @param project
     * @param commitId
     * @return
     */
    public static Methods4DB getMethodX(String project, String commitId){
        Dao<Methods4DB> mdDao = new Dao<>(Methods4DB.class);
        mdDao.init();
        mdDao.setWhere("project", project);
        mdDao.setWhere("commit_id", commitId);
        mdDao.setWhere("type", "X");
        List<Methods4DB> list = mdDao.select();
        mdDao.close();
        return list.get(0);
    }
    /**
     * get methods in the parent commit
     * @param project
     * @param commitId
     * @return
     */
    public static Methods4DB getMethodX_1(String project, String commitId){
        Dao<Methods4DB> mdDao = new Dao<>(Methods4DB.class);
        mdDao.init();
        mdDao.setWhere("project", project);
        mdDao.setWhere("commit_id", commitId);
        mdDao.setWhere("type", "X_1");
        List<Methods4DB> list = mdDao.select();
        mdDao.close();
        return list.get(0);
    }

    /**
     * get method definitions that have @Test
     * @param sm
     * @param commit_id
     * @param type
     * @return
     */
    public static List<MethodDefinition4DB> getTestCase(SettingManager sm, String commit_id, String type){
        Dao<MethodDefinition4DB> mdDao = new Dao<>(MethodDefinition4DB.class);
        mdDao.init();
        mdDao.setWhere("project", sm.getProject().name);
        mdDao.setWhere("commit_id", commit_id);
        mdDao.setWhere("type", type);
        mdDao.setWhere("istestcase", true);
        List<MethodDefinition4DB> list = mdDao.select();
        mdDao.close();
        return list;
    }

    /**
     * get test methods that are in test directories
     * @param sm
     * @param type
     * @return
     */
    public static List<MethodDefinition4DB> getmdisInTest(SettingManager sm, String type){
        Dao<MethodDefinition4DB> mdDao = new Dao<>(MethodDefinition4DB.class);
        mdDao.init();
        mdDao.setWhere("project", sm.getProject().name);
        mdDao.setWhere("type", type);
        mdDao.setWhere("isintest", true);
        List<MethodDefinition4DB> list = mdDao.select();
        mdDao.close();
        return list;
    }

    /**
     * get method definitions using another signature
     * @param sm
     * @param commit_id
     * @param type
     * @param signature
     * @return
     */
    public static MethodDefinition4DB getMdByAnotherSignature(SettingManager sm, String commit_id, String type, String signature){
        Dao<MethodDefinition4DB> mdDao = new Dao<>(MethodDefinition4DB.class);
        mdDao.init();
        mdDao.setWhere("project", sm.getProject().name);
        mdDao.setWhere("commit_id", commit_id);
        mdDao.setWhere("type", type);
        mdDao.setWhere("anothersignature", signature);
        List<MethodDefinition4DB> list = mdDao.select();
        mdDao.close();
        if(list.isEmpty()){
            return new MethodDefinition4DB();
        }else{
            return list.get(0);
        }
    }

    /**
     * get method definitions in the same file
     * @param sm
     * @param commit_id
     * @param fileName
     * @return
     */
    public static List<MethodDefinition4DB> getSameFileMd(SettingManager sm, String commit_id, String fileName){
        Dao<MethodDefinition4DB> mdDao = new Dao<>(MethodDefinition4DB.class);
        mdDao.init();
        mdDao.setWhere("project", sm.getProject().name);
        mdDao.setWhere("commit_id", commit_id);
        mdDao.setWhere("filename", fileName);
        List<MethodDefinition4DB> list = mdDao.select();
        mdDao.close();
        return list;
    }
}
