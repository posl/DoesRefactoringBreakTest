package exe._3_analyze.store;

import exe._3_analyze.ImpactAnalysisService;
import utils.db.Dao;

import java.util.*;

/**
 * This class stores and loads method information
 */
public class StructureAnalyzer4DB {

    /**
     * Store the list of methods
     * @param service
     */
    public static void store(ImpactAnalysisService service){
        Methods4DB x_1 = new Methods4DB(service.registry, service.structureAnalyzerX_1, "X_1");
        Methods4DB x = new Methods4DB(service.registry, service.structureAnalyzerX, "X");
        Dao<Methods4DB> dao = new Dao<>(Methods4DB.class);
        dao.init();
        dao.insert(x_1);


        dao.insert(x);
        dao.close();
    }

    /**
     * Loads methods from database
     * @param project
     * @param commit
     * @param type
     */
    public StructureAnalyzer4DB(String project, String commit, String type){
        Dao<Methods4DB> dao = new Dao<>(Methods4DB.class);
        dao.init();
        dao.setWhere("project",project);
        dao.setWhere("commitId",commit);
        dao.setWhere("type",type);
        Methods4DB x = dao.select().get(0);
        dao.close();
        this.setUp(x);
    }

    /**
     * Receive signature and Returns method information
     */
    private Map<String, MethodDefinition4DB> structureBySignature;
    /**
     * Receive File name and returns a list of methods in the file
     */
    private Map<String, List<String>> structureByFileName;//filename, signature

    /**
     * This prepares the above two attributes when the constructor is invoked
     * @param methods
     */
    private void setUp(Methods4DB methods){
        structureBySignature = new HashMap<>();
        structureByFileName = new HashMap<>();
        for(MethodDefinition4DB m: methods.methods){
            structureBySignature.put(m.signature, m);
            List<String> li = structureByFileName.getOrDefault(m.fileName, new ArrayList<>());
            li.add(m.signature);
            structureByFileName.put(m.fileName, li);
        }
    }
    /**
     * returns a list of signatures in the file
     */
    public List<String> getSignatures(String filePath){
        return this.structureByFileName.get(filePath);
    }
    /**
     * returns a method information
     */
    public MethodDefinition4DB getMethod(String signature) {
        return structureBySignature.get(signature);
    }

    /**
     * returns methods in the file
     */
    private List<MethodDefinition4DB> getMethods(String filename) {
        List<String> signatures = this.structureByFileName.get(filename);
        if(signatures==null) return null;
        List<MethodDefinition4DB> methods = this.getMethods(signatures);
        return methods;
    }

    /**
     * returns methods in the given signatures
     */
    private List<MethodDefinition4DB> getMethods(List<String> signatures) {
        List<MethodDefinition4DB> methods = new ArrayList<>();
        for(String signature: signatures){
            methods.add(structureBySignature.get(signature));
        }
        return methods;
    }

    /**
     * returns all the methods in the production code
     */
    public Set<String> getProductionSignature() {
        Set<String> set = new HashSet<>();
        for(MethodDefinition4DB md: structureBySignature.values()){
            if(!md.isInTest){
                set.add(md.signature);
            }
        }
        return set;
    }

    /**
     * returns all the methods in the test code
     */
    public Set<String> getTestSignature() {
        Set<String> set = new HashSet<>();
        for(MethodDefinition4DB md: structureBySignature.values()){
            if(md.isInTest) {
                if(md.isTestCase){
                    set.add(md.signature);
                }
            }
        }
        return set;
    }


}
