package in.koala.exception;

import in.koala.enums.ErrorMessage;

public class CrawlingException extends CriticalException{

    public CrawlingException(String className, ErrorMessage errorMessage) {
        super(className, errorMessage);
    }
    public CrawlingException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
