package in.koala.enums;

public enum ChatType {
    CHAT(1), IMAGE(2), ENTER(3), EXIT(4);

    Integer code;
    ChatType(Integer code){ this.code = code; }
    public Integer getCode(){return code;}
}
