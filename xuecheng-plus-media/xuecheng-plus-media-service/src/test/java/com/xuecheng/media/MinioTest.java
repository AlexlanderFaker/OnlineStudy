package com.xuecheng.media;

import com.alibaba.nacos.common.utils.IoUtils;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.ContentType;
import com.xuecheng.base.exception.XueChengPlusException;
import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;

import javax.print.attribute.standard.Media;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Mr.Tang
 * @version 1.0
 * @description
 * @date ${LocalDateTime.now()}
 */
public class MinioTest {

    String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    public void test_upload() throws Exception {
        ContentInfo extensionMatch = ContentInfoUtil
                .findExtensionMatch("D:\\IdeaProjects\\WIN_20230430_22_59_23_Pro.jpg");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }
        UploadObjectArgs testbucket = UploadObjectArgs.builder()
                .bucket("testbucket")
                .filename("D:\\IdeaProjects\\WIN_20230430_22_59_23_Pro.jpg")
                .object("test/01/02/WIN_20230430_22_59_23_Pro.jpg").contentType(mimeType).build();
        minioClient.uploadObject(testbucket);
    }
    @Test
    public void test_delete() throws Exception {
        RemoveObjectArgs build = RemoveObjectArgs
                .builder()
                .bucket("testbucket")
                .object("WIN_20230430_22_59_23_Pro.jpg").build();
        minioClient.removeObject(build);
    }
    @Test
    public void test_select() throws Exception {
        GetObjectArgs build = GetObjectArgs
                .builder()
                .bucket("testbucket")
                .object("test/01/02/WIN_20230430_22_59_23_Pro.jpg").build();
        FilterInputStream object = minioClient.getObject(build);
        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\桌面\\课程资料\\1.jpeg"));
        IoUtils.copy(object,fileOutputStream);
        String source_md5 = DigestUtils.md5DigestAsHex(object);
        String local_md5 = DigestUtils.md5DigestAsHex(new FileInputStream(new File("D:\\桌面\\课程资料\\1.jpeg")));
        if (source_md5.equals(local_md5)){
            System.out.println("文件下载成功");
        }else {
            XueChengPlusException.cast("失败了");
        }
    }
}
