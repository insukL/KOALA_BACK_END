package in.koala.enums;

public enum TokenType {
    REFRESH(30), ACCESS(60);

    private final int tokenRemainTime;

    TokenType(int tokenRemainTime) {
        this.tokenRemainTime = tokenRemainTime;
    }
}
