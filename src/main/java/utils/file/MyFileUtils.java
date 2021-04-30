package utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MyFileUtils {
    /** Write out */
    private static void dumpFile(File file, boolean includeDirectory, String ignore, List<String> set){
        // get a list of files
        File[] files = file.listFiles();
        if(files == null){
            return;
        }
        for (File tmpFile : files) {
            if(tmpFile.isDirectory()){// if directory
                if(ignore.equals(tmpFile.getPath())){
                    continue;
                }
                if(includeDirectory){
                    set.add(tmpFile.getPath());
                }
                dumpFile(tmpFile,includeDirectory, ignore, set);
            }else{// if file
                set.add(tmpFile.getPath());
            }
        }
    }
    public static List<String> listFiles(File file, boolean includeDirectory, String ignore){
        List set = new ArrayList<>();

        dumpFile(file, includeDirectory, ignore, set);
        return set;
    }

    public static void deleteDirectory(String dir) throws IOException {
        Path pathToBeDeleted = Paths.get(dir);
        Files.walk(pathToBeDeleted)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
//        FileUtils.deleteDirectory(new File(dir));
    }

    public static void deleteDirectory(String dir, boolean ignoreException) {
        if(ignoreException){
            try {
                deleteDirectory(dir);
            } catch (IOException e) {
            }
        }else{
            try {
                deleteDirectory(dir);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }
}
