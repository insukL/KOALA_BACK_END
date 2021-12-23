package in.koala.exception;

import in.koala.enums.ErrorMessage;

public class KeywordPushException extends CriticalException{

    public KeywordPushException(String className, ErrorMessage errorMessage) {
        super(className, errorMessage);
    }
    public KeywordPushException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
