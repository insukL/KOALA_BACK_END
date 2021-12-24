package in.koala.enums;

import java.util.Calendar;
import java.util.Date;

public enum TokenType {
    ACCESS(12), REFRESH(24*14), SNS(0);

    private final Date tokenExp;

    TokenType(int tokenRemainTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.add(Calendar.HOUR_OF_DAY, tokenRemainTime);

        tokenExp = calendar.getTime();
    }

    public Date getTokenExp() {
        return tokenExp;
    }
}
