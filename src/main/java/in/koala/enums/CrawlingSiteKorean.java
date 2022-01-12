package in.koala.enums;

public enum CrawlingSiteKorean {
    PORTAL("아우누리"),
    DORM("아우미르"),
    YOUTUBE("한국기술교육대학교 유튜브"),
    FACEBOOK("페이스북"),
    INSTAGRAM("인스타그램");

    String siteName;
    CrawlingSiteKorean(String siteName){ this.siteName = siteName; }
    public String getSiteName(){ return siteName; }
}
