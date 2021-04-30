package modules.build.compile;

import beans.source.MethodDefinition;
import org.apache.commons.io.FileUtils;
import utils.log.MyLogger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class removes test methods that has compiler errors.
 */
public class CompileErrorEliminator {
    static MyLogger logger = MyLogger.getInstance();
    public class Line{
        /**
         * the contents in the line
         */
        public String contents;
        /**
         * Whether this line should be deleted
         */
        public Boolean delete;
        Line(String contents){
            this.contents=contents;
            delete=false;
        }
    }
    Map<String, List<Line>> linesByFileName;
    public CompileErrorEliminator(List<CompileErrorFinder.ErrorMethod> methods){
        linesByFileName = new TreeMap<>();
        try {
            markLines(methods);
        } catch (IOException e) {
            logger.error(methods.toString());
            throw new AssertionError();
        }
    }

    public Map<String, List<Line>> deleteLines() throws IOException {
        for(String filename: linesByFileName.keySet()){
            List<Line> lines = linesByFileName.get(filename);
            lines.parallelStream().filter(s -> s.delete).forEach(s->s.contents="");
            List<String> updatedLines = lines.stream().map(l->l.contents).collect(Collectors.toList());
            FileUtils.writeLines(new File(filename), "UTF-8", updatedLines);
        }
        return linesByFileName;
    }

    /**
     * The lines of the methods that have compiler errors will be flagged to be deleted
     * @param methods
     * @throws IOException
     */
    private void markLines(List<CompileErrorFinder.ErrorMethod> methods) throws IOException {

        for(CompileErrorFinder.ErrorMethod em: methods){
            String fileName=em.getPath();
            List<Line> checker = linesByFileName.get(fileName);
            if(checker==null){//initialize
                File f = new File(fileName);
                List<String> lines = FileUtils.readLines(f);
                List<Line> c = new ArrayList<>();
                lines.forEach(s -> c.add(new Line(s)));
                checker=c;
            }
            //delete will be true when the test method has compiler errors
            MethodDefinition md = em.getDefinition();
            if(md==null){//outside of methods
                System.out.println(fileName);
                System.out.println(em.getLine()-1);
                checker.get(em.getLine()-1).delete=true;
            }else{
                //all the lines of the method will be deleted
                for(int i=md.start;i<=md.end;i++){
                    checker.get(i-1).delete=true;
                }
            }
            linesByFileName.put(fileName, checker);
        }
    }
}
