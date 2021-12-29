package in.koala.enums;

import lombok.Getter;

@Getter
public enum EmailType {
    ACCOUNT(0), PASSWORD(1), UNIVERSITY(2);

    private final int emailType;

    EmailType(int emailType){
        this.emailType = emailType;
    }

}
