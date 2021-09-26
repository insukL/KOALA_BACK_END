package in.koala.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@MapperScan("in.koala.mapper")
@Configuration
public class DatabaseConfig {

	@Bean
	public DataSourceTransactionManager mybatisTransactionManager(DataSource dataSource) {
	  return new DataSourceTransactionManager(dataSource);
	}

}
