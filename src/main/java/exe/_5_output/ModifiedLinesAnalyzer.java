package exe._5_output;

import beans.RQ.CsvHandler;
import beans.RQ.ModifiedLineData;
import beans.RQ.Refactoring4ModifiedLines;
import beans.other.run.IARegistry;
import utils.db.Dao;
import utils.db.RegistryDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifiedLinesAnalyzer {
	/**
	 * create CSV files about modified lines, extracting from database
	 * @param args
	 */
	public static void main(String[] args){
		//Get target project name
		RegistryDao<IARegistry> iaregistryDao = new RegistryDao<>(IARegistry.class);
		iaregistryDao.init();
		IARegistry iaregistry =  iaregistryDao.getProjectFromRegistry();
		String project = iaregistry.project;	
		//get modified line data which have only one refactoring
		Dao<ModifiedLineData> modifiedLineDatadao = new Dao<>(ModifiedLineData.class);
		modifiedLineDatadao.init();
		modifiedLineDatadao.setWhere("project", project);
		modifiedLineDatadao.setWhere("size", 1);
		//output path
		String dataPath = "./outputs/" + project + "/rq3/data.csv";
		String resultPath = "./outputs/" + project + "/rq3/result.csv";
		CsvHandler dataHandler = new CsvHandler(dataPath, project);
		CsvHandler resultHandler = new CsvHandler(resultPath, project);
		//make indent
		dataHandler.initModifiedLineData(false);
		resultHandler.initModifiedLineData(true);
		//these are each line information
		//<refactoring type, line information
		List<ModifiedLineData> modifiedLineDatas = modifiedLineDatadao.select();
		Map<String, List<Integer>> allChangedLinesMap = new HashMap<String, List<Integer>>();
		Map<String, List<Integer>> traceChangedMap = new HashMap<String, List<Integer>>();
		for(ModifiedLineData data : modifiedLineDatas){
			System.out.println("***** commitId" + data.commit_id + "****");
			for(Refactoring4ModifiedLines ref : data.refset){
				String refType = ref.getRefactoring();
				//store line information to list
				List<Integer> allChangedLinesList = allChangedLinesMap.getOrDefault(refType, new ArrayList<Integer>());
				List<Integer> traceChangedList= traceChangedMap.getOrDefault(refType, new ArrayList<Integer>());			
				Integer  allChangedLines = data.allChangedLines;
				if(allChangedLines == null){
					allChangedLines= 0;
				}
				allChangedLinesList.add(allChangedLines);
				allChangedLinesMap.put(refType, allChangedLinesList);
				dataHandler.insertModifiedLineData(data.signature, ref, data.url,  data.allChangedLines, data.traceChanged);

				Integer traceChanged = data.traceChanged;
				if(traceChanged == null)
					traceChanged = 0;
				traceChangedList.add(traceChanged);
				traceChangedMap.put(refType, traceChangedList);
				
			}
			
		}
		modifiedLineDatadao.close();
		//run by refactoring type in this loop
		for(String ref : allChangedLinesMap.keySet()){
			Integer allChangedLinesSum = 0;
			Integer traceChangedSum = 0;
			//get line information by refactoring type
			for(Integer i :allChangedLinesMap.get(ref))
				allChangedLinesSum += i;
			for(Integer i : traceChangedMap.getOrDefault(ref, new ArrayList<Integer>()))
				traceChangedSum += i;

			resultHandler.insertRQ3Result(ref, allChangedLinesSum, traceChangedSum);
		}
	}
}
