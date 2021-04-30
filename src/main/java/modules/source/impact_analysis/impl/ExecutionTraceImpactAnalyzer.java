package modules.source.impact_analysis.impl;

import beans.refactoring.Refactoring2;

import beans.source.MethodDefinition;
import beans.source.TestMethodDefinition;
import beans.test.rowdata.TestInfo;
import beans.trace.ExecutionTrace;
import modules.source.impact_analysis.ImpactAnalyzer;
import modules.source.structure.StructureAnalyzer;
import org.refactoringminer.api.Refactoring;
import utils.program.MyProgramUtils;

import java.util.*;

public class ExecutionTraceImpactAnalyzer implements ImpactAnalyzer {
    public static final int MAX_LAYER = 1;//If the limitation of calls is needed (NOT USED)
    StructureAnalyzer structureAnalyzer;

    public ExecutionTraceImpactAnalyzer(StructureAnalyzer rs){
        this.structureAnalyzer = rs;
    }

    /**
     * For each line of the test method, we conduct the impact analysis
     * @param test
     * @param passedLine
     */
    public void analyze(TestInfo test, List<ExecutionTrace> passedLine) {
        for(ExecutionTrace trace: passedLine){
            this.initiate(trace.passes);
            this.directImpact(test, trace.lineNo, trace.passes);
            this.inDirectImpact(test, trace.lineNo, trace.passes);
        }

    }

    public StructureAnalyzer getStructureAnalyzer(){
        return structureAnalyzer;
    }

    /**
     * delete redundant lines that are not shown in the source code
     * @param passedLines
     */
    private void initiate(List<String> passedLines) {
        List<String> deleter = new ArrayList<>();
        for(String trace: passedLines){
            String signature = this.getSignature(trace);
            MethodDefinition md = structureAnalyzer.getMethod(signature);
            if (md == null) {
                if (MyProgramUtils.isConstructor(signature)) {
                    deleter.add(trace);
                }else if(this.commonMethod(signature)){
                    deleter.add(trace);
                }else {//We cannot handle generics
                    logger.error("This might be generics pattern: " + signature);
                    deleter.add(trace);
                }
            }
        }
        passedLines.removeAll(deleter);
    }
    List<String> commons = Arrays.asList("values#", "valueOf#String");//, "toString#", "equals#Object", "hash#"
    private boolean commonMethod(String signature) {
        String tmp = MyProgramUtils.getMethodNameFromSignature(signature, true);
        return commons.contains(tmp);
    }

    /**
     * for each line of the test method, we explore refactoring edits in the paths in the production code
     * @param info
     * @param tLine
     * @param passedLines
     */
    private void directImpact(TestInfo info, Integer tLine, List<String> passedLines) {
        if(info.isCompileError()){// skip test methods that have compiler errors
            return;
        }

        MethodDefinition md = structureAnalyzer.getMethod(info.getSignature());
        logger.trace(info.getSignature());
        TestMethodDefinition tm = (TestMethodDefinition) md;
        logger.trace("tm.signature: "+tm.signature);
        if(!(tm.isTestCase())){
            return;
        }
        //find refactoring edits
        tm = this.setRefactoringByLine(tm, tLine, passedLines);
        logger.trace("test refactoring: " + md.inherentRefactorings);
        logger.trace("test directly affected: " + tm.directRefactorings);
        structureAnalyzer.updateMethod(tm);
    }

    /**
     *
     * @param tm
     * @param tLine
     * @param passedLines
     * @return
     */
    private TestMethodDefinition setRefactoringByLine(TestMethodDefinition tm, Integer tLine, List<String> passedLines) {
        Set<String> layers = new HashSet<>();
        String previousLayer = tm.getSignature();
        for(String trace:  passedLines) {
            String signature = this.getSignature(trace);
            Integer lineNo = this.getLineNo(trace);
            assert (lineNo!=null);
            if (tm.getSignature().equals(signature)) {//if the line in the test, skip
                continue;
            }
            if(!previousLayer.equals(signature)) {
                if(layers.contains(signature)){//when method A line 1 -> method B line1 -> method A line 2
                    layers.remove(previousLayer);//reduce layer number
                }else{//first access to the method
                    layers.add(signature);//increment the layers
                    tm = this.setRefactoringByMethod(tm, tLine, signature, lineNo, layers.size());
                    previousLayer = signature;
                }
            }

            MethodDefinition pMethod = structureAnalyzer.getMethod(signature);
            if (pMethod == null) {
                logger.trace("pSignature: "+ signature);//SELogger calls implicit constructor
            }else if (pMethod instanceof TestMethodDefinition){
                //Skip refactoring in test code
            }else {
                Set<Refactoring> newRefs = pMethod.inherentRefactorings.get(lineNo);
                tm = this.setRefactoring(tm, tLine, newRefs, layers.size(), signature);
            }

        }
        return tm;
    }

    /**
     * get refactoring edits for the method signature.
     * @param tm
     * @param tLine
     * @param pSignature
     * @param pLineNo
     * @param layer
     * @return
     */
    private TestMethodDefinition setRefactoringByMethod(TestMethodDefinition tm, Integer tLine, String pSignature, Integer pLineNo, Integer layer) {
        MethodDefinition pMethod = structureAnalyzer.getMethod(pSignature);
        if (pMethod == null) {//SElogger detects Implicit constructor but Source code does not have it. Then pMethod will be null
            return tm;
        }
        if (pMethod instanceof TestMethodDefinition){
            return tm;//refactoring in test will be skipped
        }
        for(int i=pLineNo;pMethod.start<=i;i--){//find refactoring from the first exercised line in the method to the top line of the method
            Set<Refactoring> newRefs = pMethod.inherentRefactorings.get(i);
            tm = this.setRefactoring(tm, tLine, newRefs, layer, pSignature);
        }
        return tm;
    }



    private Integer getLineNo(String trace) {
        return Integer.parseInt(trace.split("@")[1]);
    }

    /**
     * parse the string
     * e.g.,
     * signature@line
     * @param trace
     * @return
     */
    private String getSignature(String trace) {
        return trace.split("@")[0];
    }

    /**
     * set refactoring information to the test method
     * @param tm
     * @param tLine
     * @param newRefs
     * @param layer
     * @param pSignature
     * @return
     */
    private TestMethodDefinition setRefactoring(TestMethodDefinition tm, Integer tLine,  Set<Refactoring> newRefs, int layer, String pSignature){
        if(newRefs==null){
            return tm;
        }
        logger.trace(tm.getSignature());//logger.trace
        logger.trace(Integer.toString(tLine));//logger.trace
        logger.trace(tm.directRefactorings+"");
        for(Refactoring r: newRefs){
            Refactoring2 r2 = new Refactoring2();
            r2.refactoring = r;
            r2.layer = layer;
            if(pSignature == null)
                r2.whoMade = "NULL_pSignature";
            else{
                r2.whoMade = pSignature;
            }
            r2.refactoringHash = r.hashCode();
            Set<Refactoring2> ref = tm.directRefactorings.get(tLine);
            ref.add(r2);
            tm.directRefactorings.put(tLine, ref);
        }
        return tm;

    }

    private void inDirectImpact(TestInfo info, Integer tLine, List<String> passedLines) {
        //TODO: FUTURE WORK
//        for(MethodDefinition md: analyzerX.getStructureBySignature().values()){
//            if(md instanceof TestMethodDefinition){
//
//            }
//        }
    }

}
