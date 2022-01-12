package in.koala.enums;

import lombok.Getter;

import java.util.Calendar;
import java.util.Date;

@Getter
public enum TokenType {
    ACCESS(Calendar.HOUR_OF_DAY, 12), REFRESH(Calendar.HOUR_OF_DAY, 24*14);

    private int calendar;
    private int tokenRemainTime;

    TokenType(int calendar, int tokenRemainTime) {
        this.calendar = calendar;
        this.tokenRemainTime = tokenRemainTime;
    }

}
