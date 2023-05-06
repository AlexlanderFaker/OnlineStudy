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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * 测试上传文件
     * @throws Exception
     */
    @Test
    public void test_upload() throws Exception {
        ContentInfo extensionMatch = ContentInfoUtil
                .findExtensionMatch("D:\\IdeaProjects\\WIN_20230430_22_59_23_Pro.jpg");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        UploadObjectArgs testbucket = UploadObjectArgs.builder()
                .bucket("testbucket")
                .filename("D:\\IdeaProjects\\WIN_20230430_22_59_23_Pro.jpg")
                .object("test/01/02/WIN_20230430_22_59_23_Pro.jpg").contentType(mimeType).build();
        minioClient.uploadObject(testbucket);
    }

    /**
     * 测试从文件系统删除文件
     * @throws Exception
     */
    @Test
    public void test_delete() throws Exception {
        RemoveObjectArgs build = RemoveObjectArgs
                .builder()
                .bucket("testbucket")
                .object("WIN_20230430_22_59_23_Pro.jpg").build();
        minioClient.removeObject(build);
    }

    /**
     * 测试从文件系统查询文件
     * @throws Exception
     */
    @Test
    public void test_select() throws Exception {
        GetObjectArgs build = GetObjectArgs
                .builder()
                .bucket("testbucket")
                .object("test/01/02/WIN_20230430_22_59_23_Pro.jpg").build();
        FilterInputStream object = minioClient.getObject(build);
        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\桌面\\课程资料\\1.jpeg"));
        IoUtils.copy(object, fileOutputStream);
        String source_md5 = DigestUtils.md5DigestAsHex(object);
        String local_md5 = DigestUtils.md5DigestAsHex(new FileInputStream(new File("D:\\桌面\\课程资料\\1.jpeg")));
        if (source_md5.equals(local_md5)) {
            System.out.println("文件下载成功");
        } else {
            XueChengPlusException.cast("失败了");
        }
    }

    /**
     * 测试上传本地分块文件到文件系统
     * @throws IOException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws InsufficientDataException
     * @throws NoSuchAlgorithmException
     * @throws ServerException
     * @throws InternalException
     * @throws XmlParserException
     * @throws ErrorResponseException
     */
    @Test
    public void uploadChunk() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        for (int i = 0; i < 9; i++) {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .filename("D:\\IdeaProjects\\testFile\\chunk\\" + i)
                    .object("chunk/" + i)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("分块" + i + "上传成功");
        }
    }

    /**
     * 测试合并文件在分布式文件系统上
     * @throws IOException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws InsufficientDataException
     * @throws NoSuchAlgorithmException
     * @throws ServerException
     * @throws InternalException
     * @throws XmlParserException
     * @throws ErrorResponseException
     */
    @Test
    public void testMerge() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
//        List<ComposeSource> sources = null;
//        for (int i = 0; i < 9; i++) {
//            ComposeSource composeSource = ComposeSource.builder().bucket("testbucket").object("chunk" + i).build();
//            sources.add(composeSource);
//        }
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i).limit(9)
                .map(i -> ComposeSource.builder().bucket("testbucket")
                        .object("chunk/" + i).build()).collect(Collectors.toList());

        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .sources(sources)
                .bucket("testbucket")
                .object("merge01.avi")
                .build();
        minioClient.composeObject(composeObjectArgs);
    }
}
