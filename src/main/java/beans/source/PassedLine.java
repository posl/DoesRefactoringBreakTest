package beans.source;

public class PassedLine {
    /**
     * method signature
     */
    public String signature;
    /**
     * line no
     */
    public String lineNo;
    /**
     * refactoring type
     */
    public String type;

    public PassedLine(String s1, String lineNo, String type) {
        assert (s1 != null);
        this.signature = s1;
        this.lineNo = lineNo;
        this.type=type;
    }



    public String toString() {
        return signature + "@" + lineNo;
    }
}
