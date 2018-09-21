package com.yunche.loan.config.util;

import com.yunche.loan.web.aop.GlobalExceptionHandler;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

public class FfmpegUtils
{
    private static final Logger logger = LoggerFactory.getLogger(FfmpegUtils.class);

    private static final String ffmpegPath = "/Users/zhongmingxiao/Desktop/ffmpeg-4.0.2-macos64-shared/bin/ffmpeg";

    private static final String ffprobePath = "/Users/zhongmingxiao/Desktop/ffmpeg-4.0.2-macos64-shared/bin/ffprobe";

    private static  FFmpegExecutor executor;


    //获取默认构建器---设置参数使用
    public static FFmpegBuilder getDefaultFFmpegBuilder(String inPath,String outPath)
    {
        return new FFmpegBuilder();
    }

    //压缩任务启动
    public static boolean compress(FFmpegJob fFmpegJob)
    {
        logger.info("压缩开始时间："+ LocalDateTime.now());

        fFmpegJob.run();

        logger.info("压缩结束时间："+ LocalDateTime.now());

        return true;
    }
}
