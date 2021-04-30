package exe._2_run;

import beans.other.run.CrossRegistry;
import beans.other.run.Registry;
import modules.build.BuildRunner;
import modules.build.BuildRunnerCross;
import utils.db.RegistryDao;
import utils.exception.*;

import java.io.IOException;

public class RefactoringEffortAnalyzerCross extends AbstractRefactoringEffortAnalyzer{


    public RefactoringEffortAnalyzerCross() throws FinishException {
        super();
    }
    private RegistryDao<CrossRegistry> registryDao;
    /**
     * This method gets a target to be built from database
     * @return
     */
    @Override
    protected Registry init() {
        registryDao = new RegistryDao<>(CrossRegistry.class);
        registryDao.init();
        return registryDao.getOneFromMany();
    }
    /**
     * This method store the result of builds
     * @return
     */
    @Override
    protected void finish(Registry registry) {
        //store registry data
        CrossRegistry r = (CrossRegistry)registry;
        registryDao.insert(r);
        registryDao.close();
    }

    /**
     * This method creates a runner that builds and tests
     * @return
     */
    @Override
    protected BuildRunner getBuildRunner() throws IOException, NoParentsException, NoTargetBuildFileException, DependencyProblemException, ProductionProblemException, NoSureFireException {
        return new BuildRunnerCross(sm, git, commit.commitId);
    }


    public static void main(String[] args) throws FinishException {
        AbstractRefactoringEffortAnalyzer.execute(new RefactoringEffortAnalyzerCross());
    }

}
