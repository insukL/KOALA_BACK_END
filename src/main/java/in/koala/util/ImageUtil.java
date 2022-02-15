package in.koala.util;

import in.koala.enums.ErrorMessage;
import in.koala.exception.NonCriticalException;
import in.koala.util.image.MultipartImage;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.Buffer;

@Component
public class ImageUtil {

    public MultipartFile resizing(MultipartFile multipartFIle, int newWidth) {
        byte[] imgBytes = null;

        try {
            BufferedImage inputImage = ImageIO.read(multipartFIle.getInputStream());

            int originalWidth = inputImage.getWidth();
            int originalHeight = inputImage.getHeight();

            int newHeight = (originalHeight * newWidth) / originalWidth;

            if(originalWidth < newWidth){
                newWidth = originalWidth;
                newHeight = originalHeight;
            }

            Image resizeImage = inputImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_BGR);

            Graphics graphics = newImage.getGraphics();
            graphics.drawImage(resizeImage, 0, 0, null);
            graphics.dispose();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(newImage, "jpg", out);
            imgBytes = out.toByteArray();

        } catch (Exception e){
            e.printStackTrace();
            throw new NonCriticalException(ErrorMessage.IMAGE_RESIZING_EXCEPTION);
        }

        return new MultipartImage(imgBytes, multipartFIle.getName(), multipartFIle.getOriginalFilename(), "jpg", imgBytes.length);
    }

}

