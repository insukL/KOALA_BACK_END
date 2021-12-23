package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.annotation.ValidationGroups;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Memo extends BaseObject {
    private Long user_scrap_id;

    @Length(groups = {ValidationGroups.createMemo.class}, min=1, max=100, message = "메모는 1 ~ 100자까지 작성할 수 있습니다.")
    private String memo;

}
