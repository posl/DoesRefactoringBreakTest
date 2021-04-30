package exe._2_run;

import beans.other.run.Registry;
import beans.other.run.StraightRegistry;
import modules.build.BuildRunner;
import modules.build.BuildRunnerStraight;
import utils.db.RegistryDao;
import utils.exception.*;

import java.io.IOException;

/**
 * This class manipulates the straight runner that sets up dynamic trace tools and run tests
 */
public class RefactoringEffortAnalyzerStraight extends AbstractRefactoringEffortAnalyzer{


    public RefactoringEffortAnalyzerStraight() throws FinishException {
        super();
    }

    private RegistryDao<StraightRegistry> registryDao;

    /**
     * This method gets a target to be built from database
     * @return
     */
    @Override
    protected Registry init() {
        registryDao = new RegistryDao<>(StraightRegistry.class);
        registryDao.init();
        return registryDao.getOneFromMany();
    }

    /**
     * This method store the result of builds
     * @return
     */
    @Override
    protected void finish(Registry registry) {
        StraightRegistry r = (StraightRegistry)registry;
        registryDao.insert(r);
        registryDao.close();
    }


    /**
     * This method creates a runner that builds and tests
     * @return
     */
    @Override
    protected BuildRunner getBuildRunner() throws IOException, NoTargetBuildFileException, DependencyProblemException, ProductionProblemException, NoSureFireException {
        return new BuildRunnerStraight(sm, git, commit.commitId);
    }

    public static void main(String[] args) throws FinishException {
        AbstractRefactoringEffortAnalyzer.execute(new RefactoringEffortAnalyzerStraight());
    }

}
