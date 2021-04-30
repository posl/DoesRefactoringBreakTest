package utils.db;

import beans.other.run.Registry;

import javax.persistence.LockModeType;
import java.util.List;

public class RegistryDao <T extends Registry> extends Dao<T>{
	
	public RegistryDao(Class<T> m) {
		super(m);
	}

	/**
	 * this gets a registry with a lock
	 * to prevent from building the same instances on Kubernetes at the same time
	 * @return
	 */
	public T getOneFromMany() {
		T result = null;
		try {
			this.entityManager.getTransaction().begin();
			this.setWhere("resultcode", 0);
			this.setLimit(1);
			this.setLock(LockModeType.PESSIMISTIC_READ);//lock
			List<T> selected = this.select();
			if(selected != null){
				result = selected.get(0);
				result.resultCode = 1;
				entityManager.merge(result);//change status to running
				this.entityManager.getTransaction().commit();
			}else{
				this.entityManager.getTransaction().rollback();
			}
			this.entityManager.getTransaction().commit();
		}catch (final Exception e) {
			if (this.entityManager.getTransaction().isActive())
				this.entityManager.getTransaction().rollback();
			//need not throw Exception
		}
		return result;
    }
	
	public T getProjectFromRegistry() {
		T result = null;
		try {
			this.entityManager.getTransaction().begin();
			this.setLimit(1);
			this.setLock(LockModeType.PESSIMISTIC_READ);//lock
			List<T> selected = this.select();
			if(selected != null){
				result = selected.get(0);
				result.resultCode = 1;
				entityManager.merge(result);//change status to running
				this.entityManager.getTransaction().commit();
			}else{
				this.entityManager.getTransaction().rollback();
			}
			this.entityManager.getTransaction().commit();
		}catch (final Exception e) {
			if (this.entityManager.getTransaction().isActive())
				this.entityManager.getTransaction().rollback();
			//need not throw Exception
		}
		return result;
    }
}