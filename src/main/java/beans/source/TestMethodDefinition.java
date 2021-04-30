package beans.source;

import beans.refactoring.Refactoring2;
import gr.uom.java.xmi.UMLAbstractClass;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Method Definition for test code
 */
public class TestMethodDefinition extends MethodDefinition{
    /**
     * Refactorings affecting this test methods.
     * When the test exercises the lines that refactoring happens in the production code,
     * this map will have refactoring information.
     */
    public Map<Integer, Set<Refactoring2>> directRefactorings;//lineNo, refactorings
    /**
     * This is for future work.
     */
    public Map<Integer, Set<Refactoring2>> indirectRefactorings;//lineNo, refactorings
    /**
     * When this method has @Test, this variable will be True
     */
    public boolean isTestCase;

    /**
     * Initialize
     *
     * @param umlClass
     * @param umlOperation
     */
    public TestMethodDefinition(UMLAbstractClass umlClass, UMLOperation umlOperation, List<UMLClass> classes) {
        super(umlClass, umlOperation, classes);
        directRefactorings = this.getRefactoringMap();//
        indirectRefactorings = this.getRefactoringMap();
        isTestCase = isTestCase();
    }

    public TestMethodDefinition(){}

    public boolean isInTest(){
        return true;
    }


}
