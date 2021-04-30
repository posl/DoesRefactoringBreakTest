package utils.trace;

import beans.other.run.Registry;
import beans.source.PassedLine;
import beans.trace.ExecutionTrace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyTraceUtils {
    /**
     * create ExecutionTrace
     * @param registry
     * @param passedLines
     * @return
     */
    public static List<ExecutionTrace>  transform(Registry registry, Map<String, Map<Integer, List<PassedLine>>> passedLines){
        List<ExecutionTrace> list = new ArrayList<>();
        for(String testSignature: passedLines.keySet()){
            setTrace(list, registry, testSignature, passedLines);
        }
        return list;
    }


    public static void setTrace(List<ExecutionTrace> list, Registry registry, String testSignature, Map<String, Map<Integer, List<PassedLine>>> passedLines) {
        Map<Integer, List<PassedLine>> lines = passedLines.get(testSignature);
        for(Integer i: lines.keySet()){
            ExecutionTrace trace = new ExecutionTrace(registry, testSignature, i);
            trace.add(lines.get(i));
            list.add(trace);
        }
    }

}
