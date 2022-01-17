package in.koala.exception;

import in.koala.enums.ErrorMessage;

public class HistoryException extends NonCriticalException {

    public HistoryException(String className, ErrorMessage errorMessage) {
        super(className, errorMessage);
    }
    public HistoryException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
