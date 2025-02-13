package in.koala.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    @Value("${s3.access-key}")
    private String accessKey;

    @Value("${s3.secret-key}")
    private String secretKey;

    @Bean
    public BasicAWSCredentials AwsBasicAWSCredentials(){
        BasicAWSCredentials awsBasicAWSCredentials = new BasicAWSCredentials(accessKey,secretKey);
        return awsBasicAWSCredentials;
    }

    @Bean
    public AmazonS3 amazonS3(){
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(this.AwsBasicAWSCredentials()))
                .build();
        return  amazonS3;
    }
}
