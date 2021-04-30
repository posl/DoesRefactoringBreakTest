package utils.exception;

/**
 * This exception happens when anonymous errors happens
 */
public class TeaException extends Exception{
	String errors;

	public TeaException(String errors){
		this.errors = errors;
	}
	
	public String getErrors(){
		return errors;
	}

}