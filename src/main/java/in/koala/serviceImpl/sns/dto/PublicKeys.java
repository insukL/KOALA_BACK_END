package in.koala.serviceImpl.sns.dto;

import in.koala.serviceImpl.sns.dto.Key;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PublicKeys {
    private List<Key> keys;
}
