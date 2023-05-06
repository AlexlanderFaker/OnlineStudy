package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、任务开发：在Spring Bean实例中，开发Job方法；
 * 2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 * 4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Slf4j
@Component
public class VideoTaskJob {
    private static Logger logger = LoggerFactory.getLogger(VideoTaskJob.class);


    @Autowired
    MediaFileProcessService mediaFileProcessService;

    @Autowired
    MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;

    /**
     * 2、分片广播任务
     */
    @XxlJob("videoJobHandler")
    public void shardingJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        //获取cpu核心数
        int i = Runtime.getRuntime().availableProcessors();
        //查询待处理的任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, i);
        //任务数量
        int size = mediaProcessList.size();
        log.debug("取到的视频处理任务数:{}", size);
        if (size <= 0) {
            return;
        }
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        CountDownLatch countDownLatch = new CountDownLatch(size);
        //开启任务
        mediaProcessList.forEach(mediaProcess -> {
            //将任务加入线程池
            executorService.execute(() -> {
                try {
                    //任务执行逻辑
                    Long id = mediaProcess.getId();
                    String fileId = mediaProcess.getFileId();
                    //开启任务
                    boolean b = mediaFileProcessService.startTask(id);
                    if (!b) {
                        log.debug("任务抢占资源失败，任务id{}", id);
                        return;
                    }
                    //执行视频转码
                    File file = mediaFileService.downloadFileFromMinIO(mediaProcess.getBucket(), mediaProcess.getFilePath());
                    if (file == null) {
                        log.debug("任务下载视频出错，任务id{},bucket:{},objectName:{}",
                                id, mediaProcess.getBucket(), mediaProcess.getFilePath());
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", fileId, null, "下载视频到本地失败");
                    }

                    String video_path = file.getAbsolutePath();
                    //转换后mp4文件的名称

                    String mp4_name = fileId + ".mp4";
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.debug("创建临时文件异常,{}", e.getMessage());
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", fileId, null, "创建临时文件异常");
                    }
                    String mp4_path = mp4File.getAbsolutePath();
                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath, video_path, mp4_name, mp4_path);
                    //开始视频转换，成功将返回success
                    String result = videoUtil.generateMp4();
                    if (!result.equals("success")) {
                        log.debug("视频转码失败,失败原因:{},bucket:{},objectName:{}", result, mediaProcess.getBucket(), mediaProcess.getFilePath());
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", fileId, null, result);
                        return;
                    }
                    //转码后的视频保存到minio
                    boolean b2 = mediaFileService.addMediaFilesToMinIO(mp4File.getAbsolutePath(), "video/mp4", mediaProcess.getBucket(), mediaProcess.getFilePath());
                    if (!b2) {
                        log.debug("上传MP4视频到minio失败,taskid{}", id);
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", fileId, null, result);
                        return;
                    }
                    String url = getFilePath(fileId, ".mp4");

                    mediaFileProcessService.saveProcessFinishStatus(id, "2", fileId, url, "保存任务成功");
                    //保存任务处理结果
                } finally {
                    countDownLatch.countDown();
                }
            });
        });
        //指定一个最大限度等待时间
        countDownLatch.await(30, TimeUnit.MINUTES);

    }

    private String getFilePath(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

}
