package in.koala.enums;

public enum CrawlingSite {
    PORTAL(1, "'아우누리'"),
    DORM(2,"'아우미르'"),
    YOUTUBE(3,"'YouTube'"),
    FACEBOOK(4,"'FaceBook'"),
    INSTAGRAM(5,"'Instagram'");

    Integer code;
    String name;
    CrawlingSite(int code, String name){
        this.code = code; this.name = name;
    }
    public String getName(){ return name; }
    public Integer getCode(){ return code; }
}
