package in.koala.exception;

import in.koala.enums.ErrorMessage;

public class KeywordException extends NonCriticalException{

    public KeywordException(String className, ErrorMessage errorMessage) {
        super(className, errorMessage);
    }
    public KeywordException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
