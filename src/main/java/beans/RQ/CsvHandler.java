package beans.RQ;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CsvHandler {
	public String filepath;
	public String project;
	private static final String COMMA = ",";
	private static final String NEW_LINE= "\r\n";
	public CsvHandler(String filepath, String project){
		this.filepath = filepath;
		this.project = project;
	}

	//make headline
	public void initErrorData(Boolean isResult){
		List<String> init = new ArrayList<String>();
		if(!isResult){
			String[] title = {"project", "result", "RefactoringType", "url", "signature", "whoMade"};
			Collections.addAll(init, title);
		}else{
			String[] title = {"project", "RefactoringType", "#PASS", "#COMPILE ERROR", "#RUNTIME ERROR", "#FAIL", "PASS", "COMPILE ERROR", "RUNTIME ERROR", "FAIL"};
			Collections.addAll(init, title);
		}
		init(init);
	}

	//make headline
	public void initAddedTestData(Boolean isResult){
		List<String> init = new ArrayList<String>();
		if(!isResult){
			String[] title = {"project", "result", "RefactoringType", "url", "signature", "whoMade"};
			Collections.addAll(init, title);
		}else{
			String[] title = {"project", "RefactoringType", "#ADD", "#MODIFY", "#DELETE", "ADD", "MODIFY", "DELETE"};
			Collections.addAll(init, title);
		}
		init(init);
	}

	//make headline
	public void initModifiedLineData(Boolean isResult){
		List<String> init = new ArrayList<String>();
		if(!isResult){
			String[] title = {"project", "RefactoringType", "url", "signature", "whoMade", "All Changed Lines", "Trace Changed"};
			Collections.addAll(init, title);
		}
		else{
			String[] title = {"project", "RefactoringType", "Total change line in affected methods", "Total chenge in passed test code"};
			Collections.addAll(init, title);
		}
		init(init);
	}

	//setup for using scv
	public void init(List<String> title){
		FileWriter fileWriter = null;
		try{
			fileWriter = new FileWriter(this.filepath, true);
			for(String s : title){
				fileWriter.append(s);
				fileWriter.append(COMMA);
			}
			fileWriter.append(NEW_LINE);
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
			fileWriter.flush();
			fileWriter.close();
			} catch (IOException e) {
			e.printStackTrace();
			}
		}
	}
	
	//insert data
	public void insertErrorData(String result, String signature, Refactoring4TestResults ref, String url, String errorLog){
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(this.filepath, true);
			fileWriter.append(this.project);
			fileWriter.append(COMMA);
			fileWriter.append(result);
			fileWriter.append(COMMA);
			fileWriter.append(ref.refactoring);
			fileWriter.append(COMMA);
			fileWriter.append(url);
			fileWriter.append(COMMA);
			fileWriter.append(signature);
			fileWriter.append(COMMA);
			fileWriter.append(ref.whoMade);
			fileWriter.append(COMMA);
			fileWriter.append(errorLog);
			fileWriter.append(NEW_LINE);
		  } catch (Exception e) {
			e.printStackTrace();
		  } finally {
			try {
			  fileWriter.flush();
			  fileWriter.close();
			} catch (IOException e) {
			  e.printStackTrace();
			}
		}
	}

	//insert data
	public void insertAddedTestData(String result, String signature, Refactoring4AddedTests ref, String url){
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(this.filepath, true);
			fileWriter.append(this.project);
			fileWriter.append(COMMA);
			fileWriter.append(result);
			fileWriter.append(COMMA);
			fileWriter.append(ref.refactoring);
			fileWriter.append(COMMA);
			fileWriter.append(url);
			fileWriter.append(COMMA);
			fileWriter.append(signature);
			fileWriter.append(COMMA);
			fileWriter.append(ref.whoMade);
			fileWriter.append(NEW_LINE);
		  } catch (Exception e) {
			e.printStackTrace();
		  } finally {
			try {
			  fileWriter.flush();
			  fileWriter.close();
			} catch (IOException e) {
			  e.printStackTrace();
			}
		}
	}

	//insert data
	public void insertModifiedLineData(String signature, Refactoring4ModifiedLines ref, String url, Integer allChangedLines, Integer traceChanged){
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(this.filepath, true);
			fileWriter.append(this.project);
			fileWriter.append(COMMA);
			fileWriter.append(ref.refactoring);
			fileWriter.append(COMMA);
			fileWriter.append(url);
			fileWriter.append(COMMA);
			fileWriter.append(signature);
			fileWriter.append(COMMA);
			fileWriter.append(ref.whoMade);
			fileWriter.append(COMMA);
			fileWriter.append(String.valueOf(allChangedLines));
			fileWriter.append(COMMA);
			fileWriter.append(String.valueOf(traceChanged));
			fileWriter.append(NEW_LINE);
		  } catch (Exception e) {
			e.printStackTrace();
		  } finally {
			try {
			  fileWriter.flush();
			  fileWriter.close();
			} catch (IOException e) {
			  e.printStackTrace();
			}
		}
	}

	//insert data
	public void insertErrorResult(String refactoringType, Integer pass, Integer compileError, Integer runtimeError, Integer fail){
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(this.filepath, true);
			fileWriter.append(this.project);
			fileWriter.append(COMMA);
			fileWriter.append(refactoringType);
			fileWriter.append(COMMA);
			fileWriter.append(String.valueOf(pass));
			fileWriter.append(COMMA);
			fileWriter.append(String.valueOf(compileError));
			fileWriter.append(COMMA);
			fileWriter.append(String.valueOf(runtimeError));
			fileWriter.append(COMMA);
			fileWriter.append(String.valueOf(fail));
			fileWriter.append(COMMA);
			double prob = (double)(100 * (double)pass/(pass + compileError + runtimeError + fail));
			fileWriter.append(String.valueOf(prob));
			fileWriter.append(COMMA);
			prob = (double)(100 * (double)compileError/(pass + compileError + runtimeError + fail));
			fileWriter.append(String.valueOf(prob));
			fileWriter.append(COMMA);
			prob = (double)(100 * (double)runtimeError/(pass + compileError + runtimeError + fail));
			fileWriter.append(String.valueOf(prob));
			fileWriter.append(COMMA);
			prob = (double)(100 * (double)fail/(pass + compileError + runtimeError + fail));
			fileWriter.append(String.valueOf(prob));
			fileWriter.append(NEW_LINE);
		  } catch (Exception e) {
			e.printStackTrace();
		  } finally {
			try {
			  fileWriter.flush();
			  fileWriter.close();
			} catch (IOException e) {
			  e.printStackTrace();
			}
		}
	}

	//insert data
	public void insertAddedTestResult(String refactoringType, Integer add, Integer modify, Integer delete){
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(this.filepath, true);
			fileWriter.append(this.project);
			fileWriter.append(COMMA);
			fileWriter.append(refactoringType);
			fileWriter.append(COMMA);
			fileWriter.append(String.valueOf(add));
			fileWriter.append(COMMA);
			fileWriter.append(String.valueOf(modify));
			fileWriter.append(COMMA);
			fileWriter.append(String.valueOf(delete));
			fileWriter.append(COMMA);
			double prob = (double)(100 * (double)add/(add + modify + delete));
			fileWriter.append(String.valueOf(prob));
			fileWriter.append(COMMA);
			prob = (double)(100 * (double)modify/(add + modify + delete));
			fileWriter.append(String.valueOf(prob));
			fileWriter.append(COMMA);
			prob = (double)(100 * (double)delete/(add + modify + delete));
			fileWriter.append(String.valueOf(prob));
			fileWriter.append(NEW_LINE);
		  } catch (Exception e) {
			e.printStackTrace();
		  } finally {
			try {
			  fileWriter.flush();
			  fileWriter.close();
			} catch (IOException e) {
			  e.printStackTrace();
			}
		}
	}

	//insert data
	public void insertRQ3Result(String refactoringType, Integer allChangedLines, Integer traceChanged){
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(this.filepath, true);
			fileWriter.append(this.project);
			fileWriter.append(COMMA);
			fileWriter.append(refactoringType);
			fileWriter.append(COMMA);
			fileWriter.append(String.valueOf(allChangedLines));
			fileWriter.append(COMMA);
			fileWriter.append(String.valueOf(traceChanged));
			fileWriter.append(NEW_LINE);
		  } catch (Exception e) {
			e.printStackTrace();
		  } finally {
			try {
			  fileWriter.flush();
			  fileWriter.close();
			} catch (IOException e) {
			  e.printStackTrace();
			}
		}
	}
}
