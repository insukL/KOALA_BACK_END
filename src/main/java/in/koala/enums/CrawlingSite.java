package in.koala.enums;

public enum CrawlingSite {
    PORTAL("'아우누리'"),
    DORM("'아우미르'"),
    YOUTUBE("'YouTube'"),
    FACEBOOK("'FaceBook'"),
    INSTAGRAM("'Instagram'");

    String name;
    CrawlingSite(String name){ this.name = name; }
    public String getName(){ return name; }
}
