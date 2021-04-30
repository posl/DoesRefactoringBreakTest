package modules.refactoring.trace;

import beans.commit.Commit;
import beans.test.rowdata.TestInfo;
import beans.trace.ExecutionTrace;
import modules.build.controller.BuildToolController;
import modules.build.controller.maven.MavenController;
import modules.git.GitController;
import modules.refactoring.detect.RefactoringMinerController;
import modules.source.impact_analysis.ImpactAnalyzer;
import modules.source.structure.StructureAnalyzer;
import org.refactoringminer.api.Refactoring;
import utils.exception.NoParentsException;
import utils.log.MyLogger;
import utils.setting.SettingManager;

import java.util.List;

public class ImpactAnalyzerController {
    MyLogger logger = MyLogger.getInstance();
    /**
     * manipulate build tool
     */
    BuildToolController maven;
    /**
     * impact analyzer controller
     */
    private final ImpactAnalyzer impact;
    /**
     * target commit to be analyzed
     */
    private final Commit commit;
    /**
     * list of commits in the target commit
     */
    private final List<Refactoring> refactorings;
    private final SettingManager sm;

    /**
     * get refactoring list and scan the structure of the code in the commit X
     * @param sm
     * @param commitId
     * @throws NoParentsException
     */
    public ImpactAnalyzerController(SettingManager sm, String commitId) throws NoParentsException {
        this.sm = sm;
        GitController gitX = new GitController(sm,"/x/");
        maven = new MavenController(gitX);
        maven.checkout(commitId);
        refactorings = RefactoringMinerController.getRefactoringInstanceAtCommit(gitX.getRepo(), commitId);
        commit = gitX.getCommit();
        //init
        StructureAnalyzer analyzer = new StructureAnalyzer(maven);
        analyzer.scan();
        analyzer.flagChange(commit);
        analyzer.setRefactoring(refactorings);
        //impact
        this.impact = ImpactAnalyzer.getInstance(maven.getSettingManager().getSetting("impact_analyzer"), analyzer);
    }

    /**
     * this is the same as the above but for commit X-1
     * @param sm
     * @param mavenX_1
     * @param commit
     * @param refactorings
     */
    private ImpactAnalyzerController(SettingManager sm, BuildToolController mavenX_1, Commit commit, List<Refactoring> refactorings) {
        this.sm = sm;
        this.maven = mavenX_1;
        this.refactorings = refactorings;
        mavenX_1.checkout(commit.commitId, true);
        this.commit = commit;
        StructureAnalyzer analyzer = new StructureAnalyzer(mavenX_1, true);
        analyzer.scan();
        analyzer.flagChange(commit);
        analyzer.setRefactoring(this.refactorings);
        this.impact = ImpactAnalyzer.getInstance(this.sm.getSetting("impact_analyzer"), analyzer);
    }

    /**
     * create impact analyzer for commit X-1
     * @return
     * @throws NoParentsException
     */
    public ImpactAnalyzerController getImpactAnalyzerX_1() throws NoParentsException {
        GitController gitX_1 = new GitController(sm,"/x_1/");
        BuildToolController mavenX_1 = new MavenController(gitX_1);
        return new ImpactAnalyzerController(sm, mavenX_1, commit, refactorings);
    }

    /**
     * conduct impact analysis
     * @param ti
     * @param paths
     */
    public void analyze(TestInfo ti, List<ExecutionTrace> paths){
        impact.analyze(ti, paths);
    }


    /**
     * return structure analyzer
     * @return
     */
    public StructureAnalyzer getStructureAnalyzer() {
        return impact.getStructureAnalyzer();
    }

    /**
     * get commit id
     * @return
     */
    public String getCommitId(){
        return maven.getCommitId();
    }

    public Refactoring getRefactoring(Integer hash){
        for(Refactoring r: refactorings){
            if(r.hashCode()== hash){
                return r;
            }
        }
        return null;
    }

    public List<Refactoring> getRefactorings(){
        return refactorings;
    }
}
