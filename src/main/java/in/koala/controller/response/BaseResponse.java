package in.koala.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import javax.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class BaseResponse<T> {

    private T body;
    private Integer code;

    public static BaseResponse of(HttpStatus  code){
        return of(null, code);
    }

    public static <T> BaseResponse of(@Nullable T body, HttpStatus code){
        BaseResponse responseBody = new BaseResponse();

        responseBody.body = body;
        responseBody.code = code.value();

        return responseBody;
    }
}
