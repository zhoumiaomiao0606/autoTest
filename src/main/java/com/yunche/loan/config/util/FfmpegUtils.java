package com.yunche.loan.config.util;

import com.yunche.loan.web.aop.GlobalExceptionHandler;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class FfmpegUtils
{
    private static final Logger logger = LoggerFactory.getLogger(FfmpegUtils.class);

    private static final String FFMPEGPATH = "/Users/zhongmingxiao/Desktop/ffmpeg-4.0.2-macos64-shared/bin/ffmpeg";

    private static final String FFPROBEPATH = "/Users/zhongmingxiao/Desktop/ffmpeg-4.0.2-macos64-shared/bin/ffprobe";

    private static final Long SUITSIZE = 31_457_000L;

    private static final Long TARGETSIZE = 14_320_000L;


    //压缩任务启动
    public static boolean compress(String inPath,String outPath)
    {
        logger.info("压缩开始时间："+ LocalDateTime.now());

        try {
        FFmpeg ffmpeg = new FFmpeg(FFMPEGPATH);
        FFprobe ffprobe = new FFprobe(FFPROBEPATH);
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        FFmpegProbeResult in = ffprobe.probe(inPath);

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in) // Or filename
                .addOutput(outPath)
                .setTargetSize(TARGETSIZE)
                .done();

        FFmpegJob job = executor.createJob(builder, new ProgressListener() {

            // Using the FFmpegProbeResult determine the duration of the input
            final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {
                double percentage = progress.out_time_ns / duration_ns;

                // Print out interesting information about the progress
                System.out.println(String.format(
                        "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
                        percentage * 100,
                        progress.status,
                        progress.frame,
                        FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                        progress.fps.doubleValue(),
                        progress.speed
                ));
            }
        });

        //不是线程执行的。。。。。。。
        job.run();

        } catch (IOException e)
        {
            logger.error("ffmpeg服务压缩失败",e);
        }

        logger.info("压缩结束时间："+ LocalDateTime.now());

        return true;
    }

    //判断文件大小

    public static boolean needCompress(String inPath)
    {
        try {
            FFprobe ffprobe = new FFprobe(FFPROBEPATH);
            FFmpegProbeResult probeResult = ffprobe.probe(inPath);
            FFmpegFormat format = probeResult.getFormat();
            if (format.size >SUITSIZE)
            {
                return true;
            }

        } catch (IOException e) {
            logger.error("启动ffmpeg服务失败",e);
        }
        return false;
    }

}
