package in.koala.domain;

import io.swagger.annotations.ApiParam;

public class Criteria {
    @ApiParam(required = false, defaultValue = "10")
    private Integer limit = 10;
    @ApiParam(required = false, defaultValue = "0")
    private Integer cursor;

    public Integer getLimit() { return limit; }

    public void setLimit(Integer limit) {
        this.limit = limit > 50 ? 50 : limit;
    }

    public Integer getCursor() {
        return cursor;
    }

    public void setCursor(Integer cursor){ this.cursor = cursor; }

    @Override
    public String toString() {
        return "Criteria{" +
                "cursor=" + cursor +
                ", limit=" + limit +
                '}';
    }
}
