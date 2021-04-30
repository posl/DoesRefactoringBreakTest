package exe._4_aggregate.TestResults.impl;

import beans.RQ.ErrorData;
import beans.refactoring.Refactoring2;
import beans.source.TestMethodDefinition;
import beans.test.result.CrossJunitTestResult;
import beans.test.rowdata.TestInfo;
import exe._3_analyze.ImpactAnalysisService;
import exe._4_aggregate.Mapper;
import modules.source.structure.StructureAnalyzer;
import utils.db.Dao;

import java.util.*;

public class ImplTestResults implements Mapper {
//    StructureAnalyzer structureX;
    StructureAnalyzer structureX_1;
    CrossJunitTestResult crossResult;
    public ImplTestResults(ImpactAnalysisService service, CrossJunitTestResult crossResult){
//        this.structureX = service.structureAnalyzerX;
        this.structureX_1 = service.structureAnalyzerX_1;
        this.crossResult = crossResult;
    }

    ErrorData data;
    Dao<ErrorData> RQ1dataDao = new Dao<>(ErrorData.class);

    @Override
    public Map<String, Integer> run() {
        Map<String, Integer> map = new HashMap();
        List<String> refs = new ArrayList<String>();
        for(TestInfo result: this.crossResult.results){
            String resultStr = result.testResult.type.name();
            String signature = result.getSignature();
            TestMethodDefinition tm = (TestMethodDefinition) structureX_1.getMethod(signature);
            if(tm.directRefactorings != null){
                for(Set<Refactoring2> sr: tm.directRefactorings.values()){
                    if(tm.directRefactorings != null){
                        for(Refactoring2 r: sr){
                                refs.add(r.refactoring.getRefactoringType() + "_" + resultStr);
                        }
                    }
                }
            }
        }
        map = count(refs);
        return map;
    }

    private Map<String, Integer> count(List<String> list){
        Map<String, Integer> map = new HashMap<>();
            for (String ref: list){
                Integer i = map.getOrDefault(ref,0);
                map.put(ref, i+1);
            }
        return map;
    }

}
