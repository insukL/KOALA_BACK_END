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

    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(hidden = true)
    private Long userId;

    private String name;
    private List<CrawlingSite> siteList;

    private Short isImportant;
    private Short alarmMode;
    private int alarmCycle;

    @ApiModelProperty(hidden = true)
    private Long noticeNum;

    @ApiModelProperty(hidden = true)
    private Timestamp createdAt;

    @ApiModelProperty(hidden = true)
    private Timestamp updatedAt;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private Short isDeleted;

    public Keyword(String name, List<CrawlingSite> siteList){
        this.name = name;
        this.siteList = siteList;
    }

    public Keyword(Long id, Long userId, String name, List<CrawlingSite> siteList, Short isImportant, Short alarmMode, int alarmCycle, Long noticeNum, Timestamp createdAt, Timestamp updatedAt, Short isDeleted) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.siteList = siteList;
        this.isImportant = isImportant;
        this.alarmMode = alarmMode;
        this.alarmCycle = alarmCycle;
        this.noticeNum = noticeNum;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }
}
