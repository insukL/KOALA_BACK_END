package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.annotation.ValidationGroups;
import in.koala.enums.CrawlingSite;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrawlingToken {

    private Long id;

    @Length(message="설명은 1 ~ 200자 사이여야 합니다", min=1, max=200, groups = {ValidationGroups.createCrawlingToken.class, ValidationGroups.updateCrawlingToken.class})
    private String description;

    private CrawlingSite site;

    @Length(message="토큰은 1 ~ 300자 사이여야 합니다", min=1, max=300, groups = {ValidationGroups.createCrawlingToken.class, ValidationGroups.updateCrawlingToken.class})
    private String token;
}
