package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.enums.CrawlingSite;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Keyword {

    //키워드 id
    @ApiModelProperty(hidden = true)
    private Long id;

    //유저 id
    @ApiModelProperty(hidden = true)
    private Long userId;

    //키워드 이름
    private String name;

    //구독하는 사이트
    private List<CrawlingSite> siteList;

    //중요 알림 or 일반알림
    private Short isImportant;

    //무음모드 알림
    private Short silentMode;

    //진동 알림
    private Short vibrationMode;

    //확인버튼 누를 때 까지 알림
    private Short untilPressOkButton;

    //알림 주기
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
