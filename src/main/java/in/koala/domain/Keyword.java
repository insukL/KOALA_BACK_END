package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private List<String> siteList;

    @JsonIgnore
    private short isImportant;

    @JsonIgnore
    private short alarmMode;

    @JsonIgnore
    private int alarmCycle;

    @ApiModelProperty(hidden = true)
    private Timestamp createdAt;

    @ApiModelProperty(hidden = true)
    private Timestamp updatedAt;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private short isDeleted;
}
