package in.koala.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import javax.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class CustomBody {

    private Object body;
    private Integer code;

    private static CustomBody responseBody = null;

    public static CustomBody of(HttpStatus  code){
        return of(null, code);
    }

    public static CustomBody of(@Nullable Object body, HttpStatus code){
        CustomBody responseBody = new CustomBody();

        responseBody.body = body;
        responseBody.code = code.value();

        return responseBody;
    }
}
