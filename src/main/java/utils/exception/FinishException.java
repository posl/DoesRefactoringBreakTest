package utils.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This exception notify normal exit, which happens when no more instances remains to be analyzed
 */
public class FinishException extends Exception{
    @Override
    public String getMessage() {
        return "Appropriately finished";
    }

    @Override
    public void printStackTrace() {
        System.out.println();
        System.out.println("**************************");
        System.out.println("* Appropriately finished *");
        System.out.println("**************************");
    }

    @Override
    public void printStackTrace(PrintStream s) {
        this.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        this.printStackTrace();
    }
}
