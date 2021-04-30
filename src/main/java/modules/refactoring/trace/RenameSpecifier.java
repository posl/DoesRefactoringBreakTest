package modules.refactoring.trace;

import beans.source.MethodDefinition;
import gr.uom.java.xmi.diff.CodeRange;
import modules.source.structure.StructureAnalyzer;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import utils.log.MyLogger;

import java.util.List;

/**
 * Since we use two snapshots before and after changes, sometimes signature changes happen.
 * Thus, we need to link the two method name before and after the change.
 * We explicit refactoring miner that gives us the refactoring information including previous and new name of the method.
 */
public class RenameSpecifier {
    static MyLogger logger = MyLogger.getInstance();
    public static void linkMethods(StructureAnalyzer analyzerX_1, StructureAnalyzer analyzerX, List<Refactoring> refactoringInstances) {
        for (Refactoring refactoring : refactoringInstances) {
            RefactoringType type = refactoring.getRefactoringType();
            logger.trace("Rtype: " + type);
            List<CodeRange> left = refactoring.leftSide();
            List<CodeRange> right = refactoring.rightSide();
            switch (type) {
                //**************CLASS LEVEL************************************************************
                case RENAME_CLASS:
                case MOVE_CLASS:
                case RENAME_PACKAGE:
                case MOVE_SOURCE_FOLDER://未確認
                case MOVE_RENAME_CLASS:
                    //change class name
                    renameClass(analyzerX, analyzerX_1, left, right);
                    break;

                //**************METHOD LEVEL************************************************************
                case PULL_UP_OPERATION:
                case PUSH_DOWN_OPERATION:
                case MOVE_OPERATION:
                case RENAME_METHOD:
                case MOVE_AND_RENAME_OPERATION:
                    // change method name
                    signatureChange(analyzerX, analyzerX_1, left, right);
                    break;

                //**************Statement level*******************************************
                case EXTRACT_CLASS:
                case EXTRACT_OPERATION:
                    //https://github.com/apache/hadoop-hdfs/commit/5e964eec394a894271fa95540004ddfc401f896f
                    //       leftsrc/test/hdfs/org/apache/hadoop/hdfs/TestDFSRename.java#41
                    //       rightsrc/test/hdfs/org/apache/hadoop/hdfs/TestDFSRename.java#41//========ATTRIBUTE===========

                    //========ANNOTATION===========
                case ADD_CLASS_ANNOTATION:
                case REMOVE_CLASS_ANNOTATION:
                case MODIFY_CLASS_ANNOTATION:
                case ADD_METHOD_ANNOTATION:
                case MODIFY_METHOD_ANNOTATION:
                    //https://github.com/apache/hadoop-hdfs/commit/3b82ca2014925a64489da25016281d25c2e53d15
                    //       leftsrc/test/aop/org/apache/hadoop/hdfs/TestFiHFlush.java#74
                    //       rightsrc/test/aop/org/apache/hadoop/hdfs/TestFiHFlush.java#82
                case REMOVE_METHOD_ANNOTATION:
                case ADD_ATTRIBUTE_ANNOTATION:
                case REMOVE_ATTRIBUTE_ANNOTATION:
                case MODIFY_ATTRIBUTE_ANNOTATION:

                    //========ATTRIBUTE===========
                case CHANGE_ATTRIBUTE_TYPE:
                case EXTRACT_ATTRIBUTE:
                case MERGE_ATTRIBUTE:
                case MOVE_ATTRIBUTE:
                    //https://github.com/apache/hadoop-hdfs/commit/58cdf4f5ac219ab8b63a49e6fedbce691f365977
                    //       leftsrc/java/org/apache/hadoop/hdfs/server/namenode/INodeFile.java#33
                    //       rightsrc/test/unit/org/apache/hadoop/hdfs/server/namenode/TestINodeFile.java#35
                case MOVE_RENAME_ATTRIBUTE:
                case SPLIT_ATTRIBUTE:
                case RENAME_ATTRIBUTE:
                    //https://github.com/apache/hadoop-hdfs/commit/58cdf4f5ac219ab8b63a49e6fedbce691f365977
                    //       leftsrc/java/org/apache/hadoop/hdfs/server/namenode/INodeFile.java#32
                    //       rightsrc/java/org/apache/hadoop/hdfs/server/namenode/INodeFile.java#38
                case REPLACE_ATTRIBUTE:
                case PUSH_DOWN_ATTRIBUTE:
                case PULL_UP_ATTRIBUTE:
                    //========RETURN===========
                case CHANGE_RETURN_TYPE:
                    //nothing
                    break;
                    //========引数===========
                case ADD_PARAMETER:
                // c268516e64e2deebf570d8a8d6a9f376bfdcc40a
                    //https://github.com/apache/hadoop-hdfs/commit/cc07fe635f3ba5d7abf7c8f1953fc46ea2f19dc5
                    //       leftsrc/java/org/apache/hadoop/hdfs/protocol/LocatedBlocks.java#46
                    //       rightsrc/java/org/apache/hadoop/hdfs/protocol/LocatedBlocks.java#51
                case CHANGE_PARAMETER_TYPE:
                // c810627978b07289c8481d368f501de19c38f598
                case REMOVE_PARAMETER:
                // c716d5c44e7ef7e0d55ab388740a6af9ca8edf79
                case REORDER_PARAMETER:
                case SPLIT_PARAMETER:
                case MERGE_PARAMETER:
                case RENAME_PARAMETER:
                //34bde0e9547549b4720b9ac6b5c12170d58bb1c8
                    // parameter change
                    signatureChange(analyzerX, analyzerX_1, left, right);
                    break;


                    //_=========ローカル変数==================
                case REPLACE_VARIABLE_WITH_ATTRIBUTE:
                case CHANGE_VARIABLE_TYPE:
                    //https://github.com/apache/hadoop-hdfs/commit/cc07fe635f3ba5d7abf7c8f1953fc46ea2f19dc5
                    //       leftsrc/java/org/apache/hadoop/hdfs/server/namenode/FSNamesystem.java#716
                    //       rightsrc/java/org/apache/hadoop/hdfs/server/namenode/FSNamesystem.java#716
                case EXTRACT_VARIABLE:
                case PARAMETERIZE_VARIABLE:
                    //https://github.com/apache/hadoop-hdfs/commit/5e964eec394a894271fa95540004ddfc401f896f
                    //       leftsrc/test/hdfs/org/apache/hadoop/hdfs/TestDFSRename.java#49
                    //       rightsrc/test/hdfs/org/apache/hadoop/hdfs/TestDFSRename.java#41
                case RENAME_VARIABLE:
                    //https://github.com/apache/hadoop-hdfs/commit/cc07fe635f3ba5d7abf7c8f1953fc46ea2f19dc5
                    //       leftsrc/java/org/apache/hadoop/hdfs/server/namenode/BlockManager.java#367
                    //       rightsrc/java/org/apache/hadoop/hdfs/server/namenode/BlockManager.java#376
                case SPLIT_VARIABLE:
                case MERGE_VARIABLE:
                case INLINE_VARIABLE:
                    // nothing
                    break;

                case INTRODUCE_POLYMORPHISM:
                case CONVERT_ANONYMOUS_CLASS_TO_TYPE:
                case INLINE_OPERATION:
                case MOVE_AND_INLINE_OPERATION:
                case EXTRACT_AND_MOVE_OPERATION:
                case MERGE_OPERATION:
                case EXTRACT_SUBCLASS:
                case EXTRACT_SUPERCLASS:
                case EXTRACT_INTERFACE:
                //nothing
                default:
                    logger.error("    DEFAULT: " + type);
                    String leftPathName = left.get(0).getFilePath();
                    int leftNo = left.get(0).getStartLine();
                    logger.error("       left:" + leftPathName + "#" + leftNo);
                    String rightPathName = right.get(0).getFilePath();
                    int rightNo = right.get(0).getStartLine();
                    logger.error("       right:" + rightPathName + "#" + rightNo);
            }
        }
    }

    /**
     * rename class name in another signature
     * @param analyzerX
     * @param analyzerX_1
     * @param left
     * @param right
     */
    private static void renameClass(StructureAnalyzer analyzerX, StructureAnalyzer analyzerX_1, List<CodeRange> left, List<CodeRange> right) {
        assert (left.size() == 1);
        assert (right.size() == 1);
        String leftPathName = left.get(0).getFilePath();
        String rightPathName = right.get(0).getFilePath();
        renameAllSignature(analyzerX, rightPathName, leftPathName);
        renameAllSignature(analyzerX_1, leftPathName, rightPathName);
    }

    /**
     * change another names in commit X and commit X-1
     * @param analyzerX
     * @param analyzerX_1
     * @param left
     * @param right
     */
    private static void signatureChange(StructureAnalyzer analyzerX, StructureAnalyzer analyzerX_1, List<CodeRange> left, List<CodeRange> right) {
        assert (left.size() == 1);
        assert (right.size() == 1);
        String leftPathName = left.get(0).getFilePath();
        int leftNo = left.get(0).getStartLine();
        String rightPathName = right.get(0).getFilePath();
        int rightNo = right.get(0).getStartLine();
        MethodDefinition leftMethod = analyzerX_1.getMethod(leftPathName, leftNo);
        MethodDefinition rightMethod = analyzerX.getMethod(rightPathName, rightNo);
        rightMethod.anotherSignature = rightMethod.anotherSignature.replace(rightMethod.signature, leftMethod.signature);
        leftMethod.anotherSignature = leftMethod.anotherSignature.replace(leftMethod.signature, rightMethod.signature);
        analyzerX.updateMethod(rightMethod);
        analyzerX_1.updateMethod(leftMethod);
    }

    /**
     * change another name
     * @param analyzer
     * @param from
     * @param to
     */
    private static void renameAllSignature(StructureAnalyzer analyzer, String from, String to) {
        List<String> signatures = analyzer.getSignatures(from);
        for (String s : signatures) {
            MethodDefinition md = analyzer.getMethod(s);
            md.anotherSignature = md.anotherSignature.replace(from, to);
            analyzer.updateMethod(md);
        }
    }
}
