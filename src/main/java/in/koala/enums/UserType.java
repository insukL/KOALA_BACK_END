package in.koala.enums;

import in.koala.exception.NonCriticalException;

public enum UserType {
    NORMAL, NON;

    public static UserType getUserType(String userType){
        for(UserType u : UserType.values()){
            if(u.name().equals(userType)) return u;
        }

        throw new NonCriticalException(ErrorMessage.UNDEFINED_EXCEPTION);
    }
}
