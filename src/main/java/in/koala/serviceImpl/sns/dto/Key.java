package in.koala.serviceImpl.sns.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Key {
    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;
}
