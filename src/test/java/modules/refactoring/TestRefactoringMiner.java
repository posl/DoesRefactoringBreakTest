package modules.refactoring;

import modules.git.GitController;
import modules.refactoring.detect.RefactoringMinerController;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.refactoringminer.api.Refactoring;
import utils.setting.SettingManager;

import java.util.List;

public class TestRefactoringMiner {
    @Test
    public void testAddParameter(){
        String project = "TestEffortEstimationTutorial";
        String commitId = "842e720b135593638a33bccac90c16997b69b336";
        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/x/");
        List<Refactoring> refactoringResults = RefactoringMinerController.getRefactoringInstanceAtCommit(gitX.getRepo(), commitId);
        Assert.assertEquals(1, refactoringResults.size());
        for(Refactoring r: refactoringResults){
            Assert.assertEquals("Add Parameter", r.getRefactoringType().getDisplayName());
        }
    }
    @Test
    public void testAddParameter2(){
        String project = "rxjava-jdbc";
        String commitId = "ade68394d4bfad0492ae84151ce8e5421cbbc556";
        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/x/");
        List<Refactoring> refactoringResults = RefactoringMinerController.getRefactoringInstanceAtCommit(gitX.getRepo(), commitId);
        for(Refactoring r: refactoringResults){
            System.out.println(r.getRefactoringType());
        }
        Assert.assertEquals(1, refactoringResults.size());
        Assert.assertEquals("Add Parameter", refactoringResults.get(0).getRefactoringType().getDisplayName());

    }

    @Test
    public void testChangeFieldType(){
        String project = "TestEffortEstimationTutorial";
        String commitId = "53d2c478232a414a89b9642de81b718d597c44fb";
        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/x/");
        List<Refactoring> refactoringResults = RefactoringMinerController.getRefactoringInstanceAtCommit(gitX.getRepo(), commitId);
        Assert.assertEquals(2, refactoringResults.size());
        Assert.assertEquals("Change Return Type", refactoringResults.get(0).getRefactoringType().getDisplayName());
        Assert.assertEquals("Change Attribute Type", refactoringResults.get(1).getRefactoringType().getDisplayName());
    }
    @Test
    public void addMethodAnnotation(){
        String project = "TestEffortEstimationTutorial";
        String commitId = "3e4f5b450756de9ab9f7eced740d038c08d28277";
        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/x/");
        List<Refactoring> refactoringResults = RefactoringMinerController.getRefactoringInstanceAtCommit(gitX.getRepo(), commitId);
        Assert.assertEquals(1, refactoringResults.size());
        Assert.assertEquals("Add Method Annotation", refactoringResults.get(0).getRefactoringType().getDisplayName());
    }
    @Test@Ignore
    public void modifyMethodAnnotation(){
        String project = "TestEffortEstimationTutorial";
        String commitId = "8f70d79571af34d73002e59bccd7966a753bec0b";
        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/x/");
        List<Refactoring> refactoringResults = RefactoringMinerController.getRefactoringInstanceAtCommit(gitX.getRepo(), commitId);
        Assert.assertEquals(1, refactoringResults.size());
        Assert.assertEquals("Modify Method Annotation", refactoringResults.get(0).getRefactoringType().getDisplayName());
    }
    @Test
    public void testPullUp(){
        String project = "TestEffortEstimationTutorial";
        String commitId = "f0321192ad0cf5d5685e38fbab7aca5c81fb7f94";
        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/x/");
        List<Refactoring> refactoringResults = RefactoringMinerController.getRefactoringInstanceAtCommit(gitX.getRepo(), commitId);
        Assert.assertEquals(6, refactoringResults.size());
        Assert.assertEquals("Pull Up Method", refactoringResults.get(0).getRefactoringType().getDisplayName());
        Assert.assertEquals("Pull Up Method", refactoringResults.get(1).getRefactoringType().getDisplayName());
        Assert.assertEquals("Move Method", refactoringResults.get(2).getRefactoringType().getDisplayName());
        Assert.assertEquals("Move Method", refactoringResults.get(3).getRefactoringType().getDisplayName());

    }

    @Test
    public void show(){
        String project = "TestEffortEstimationTutorial";
        String commitId = "6ca307b1d233472c7bd10e5ae24130fbd1d3868f";
        SettingManager sm = new SettingManager(new String[]{project});
        GitController gitX = new GitController(sm, "/x/");
        List<Refactoring> refactoringResults = RefactoringMinerController.getRefactoringInstanceAtCommit(gitX.getRepo(), commitId);
        for (Refactoring r: refactoringResults){
            System.out.println(r.toString());
            System.out.println(r.leftSide().toString());
            System.out.println(r.rightSide().toString());
        }
    }

}
