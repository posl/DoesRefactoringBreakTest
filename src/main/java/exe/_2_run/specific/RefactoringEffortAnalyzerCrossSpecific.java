package exe._2_run.specific;

import beans.other.run.Registry;
import beans.other.run.CrossRegistry;
import exe._2_run.AbstractRefactoringEffortAnalyzer;
import modules.build.BuildRunner;
import modules.build.BuildRunnerCross;
import utils.db.RegistryDao;
import utils.exception.*;

import java.io.IOException;

public class RefactoringEffortAnalyzerCrossSpecific extends AbstractRefactoringEffortAnalyzer {


    public RefactoringEffortAnalyzerCrossSpecific(String project, String commitId) throws FinishException {
        this.registry = new CrossRegistry(project, commitId);
        super.initiate();
	}
	
	private RegistryDao<CrossRegistry> registryDao;

    @Override
    protected void setRegistry() throws FinishException {
    }
    @Override
    protected void initiate() {
        //This is a hack
    }

    @Override
    protected Registry init() {
        return null;//dummy
    }

    @Override
    protected void finish(Registry registry) {
		CrossRegistry r = (CrossRegistry)registry;
        registryDao = new RegistryDao<>(CrossRegistry.class);
        registryDao.init();
        registryDao.update(r);
        registryDao.close();
    }



    @Override
    protected BuildRunner getBuildRunner() throws IOException, NoTargetBuildFileException, DependencyProblemException, ProductionProblemException, NoParentsException, NoSureFireException {
        return new BuildRunnerCross(sm, git, commit.commitId);
    }

    public static void main(String[] args) throws FinishException {
        String project = "javapoet";
        String commitId = "b1e1a088321da708d3299138fc55c0a9976a6291";

        AbstractRefactoringEffortAnalyzer.execute(new RefactoringEffortAnalyzerCrossSpecific(project, commitId));
    }

}
