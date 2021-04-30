package modules.source.execution_trace;

import beans.source.PassedLine;
import modules.build.controller.BuildToolController;
import modules.source.execution_trace.impl.SeLogger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Depend on the setting file, this returns an appropriate tool interface
 */
public interface ExecutionTracer {

    Map<String, Map<Integer, List<PassedLine>>> getPassLinesMap(List<String> testSignatures);

    static ExecutionTracer getInstance(String type, BuildToolController mavenX){
        switch (type){
            case "se_logger":
                return new SeLogger(mavenX);
        }
        return null;
    }

    void clean() throws IOException;
}
