package exe._1_init;

import beans.commit.Commit;
import beans.other.run.CrossRegistry;
import beans.other.run.IARegistry;
import beans.other.run.StraightRegistry;
import utils.db.Dao;
import utils.db.RegistryDao;
import utils.setting.SettingManager;

import java.util.HashSet;
import java.util.Set;

public class RegistryStoreManager {
	/**
	 * Store registries involving builds and impact analysis at the same time.
	 * @param sm
	 * @param commits
	 */
	public static void register(SettingManager sm, Set<Commit> commits){
		//initialization
		Dao<StraightRegistry> straightRegistryDao = new RegistryDao<>(StraightRegistry.class);
		Dao<CrossRegistry> crossRegistryDao = new RegistryDao<>(CrossRegistry.class);
		Dao<IARegistry> IARegistryDao = new RegistryDao<>(IARegistry.class);
		crossRegistryDao.init();
		straightRegistryDao.init();
		IARegistryDao.init();
		Set<String> commitIds = new HashSet<String>();
		Set<String> commitIdsWithoutParentMerged =new HashSet<String>();
		//find targets
		for(Commit commit : commits){
			commitIds.add(commit.commitId);
			if(commit.parentCommitIds.size() == 1){//skip merge commit and the first commit
				commitIdsWithoutParentMerged.add(commit.commitId);
			}
		}
		//set the parent data
		Set<String> commitIdsWithParent = setParentCommitId(commits, commitIds);
		//store data
		registerRunRegistry(sm, commitIdsWithParent, straightRegistryDao, crossRegistryDao);
		registerIARegistry(sm, commitIdsWithoutParentMerged, IARegistryDao);
		//deconstruct
		crossRegistryDao.close();
		straightRegistryDao.close();
		IARegistryDao.close();
	}
	
	public static Set<String> setParentCommitId(Set<Commit> commits, Set<String> commitIds){
		for(Commit commit : commits){
			commitIds.addAll(commit.parentCommitIds);
		}
		return commitIds;
	}

	private static void registerRunRegistry(SettingManager sm, Set<String> commitIds,
											Dao<StraightRegistry> straightRegistryDao, Dao<CrossRegistry> crossRegistryDao) {
		for(String commitId : commitIds){
			StraightRegistry rs = new StraightRegistry(sm.getProject().name, commitId);
			CrossRegistry rc = new CrossRegistry(sm.getProject().name, commitId);
			straightRegistryDao.insert(rs);
			crossRegistryDao.insert(rc);
		}
    }
    private static void registerIARegistry(SettingManager sm, Set<String> commitIds, Dao<IARegistry> IARegistryDao) {
		for(String commitId : commitIds){
			IARegistry r = new IARegistry(sm.getProject().name, commitId);
			IARegistryDao.insert(r);
		}
	}
	
}
