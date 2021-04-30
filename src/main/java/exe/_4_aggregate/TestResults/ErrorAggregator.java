package exe._4_aggregate.TestResults;

import beans.RQ.ErrorData;
import beans.RQ.Refactoring4TestResults;
import beans.other.run.IARegistry;
import beans.test.rowdata.test.TestInfoCross;
import exe._3_analyze.TeaDBUtil;
import exe._3_analyze.store.MethodDefinition4DB;
import exe._3_analyze.store.Methods4DB;
import exe._4_aggregate.AggregateUtils;
import utils.db.Dao;
import utils.db.RegistryDao;
import utils.setting.SettingManager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ErrorAggregator {
    /**
     * This method aims to calculate each test results(i.g., success, fail, error) per refactoring type.
     * @param args
     */
    public static void main(String[] args) {
        //Get target project name
        RegistryDao<IARegistry> iaregistryDao = new RegistryDao<>(IARegistry.class);
        iaregistryDao.init();
        IARegistry iaregistry =  iaregistryDao.getProjectFromRegistry();
        String project = iaregistry.project;
        SettingManager sm = new SettingManager(new String[]{project});
        //Get all methods from database
        List<Methods4DB> mdlist = TeaDBUtil.getMethodsX_1(sm);
        Dao<ErrorData> errorDatadao = new Dao<>(ErrorData.class);
        errorDatadao.init();
        //Run methods one at a time
        for(Methods4DB methods: mdlist){
            //Get a test method signature and refacotirng
            //for X
            Map<String, Set<Refactoring4TestResults>> sigX_ref = AggregateUtils.getSignature4TestResults(sm, methods, "X");
            if(sigX_ref == null){
                continue;
            }
            //for X_1
            Map<String, Set<Refactoring4TestResults>> sigX_1_ref = AggregateUtils.getSignature4TestResults(sm, methods, "X_1");
            if(sigX_1_ref == null){
                continue;
            }
            for(MethodDefinition4DB method : methods.methods){
                System.out.println("********commitId " + methods.commitId + " *******");
                System.out.println("********signature " + method.signature + "*******");
                Set<Refactoring4TestResults> refactoring = sigX_1_ref.getOrDefault(method.signature, new HashSet<Refactoring4TestResults>());
                Set<Refactoring4TestResults> refactoringX = sigX_ref.getOrDefault(method.signature, new HashSet<Refactoring4TestResults>());
                //conbine two set
                refactoring.addAll(refactoringX);
                //remove test methods which don't have refactoring
                if(refactoring.size() == 0){
                    continue;
                }
                //remove test methods which same test of parent method doesn't pass
                if(!TeaDBUtil.isParentPass(project, methods.commitId, method))
                    continue;
                //get test results of test method
                List<TestInfoCross> results = TeaDBUtil.getTestResult(project, methods.commitId, method);
                if(results.size() == 0){
                    continue;
                }
                TestInfoCross result = results.get(0);
                String url = sm.getProject().url;
                url = url + "/commit/" + methods.commitId;
                ErrorData data = new ErrorData(project, methods.commitId, result, refactoring, url);
                errorDatadao.insert(data);
            }
        }
        System.out.println("*** FINISH **********************"); 
        errorDatadao.close();
    }
}
