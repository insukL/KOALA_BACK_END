package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BanWord {
    private Long id;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @Length(min=1, max=20, message = "금칙어는 1 ~ 20자 사이여야 합니다")
    private String word;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private Short isDeleted;
}
