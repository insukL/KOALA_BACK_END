package in.koala.enums;

public enum CrawlingSite {
    PORTAL(1), DORM(2), YOUTUBE(3), FACEBOOK(4), INSTAGRAM(5);

    Integer code;
    CrawlingSite(Integer code){ this.code = code; }
    public Integer getCode(){ return code; }
}
