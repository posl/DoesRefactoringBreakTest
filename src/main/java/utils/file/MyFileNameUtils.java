package utils.file;

public class MyFileNameUtils {
    /**
     * To fill ??? in the setting file.
     * @param placement
     * @param id
     * @param exp
     * @return
     */
    public static String setPlacement(String placement, String id, String exp) {
        return placement.replace(exp, id);
    }

    public static String setPlacement(String placement, String id) {
        return MyFileNameUtils.setPlacement(placement, id, "???");
    }

    /**
     * delete redundant directory's slash (/)
     * @param path
     * @return
     */
    public static String getDirectoryName(String path) {
        return path.replaceAll("/$", "");
    }

    public String transform4windows(String batFilePath) {
        return batFilePath.replaceAll("/", "\\\\");
    }

}
