package in.koala.util.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class MultipartImage implements MultipartFile {
    private byte[] fileBytes;
    private String name;
    private String originalFilename;
    private String contentType;
    private boolean isEmpty;
    private long size;

    public MultipartImage(byte[] fileBytes, String name, String originalFilename, String contentType, long size) {
        this.fileBytes = fileBytes;
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.isEmpty = false;
        this.size = size;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return fileBytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileBytes);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(fileBytes);
    }
}
