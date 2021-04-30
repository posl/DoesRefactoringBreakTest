package utils.exception;

/**
 * This exception happens the analyzed commit does not have the parent (i.e., the first commit in the repository)
 */
public class NoParentsException extends Exception{
	private static final long serialVersionUID = 1L;

	public NoParentsException(){
		//System.err.println("getParentException");
	}
	
}