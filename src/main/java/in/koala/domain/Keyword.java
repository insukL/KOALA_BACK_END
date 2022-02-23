package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.enums.CrawlingSite;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Keyword {

    //키워드 id
    @ApiModelProperty(hidden = true)
    private Long id;

    //유저 id
    @ApiModelProperty(hidden = true)
    private Long userId;

    //키워드 이름
    @NotBlank(message = "키워드 이름은 비워둘 수 없습니다.")
    private String name;

    //구독하는 사이트
    @NotNull(message = "구독하는 사이트는 비워둘 수 없습니다.")
    private List<CrawlingSite> siteList;

    //중요 알림 or 일반알림
    @NotNull(message = "알림의 중요도는 비워둘 수 없습니다.")
    @Min(value = 0, message = "0 또는 1이어야 합니다.")
    @Max(value = 1, message = "0 또는 1이어야 합니다.")
    private Short isImportant;

    //무음모드 알림
    @NotNull(message = "무음모드 실행 여부는 비워둘 수 없습니다.")
    @Min(value = 0, message = "0 또는 1이어야 합니다.")
    @Max(value = 1, message = "0 또는 1이어야 합니다.")
    private Short silentMode;

    //진동 알림
    @NotNull(message = "진동 알림 실행 여부는 비워둘 수 없습니다.")
    @Min(value = 0, message = "0 또는 1이어야 합니다.")
    @Max(value = 1, message = "0 또는 1이어야 합니다.")
    private Short vibrationMode;

    //확인버튼 누를 때 까지 알림
    @NotNull(message = "확인버튼 누를때까지 알림 실행 여부는 비워둘 수 없습니다.")
    @Min(value = 0, message = "0 또는 1이어야 합니다.")
    @Max(value = 1, message = "0 또는 1이어야 합니다.")
    private Short untilPressOkButton;

    //알림 주기
    @NotNull(message = "알림 주기는 비워둘 수 없습니다.")
    @Min(value = 5, message = "최소 알림 주기는 5분 입니다.")
    @Max(value = 360, message = "최대 알림 주기는 360시간 입니다.")
    private Integer alarmCycle;

    //읽지 않은 알림 숫자
    @ApiModelProperty(hidden = true)
    private Long noticeNum;

    //키워드 생성 시간
    @ApiModelProperty(hidden = true)
    private Timestamp createdAt;

    //키워드 업데이트 시간
    @ApiModelProperty(hidden = true)
    private Timestamp updatedAt;

    //키워드 삭제 여부
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private Short isDeleted;
}
