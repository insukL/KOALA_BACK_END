package in.koala.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import in.koala.enums.ErrorMessage;
import in.koala.exception.NonCriticalException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.NotActiveException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {

    private final AmazonS3 amazonS3;

    @Value("${s3.bucket}")
    private String bucket;


    public String uploader(MultipartFile multipartFile) {

        String fileName = multipartFile.getOriginalFilename();

        int index = fileName.lastIndexOf(".");
        String fileExt = fileName.substring(index+1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String date = dateFormat.format(new Date());

        UUID uid = UUID.randomUUID();

        String savedName = uid.toString() + "-" + System.currentTimeMillis() + "." + fileExt;

        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType(multipartFile.getContentType());
        omd.setContentLength(multipartFile.getSize());

        try {
            amazonS3.putObject(new PutObjectRequest(bucket + "/" + date,
                    savedName, multipartFile.getInputStream(), omd)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e){
            throw new NonCriticalException(ErrorMessage.FILE_UPLOAD_FAIL);
        }
        return amazonS3.getUrl(bucket, date+"/"+savedName).toString();
        //return "https://" + "koala-s3.s3.ap-northeast-2.amazonaws.com" +"/" + date + "/" + savedName;
    }

    public void deleteFile(String url){
//        int index = url.lastIndexOf("/");
//        String file = url.substring(54);
        url.
        String fileName = url.substring(54);
        System.out.println(fileName);
        amazonS3.deleteObject(bucket, fileName);
    }
}
