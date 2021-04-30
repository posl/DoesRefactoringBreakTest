package modules.source.impact_analysis;

import beans.test.rowdata.TestInfo;
import beans.trace.ExecutionTrace;
import modules.source.impact_analysis.impl.ExecutionTraceImpactAnalyzer;
import modules.source.structure.StructureAnalyzer;
import utils.log.MyLogger;

import java.util.List;
/**
 * Depend on the setting file, this returns an appropriate tool interface
 */
public interface ImpactAnalyzer {
    MyLogger logger = MyLogger.getInstance();
    void analyze(TestInfo test, List<ExecutionTrace> passedLine);
    StructureAnalyzer getStructureAnalyzer();
    static ImpactAnalyzer getInstance(String type, StructureAnalyzer rs){
        switch (type){
            case "dynamic_analysis":
                return new ExecutionTraceImpactAnalyzer(rs);
        }
        return null;
    }
}
