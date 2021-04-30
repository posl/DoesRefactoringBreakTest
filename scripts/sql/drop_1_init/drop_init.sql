delete from commit.chunk where change_file_id in (select change_file_id from commit.file where project='TestEffortEstimationTutorial');
delete from commit.file where project='TestEffortEstimationTutorial';
delete from commit.parent where project='TestEffortEstimationTutorial';
delete from commit.commit where project='TestEffortEstimationTutorial';

delete from refactoring.left_code_range where project='TestEffortEstimationTutorial';
delete from refactoring.right_code_range where project='TestEffortEstimationTutorial';
delete from refactoring.refactoring where project='TestEffortEstimationTutorial';

delete from run.cross where project='TestEffortEstimationTutorial';
delete from run.straight where project='TestEffortEstimationTutorial';
delete from run.ia where project='TestEffortEstimationTutorial';