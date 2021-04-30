package utils.uml;

import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.UMLOperation;

public class UmlUtils {
    /**
     * check if the Code Element is method declaration
     * @param j
     * @return
     */
    public static boolean checkIfMethod(UMLOperation j) {
        LocationInfo.CodeElementType tp = j.codeRange().getCodeElementType();
        if(tp.equals(LocationInfo.CodeElementType.METHOD_DECLARATION)){
            return true;
        }else{
            return false;
        }
    }

}
