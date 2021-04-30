package exe._5_output;

import beans.RQ.CsvHandler;
import beans.RQ.ErrorData;
import beans.RQ.Refactoring4TestResults;
import beans.other.run.IARegistry;
import utils.db.Dao;
import utils.db.RegistryDao;

import java.util.*;

public class TestBreakAnalyzer {
	/**
	 * create CSV files about broken tests, extracting from database
	 * @param args
	 */
	public static void main(String[] args) {
		//Get target project name
        RegistryDao<IARegistry> iaregistryDao = new RegistryDao<>(IARegistry.class);
        iaregistryDao.init();
        IARegistry iaregistry =  iaregistryDao.getProjectFromRegistry();
        String project = iaregistry.project;
		//output path
		String resultPath = "./outputs/" + project + "/rq1/result.csv";
		String dataPath = "./outputs/" + project + "/rq1/data.csv";
		CsvHandler resultHandler = new CsvHandler(resultPath, project);
		CsvHandler dataHandler = new CsvHandler(dataPath, project);
		//make headline
		resultHandler.initErrorData(true);
		dataHandler.initErrorData(false);
		//get error data which have only one refactoring
		Dao<ErrorData> errorDatadao = new Dao<>(ErrorData.class);
		errorDatadao.init();
		errorDatadao.setWhere("project", project);
		errorDatadao.setWhere("size", 1);
		List<String> result_ref = new ArrayList<String>();
		List<ErrorData> errorDatas = errorDatadao.select();
		//insert data
		for(ErrorData data : errorDatas){
			String result = data.result;
			for(Refactoring4TestResults ref : data.refset){
				result_ref.add(ref.getRefactoring() + "_" + result);
				if(data.errorLog == null)
					dataHandler.insertErrorData(data.result, data.signature, ref, data.url, data.errorLog);
				else
					dataHandler.insertErrorData(data.result, data.signature, ref, data.url, data.errorLog.replace("\n", "\t"));
			}
		}
		//count for result
		Map<String, Integer> map = count(result_ref);
		Map<String, Integer> pass = new HashMap<String, Integer>();
		Map<String, Integer> compileError = new HashMap<String, Integer>();
		Map<String, Integer> runtimeError = new HashMap<String, Integer>();
		Map<String, Integer> fail = new HashMap<String, Integer>();
		Set<String> type = new HashSet<String>();
		for(String key : map.keySet()){
			String rType = key.split("_", 2)[0];
			String result = key.split("_", 2)[1];
			if(result.equals("PASS"))
				pass.put(rType, map.get(key));
			else if(result.equals("COMPILE_ERROR"))
				compileError.put(rType, map.get(key));
			else if(result.equals("RUNTIME_ERROR"))
				runtimeError.put(rType, map.get(key));
			else if(result.equals("FAIL"))
				fail.put(rType, map.get(key));
			else{
				throw new AssertionError();
			}
			type.add(rType);
		}
		//insert result
		for(String key : type){
			String rType = key.split("_", 1)[0];
			resultHandler.insertErrorResult(rType, pass.getOrDefault(rType, 0), compileError.getOrDefault(rType, 0), runtimeError.getOrDefault(rType, 0), fail.getOrDefault(rType, 0));
		}
		errorDatadao.close();
	}
	
	static private Map<String, Integer> count(List<String> result_ref){
        Map<String, Integer> map = new HashMap<>();
            for (String s: result_ref){
				Integer i = map.getOrDefault(s,0);
				map.put(s, i+1);
            }
        return map;
    }
	
}
