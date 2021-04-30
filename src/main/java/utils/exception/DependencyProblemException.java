package utils.exception;

/**
 * This exception happens when the production code cannot maven import
 */
public class DependencyProblemException extends TeaException{

	public DependencyProblemException(String errors){
		super(errors);
	}

}
