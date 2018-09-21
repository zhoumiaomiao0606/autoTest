package com.yunche.loan.web.controller.chart;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CreditApplyCustomerByMonthChartParam;
import com.yunche.loan.domain.param.LoanApplyOrdersByMonthChartParam;
import com.yunche.loan.domain.param.OrdersSuccessByMonthChartParam;
import com.yunche.loan.service.ParterChartService;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-10 10:04
 * @description: 合伙人报表总计
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/app/loanorder/chart/parter")
public class ParterChartController
{
    @Autowired
    private ParterChartService parterChartService;


    /** 
    * @Author: ZhongMingxiao 
    * @Param:
    * @return:  
    * @Date:  
    * @Description:  当月提交了征信查询的订单客户
    */
    @PostMapping(value = "/creditApplyCustomerByMonthChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean creditApplyCustomerByMonthChart(@RequestBody @Validated CreditApplyCustomerByMonthChartParam param)
    {
        return parterChartService.getCreditApplyCustomerByMonthChart(param);
    }
    
    
    /** 
    * @Author: ZhongMingxiao 
    * @Param:
    * @return:  
    * @Date:  
    * @Description:  按月为单位，提交了贷款申请单的订单
    */
    @PostMapping(value = "/loanApplyOrdersByMonthChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean loanApplyOrdersByMonthChart(@RequestBody @Validated LoanApplyOrdersByMonthChartParam param)
    {
        return parterChartService.getLoanApplyOrdersByMonthChart(param);
    }

    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  征信未通过、风控未通过、银行未通过，征信未通过
    */
    @PostMapping(value = "/ordersSuccessByMonthChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean loanApplyOrdersByMonthRemitDetailChart(@RequestBody @Validated OrdersSuccessByMonthChartParam param)
    {
        return parterChartService.getOrdersSuccessByMonthChart(param);
    }
    
    /** 
    * @Author: ZhongMingxiao 
    * @Param:
    * @return:  
    * @Date:  
    * @Description:  当月提交了贷款申请的订单，修改订单信息的，以最后一次订单信息为准
    */




    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  当月提交了垫款申请的订单
    */


    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  当月垫款成功的订单（排除退款订单）
    */



   /* @PostMapping(value = "/creditApplyCustomerByMonthChart", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean creditApplyCustomerByMonthChart(@RequestBody @Validated CreditApplyCustomerByMonthChartParam param)
    {
        try {
            get_progress_while_encoding();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultBean.ofSuccess("成功");
    }

    public void get_progress_while_encoding() throws IOException {
        FFmpeg ffmpeg = new FFmpeg("/usr/local/ffmpeg-4.0.2-64bit-static/ffmpeg");
        FFprobe ffprobe = new FFprobe("/usr/local/ffmpeg-4.0.2-64bit-static/ffprobe");
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        FFmpegProbeResult in = ffprobe.probe("/usr/local/yc.mp4");

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in) // Or filename
                .addOutput("/usr/local/yc1.mp4")
                .setTargetSize(10_720_000)
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
        System.err.println("压缩开始时间："+ LocalDateTime.now());

        job.run();

        System.err.println("压缩结束时间："+ LocalDateTime.now());
    }*/


}
