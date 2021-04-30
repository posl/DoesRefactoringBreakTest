package modules.source.structure;

import beans.commit.ChangedFile;
import beans.commit.Chunk;
import beans.commit.Commit;
import beans.source.MethodDefinition;
import beans.source.TestMethodDefinition;
import gr.uom.java.xmi.*;
import gr.uom.java.xmi.diff.CodeRange;
import modules.build.controller.BuildToolController;
import org.refactoringminer.api.Refactoring;
import utils.file.MyFileNameUtils;
import utils.log.MyLogger;
import utils.program.MyProgramUtils;
import utils.uml.UmlUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Scan the structure of the source code.
 * What classes and methods are included and which lines they range in the file.
 */
public class StructureAnalyzer {
    static MyLogger logger = MyLogger.getInstance();
    BuildToolController bc;
    /**
     * the top directory path of the production code.
     */
    public String srcDir;
    /**
     * the top directory path of the test code.
     */
    public String testDir;
    /**
     * the top directory path of the target files (e.g., classes, META-INF).
     */
    public String targetDir;
    /**
     * Map to return MethodDefinition by method signature
     */
    private Map<String, MethodDefinition> structureBySignature;
    /**
     * Map to return MethodDefinitions by file name
     */
    private Map<String, List<String>> structureByFileName;//filename, signature
    public String prefix="";
    /**
     * whether this is the structure in the parent commit. (This is for Cross build)
     */
    public boolean isX_1 = false;
    /**
     * Map for variables name.
     * Receive filePath@lineNo and return path.variableName
     */
    public Map<String, Set<String>> fieldsPosition;//filePath+lineNo->path+variableName


    public StructureAnalyzer(BuildToolController maven, boolean isX_1){
        this(maven);
        this.isX_1 = isX_1;
    }

    public StructureAnalyzer(BuildToolController maven){
        this.bc = maven;
        srcDir = MyFileNameUtils.getDirectoryName(bc.getSrcDirSetting());
        testDir = MyFileNameUtils.getDirectoryName(bc.getTestDirSetting());//do not end with slash
        targetDir = MyFileNameUtils.getDirectoryName(bc.getTargetDir(false));//do not end with slash
        structureByFileName = new TreeMap<>();
        structureBySignature = new TreeMap<>();
        fieldsPosition = new TreeMap<>();
    }
    public void scan(){
        this.analyze(bc.getHomeDir(), targetDir);
    }

    public List<String> getSignatures(String filePath){
        return this.structureByFileName.get(filePath);
    }

    public MethodDefinition getMethod(String signature) {
        return structureBySignature.get(signature);
    }

    /**
     * receive file path and line no and return method
     * @param path
     * @param line
     * @return
     */
    public MethodDefinition getMethod(String path, Integer line) {
        if(!path.endsWith(".java")) return null;
        if(path.endsWith("package-info.java")) return null;
        List<String> signatures = this.structureByFileName.get(path);
        if(signatures!=null) {
            for(String s: signatures){
                MethodDefinition m = this.getMethod(s);
                if(m.isIn(line)){
                    return m;
                }
            }
        }
        return null;
    }

    /**
     * receive file path and returns the methods in the file
     * @param path
     * @param line
     * @return
     */
    private List<MethodDefinition> getMethods(String filename) {
        List<String> signatures = this.structureByFileName.get(filename);
        if(signatures==null) return null;
        List<MethodDefinition> methods = this.getMethods(signatures);
        return methods;
    }
    /**
     * receive a list of method signatures and returns method definitions
     * @param path
     * @param line
     * @return
     */
    private List<MethodDefinition> getMethods(List<String> signatures) {
        List<MethodDefinition> methods = new ArrayList<>();
        for(String signature: signatures){
            methods.add(structureBySignature.get(signature));
        }
        return methods;
    }

    /**
     * returns a list of all the signature in the production code
     * @return
     */
    public Set<String> getProductionSignature() {
        Set<String> set = new HashSet<>();
        for(MethodDefinition md: structureBySignature.values()){
            if(!md.isInTest()){
                set.add(md.getSignature());
            }
        }
        return set;
    }

    /**
     * returns a list of all the signature in the test code
     * @return
     */
    public Set<String> getTestSignature() {
        Set<String> set = new HashSet<>();
        for(MethodDefinition md: structureBySignature.values()){
            if(md.isInTest()) {
                TestMethodDefinition tmd = (TestMethodDefinition) md;
                if(tmd.isTestCase()){
                    set.add(md.getSignature());
                }
            }
        }
        return set;
    }


    /**
     * create a list of method definitions using umlClass given by AST
     * @param root
     * @param targetDir
     */
    private void analyze(String root, String targetDir) {
        File f = new File(root);
        logger.trace("root: "+root);
        UMLModel model = getUmlModel(f);
        List<UMLClass> classes = model.getClassList();


        for (UMLClass umlClass: classes){
            if(umlClass.getSourceFile().startsWith(targetDir)){
                continue;//some times class binary files are scanned
            }

            this.addMethods(umlClass, classes);
            for(UMLAnonymousClass a: umlClass.getAnonymousClassList()){
                this.addMethods(a, classes);
            }

        }


    }

    /**
     * read files and returns UMLModels
     * @param f
     * @return
     */
    private UMLModel getUmlModel(File f) {
        UMLModelASTReader reader = null;
        try {
            reader = new UMLModelASTReader(f);
        } catch (IOException e) {
            logger.error(e);
            throw new AssertionError();
        }
        return reader.getUmlModel();
    }


    /**
     *
     * @param umlClass
     * @param classes
     */
    public void addMethods(UMLAbstractClass umlClass, List<UMLClass> classes) {
        MethodDefinition md;
        List<String> methodsInAClass = new ArrayList<>();
        extractFields(umlClass);
        for(UMLOperation umlOperation: umlClass.getOperations()){
            if(umlClass.getSourceFile().startsWith(this.testDir)) {
                md = new TestMethodDefinition(umlClass, umlOperation, classes);
            }else{
                md = new MethodDefinition(umlClass, umlOperation, classes);
            }

            //check just in case
            if(UmlUtils.checkIfMethod(umlOperation)){
                methodsInAClass.add(md.signature);
                assert (!this.structureBySignature.containsKey(md.signature));
                this.structureBySignature.put(md.signature, md);
            }else{
                logger.error(md.signature);
                throw new AssertionError();
            }
        }
        String fileKey = prefix + umlClass.codeRange().getFilePath();
        //To deal with a bug in UMLClass
        List<String> tmp = structureByFileName.getOrDefault(fileKey, new ArrayList<String>());
        tmp.addAll(methodsInAClass);//These are for subClass
        structureByFileName.put(fileKey, tmp);

    }

    /**
     * scan attributions (fields) in a class
     * @param umlClass
     */
    private void extractFields(UMLAbstractClass umlClass) {//filePath+lineNo=List<path+variableName>(classies t)
        String fileName = MyProgramUtils.getQualifiedName(umlClass.getSourceFile(), srcDir, testDir);
        if(umlClass instanceof UMLClass){
            List<UMLAttribute> fields = umlClass.getAttributes();
            for(UMLAttribute i: fields){
                String filePath = i.getLocationInfo().getFilePath();
                Integer startNo = i.getLocationInfo().getStartLine();
                Integer endNo = i.getLocationInfo().getEndLine();

                String variableType = i.getType().getClassType();
                String variableName = i.getName();
                String variableQualifiedName = MyProgramUtils.getQualifiedName(filePath, srcDir, testDir)+"."+variableName;

                for(int k=startNo;k<=endNo;k++){
//                    System.out.println(k);
                    String key = MyProgramUtils.getFieldSignature(filePath,k);
                    Set set = this.fieldsPosition.getOrDefault(key, new HashSet<>());
                    set.add(variableQualifiedName);
                    this.fieldsPosition.put(key, set);
                }
            }
        }

    }

    public Collection<String> getAllSignatures() {
        return this.structureBySignature.keySet();
    }

    public Collection<MethodDefinition> getAllMethods() {
        return this.structureBySignature.values();
    }
    public Collection<String> getAllFiles() {
        return this.structureByFileName.keySet();
    }
    public boolean hasMethod(String signature) {
        return this.structureBySignature.containsKey(signature);
    }

    public boolean hasFile(String path) {
        return this.structureByFileName.containsKey(path);
    }


    public void updateMethod(MethodDefinition md) {
        this.structureBySignature.put(md.signature, md);
    }

    /**
     * flag the changed line by a commit
     *
     */
    public void flagChange(Commit commit) {
        for (ChangedFile cf : commit.changedFileList) {
            for (Chunk c : cf.chunks) {
                if(isX_1){
                    flagChange(cf.oldPath, c.getOldStartNo(), c.getOldEndNo());
                }else{
                    flagChange(cf.newPath, c.getNewStartNo(), c.getNewEndNo());
                }
            }
        }
    }
    /**
     * flag the changed line by a commit
     *
     */
    protected void flagChange(String path, int startNo, int endNo) {
        for (int lineNo = startNo + 1; lineNo <= endNo; lineNo++) {
            MethodDefinition mdX = this.getMethod(path, lineNo);
            if(mdX!=null){//if out of the method
                mdX.changedLines.put(lineNo, true);
                this.updateMethod(mdX);
            }

        }
    }

    /**
     * assign refactoring edits information on methods
     * @param refactorings
     */
    public void setRefactoring(List<Refactoring> refactorings) {
        for (Refactoring rf : refactorings) {
            boolean isFieldRefactoring = isFieldRefactoring(rf);
            if(isX_1) {
                List<CodeRange> left = rf.leftSide();
                setRefactoring(rf, left);
                if(isFieldRefactoring){
                    setFieldRefactoring(rf, left);
                }
            }else{
                List<CodeRange> right = rf.rightSide();
                setRefactoring(rf, right);
                if(isFieldRefactoring){
                    setFieldRefactoring(rf, right);
                }
            }
        }
    }

    /**
     * assign a refactoring edit information on methods
     * @param rf
     * @param codeRanges
     */
    protected void setRefactoring(Refactoring rf, List<CodeRange> codeRanges) {
        for (CodeRange codeRange : codeRanges) {
            for (int lineNo = codeRange.getStartLine(); lineNo <= codeRange.getEndLine(); lineNo++) {
                MethodDefinition mdX_1 = this.getMethod(codeRange.getFilePath(), lineNo);
                if (mdX_1 != null) {
                    Set<Refactoring> rf2List = mdX_1.inherentRefactorings.get(lineNo);
                    rf2List.add(rf);
                    mdX_1.inherentRefactorings.put(lineNo, rf2List);

                    this.updateMethod(mdX_1);
                }
            }
        }
    }

    Set<String> fieldRefactorings= new HashSet(
            Arrays.asList(new String[]{
                    "Move Attribute", "Pull Up Attribute","Push Down Attribute", "Rename Attribute",
                    "Move And Rename Attribute", "Replace Variable With Attribute", "Replace Attribute (With Attribute)",
                    "Merge Attribute", "Split Attribute", "Change Attribute Type", "Extract Attribute", "Add Attribute Annotation",
                    "Remove Attribute Annotation", "Modify Attribute Annotation"
            }));

    /**
     * check field refactoring
     * @param rf
     * @return
     */
    private boolean isFieldRefactoring(Refactoring rf) {
        String dispName = rf.getRefactoringType().getDisplayName();
        return fieldRefactorings.contains(dispName);
    }

    /**
     * flag field refactoring in the method that use the refactored fields
     * @param refactoringType
     * @param codeRanges
     */
    public void setFieldRefactoring(Refactoring refactoringType, List<CodeRange> codeRanges) {
        for(CodeRange r: codeRanges){
            String filePath = r.getFilePath();
            Integer lineNo = r.getStartLine();
            Set<String> fieldTypeFullPath = this.getFields(filePath, lineNo);
            for(MethodDefinition md: this.getAllMethods()){
                md.setFieldRefactoring(refactoringType, fieldTypeFullPath);
            }
        }
    }

    public TestMethodDefinition getTestMethod(String s) {
        return (TestMethodDefinition) this.getMethod(s);
    }

    /**
     * receive file path and line no, and then return field name
     * @param lineNo
     * @return
     */
    public Set<String> getFields(String filePath, Integer lineNo) {
        return fieldsPosition.get(MyProgramUtils.getFieldSignature(filePath, lineNo));
    }

    /**
     * returns all the test files
     * @return
     */
    public Set<String> getTestFiles() {
        Set<String> files = new HashSet<>();
        for(MethodDefinition md: this.structureBySignature.values()){
            if(md.isInTest()){
                files.add(md.getFileName().split("\\$")[0]);
            }
        }
        return files;
    }
}
