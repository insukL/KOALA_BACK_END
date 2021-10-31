package in.koala.enums;

import java.util.Calendar;

public enum TokenType {
    REFRESH(void setClendar(){

    }), ACCESS(60);

    private final Calendar tokenRemainTime;

    TokenType(int tokenRemainTime) {
        this.tokenRemainTime = tokenRemainTime;
    }
}
