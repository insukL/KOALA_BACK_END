package in.koala.domain.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.annotation.ValidationGroups;
import in.koala.enums.SnsType;
import in.koala.enums.UserType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class User {
    protected Long id;

    @ApiModelProperty(hidden = true)
    protected UserType user_type;
    @ApiModelProperty(hidden = true)
    protected Timestamp created_at;
    @ApiModelProperty(hidden = true)
    protected Timestamp updated_at;
}
