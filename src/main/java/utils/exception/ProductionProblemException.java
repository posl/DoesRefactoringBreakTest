package utils.exception;

/**
 * This exception happens when the production code have any problems
 */
public class ProductionProblemException extends TeaException{
	public ProductionProblemException(String errors){
		super(errors);
	}

}
