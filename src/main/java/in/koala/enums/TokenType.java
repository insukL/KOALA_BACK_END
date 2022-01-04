package in.koala.enums;

import java.util.Calendar;
import java.util.Date;

public enum TokenType {
    ACCESS(12), REFRESH(24*14), SNS(0);

    private int tokenRemainTime;

    TokenType(int tokenRemainTime) {
        this.tokenRemainTime = tokenRemainTime;
    }

    public int getTokenRemainTime() {
        return tokenRemainTime;
    }
}
