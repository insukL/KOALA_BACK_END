package in.koala.exception;

import in.koala.enums.ErrorMessage;


public class NonCriticalException extends BaseException {

	public NonCriticalException(String className, ErrorMessage errorMessage) {
		super(className, errorMessage);
	}
	public NonCriticalException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
