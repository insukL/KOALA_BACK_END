package in.koala.domain.sns.AppleLogin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplePublicKeys {
    private List<Key> keys;
}
