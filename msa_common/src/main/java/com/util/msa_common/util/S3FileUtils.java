package com.per.msa_common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

@Component
public class S3FileUtils {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 파일 업로드
     * 
     * @param fileMap
     * @return
     * @throws Exception
     */
    public String upload(byte[] fileData, String fileName, String contentType) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(fileData);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(contentType);
        meta.setContentLength(fileData.length);
        System.out.println(bucket);
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, bis, meta)
                        .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    /**
     * 파일 다운로드
     * 
     * @param fileName
     * @return
     * @throws Exception
     */
    public byte[] download(String fileName) throws Exception {
        log.info("download s3 : " + fileName);
        byte[] fileArray = null;

        S3Object fileObj = amazonS3Client.getObject(bucket, fileName);
        S3ObjectInputStream s3is = fileObj.getObjectContent();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] read_buf = new byte[1024];
        int read_len = 0;
        while ((read_len = s3is.read(read_buf)) > 0) {
            bao.write(read_buf, 0, read_len);
        }

        fileArray = bao.toByteArray();
        bao.close();
        s3is.close();

        return fileArray;
    }

    /**
     * 파일 삭제
     * 
     * @param fileName
     */
    public void delete(String fileName) {
        log.info("delete s3 : " + fileName);
        try {
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 파일 이동
     * 
     * @param oldName
     * @param newName
     */
    public void rename(String oldName, String newName) {
        amazonS3Client.copyObject(bucket, oldName, bucket, newName);
        amazonS3Client.deleteObject(bucket, oldName);
    }
}
