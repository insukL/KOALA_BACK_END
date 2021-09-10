package in.koala.exception;

import in.koala.enums.ErrorMessage;


public class CriticalException extends BaseException {

	public CriticalException(String className, ErrorMessage errorMessage) {
		super(className, errorMessage);
	}
	public CriticalException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
