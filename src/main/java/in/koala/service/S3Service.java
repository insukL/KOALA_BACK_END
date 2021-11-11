package in.koala.service;

import com.amazonaws.AmazonServiceException;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

public interface S3Service {
    String uploadObject(MultipartFile multipartFile) throws IOException;
    void deleteObject(String date, String savedName, boolean isHard) throws AmazonServiceException;
    org.springframework.core.io.Resource getObject(String path, String savedName) throws IOException;
}
