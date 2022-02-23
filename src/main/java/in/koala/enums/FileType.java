package in.koala.enums;

public enum FileType {
    PROFILE{
        @Override
        public String getUri(){return "";}

    },
    CHAT{
        @Override
        public String getUri(){return "chat/";}
    };
    public abstract String getUri();
}
