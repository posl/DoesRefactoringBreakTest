package exe._3_analyze;

import beans.other.RefactoringForDatabase;
import beans.other.run.CrossRegistry;
import beans.other.run.IARegistry;
import beans.other.run.StraightRegistry;
import beans.test.result.StraightJunitTestResult;
import exe._3_analyze.store.StructureAnalyzer4DB;
import utils.db.Dao;
import utils.db.RegistryDao;
import utils.log.MyLogger;
import utils.setting.SettingManager;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Run Impact analysis for one target. If you want to run many targets, please call this many times.
 * The build runs (cross and straigt) of the target should be completed beforehand.
 */
public class ImpactAnalyzerMain {
    public static void main(String[] args) {
            MyLogger logger = MyLogger.getInstance();
            System.out.println("******START*******");

            RegistryDao<IARegistry> iaregistryDao = new RegistryDao<>(IARegistry.class);
            RegistryDao<StraightRegistry> straightregistryDao = new RegistryDao<>(StraightRegistry.class);
            RegistryDao<CrossRegistry> crossregistryDao = new RegistryDao<>(CrossRegistry.class);
            Dao<RefactoringForDatabase> refactoringDao = new Dao<>(RefactoringForDatabase.class);
            iaregistryDao.init();
            straightregistryDao.init();
            crossregistryDao.init();
            refactoringDao.init();
            IARegistry iaregistry =  iaregistryDao.getOneFromMany();
            if(iaregistry != null){
                SettingManager sm = new SettingManager(new String[]{iaregistry.project});
                iaregistry.startDate = LocalDateTime.now();
                //get straight and cross registry to confirm status
                straightregistryDao.setWhere("project", iaregistry.project);
                straightregistryDao.setWhere("commit_id", iaregistry.commitId);
                StraightRegistry straightregistry = straightregistryDao.select().get(0); 
                straightregistryDao.close();
                crossregistryDao.setWhere("project",iaregistry.project);
                crossregistryDao.setWhere("commit_id", iaregistry.commitId);
                CrossRegistry crossregistry = crossregistryDao.select().get(0);
                crossregistryDao.close();
                if( (straightregistry.resultCode != 2) || (crossregistry.resultCode != 2) ){
                    iaregistry.resultCode = 4;
                    iaregistry.straight_resultcode = straightregistry.resultCode;
                    iaregistry.cross_resultcode = crossregistry.resultCode;
                    iaregistry.endDate = LocalDateTime.now();
                    iaregistryDao.update(iaregistry);
                    iaregistryDao.close();
                }else{//run only normally finished
                    refactoringDao.setWhere("project", iaregistry.project);
                    refactoringDao.setWhere("commit_id", iaregistry.commitId);
                    boolean isRefactoredCommit = !(refactoringDao.select()).isEmpty();
                    refactoringDao.close();
                    if(!isRefactoredCommit){//if this is not refactoring commit, we do not run
                        iaregistry.resultCode = 5;
                        iaregistry.endDate = LocalDateTime.now();
                        iaregistryDao.update(iaregistry);
                        iaregistryDao.close();
                    }else{
                    //analysis start
                    ImpactAnalysisService service = new ImpactAnalysisService(sm);
                    service.registry = iaregistry;
                    try{
                        service.analyze();
                        StructureAnalyzer4DB.store(service);
                        iaregistry.resultCode = 2;
                        iaregistry.endDate = LocalDateTime.now();
                        iaregistryDao.update(iaregistry);
                        iaregistryDao.close();
                    }catch (Exception e) {
                        logger.error("Exception " + iaregistry.commitId);
                        logger.error(e);
                        iaregistry.resultCode = 3;
                        iaregistry.resultMessage = "Exception";
                        iaregistry.errorMessage = e.getMessage();
                        iaregistry.endDate = LocalDateTime.now();
                        iaregistryDao.update(iaregistry);
                        iaregistryDao.close();
                    }
                }
            }
        }
    }


    public static List<StraightJunitTestResult> getTestResult(ImpactAnalysisService service) {
        Dao<StraightJunitTestResult> dao = new Dao<>(StraightJunitTestResult.class);
        dao.init();
        dao.setWhere("project", service.registry.project);
        dao.setWhere("commit_id", service.registry.commitId);
        List<StraightJunitTestResult> list = dao.select();
        dao.close();
        assert list.size()==1;
        return list;
    }


}
