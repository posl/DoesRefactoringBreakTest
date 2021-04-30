package exe._2_run.specific;

import beans.other.run.Registry;
import beans.other.run.StraightRegistry;
import exe._2_run.AbstractRefactoringEffortAnalyzer;
import modules.build.BuildRunner;
import modules.build.BuildRunnerStraight;
import utils.db.RegistryDao;
import utils.exception.*;

import java.io.IOException;

public class RefactoringEffortAnalyzerStraightSpecific extends AbstractRefactoringEffortAnalyzer {


    public RefactoringEffortAnalyzerStraightSpecific(String project, String commitId) throws FinishException {
        this.registry = new StraightRegistry(project, commitId);
        super.initiate();
    }

    private RegistryDao<StraightRegistry> registryDao;

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
        StraightRegistry r = (StraightRegistry)registry;
        registryDao = new RegistryDao<>(StraightRegistry.class);
        registryDao.init();
        registryDao.update(r);
        registryDao.close();
    }



    @Override
    protected BuildRunner getBuildRunner() throws IOException, NoTargetBuildFileException, DependencyProblemException, ProductionProblemException, NoSureFireException {
        return new BuildRunnerStraight(sm, git, commit.commitId);
    }

    public static void main(String[] args) throws FinishException {
        String project = "javapoet";
        String commitId = "034f0b6801eda34c96d880051f4611555a948ffb";

        AbstractRefactoringEffortAnalyzer.execute(new RefactoringEffortAnalyzerStraightSpecific(project, commitId));
    }

}
