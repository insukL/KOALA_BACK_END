package in.koala.mapper;

import in.koala.domain.ChatReport;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportMapper {
    int insertReport(ChatReport chatReport);
}
