package exe._2_run;

import beans.other.run.Registry;
import beans.other.run.PureRegistry;
import modules.build.BuildRunner;
import modules.build.BuildRunnerPure;
import utils.db.RegistryDao;
import utils.exception.*;

import java.io.IOException;
/**
 * This class manipulates the straight runner that run tests without setting up dynamic trace tools
 */
public class RefactoringEffortAnalyzerPure extends AbstractRefactoringEffortAnalyzer{


    public RefactoringEffortAnalyzerPure() throws FinishException {
        super();
    }

    private RegistryDao<PureRegistry> registryDao;
    /**
     * This method gets a target to be built from database
     * @return
     */
    @Override
    protected Registry init() {
        registryDao = new RegistryDao<>(PureRegistry.class);
        registryDao.init();
        return registryDao.getOneFromMany();
    }
    /**
     * This method store the result of builds
     * @return
     */
    @Override
    protected void finish(Registry registry) {
        PureRegistry r = (PureRegistry)registry;
        registryDao.insert(r);
        registryDao.close();
    }


    /**
     * This method creates a runner that builds and tests
     * @return
     */
    @Override
    protected BuildRunner getBuildRunner() throws IOException, NoTargetBuildFileException, DependencyProblemException, ProductionProblemException, NoSureFireException {
        return new BuildRunnerPure(sm, git, commit.commitId);
    }

    public static void main(String[] args) throws FinishException {
        AbstractRefactoringEffortAnalyzer.execute(new RefactoringEffortAnalyzerPure());
    }

}
