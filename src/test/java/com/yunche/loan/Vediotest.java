package com.yunche.loan;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.model.OSSObject;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.ZhongAnHttpUtil;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.ZhongAnCusParam;
import com.yunche.loan.domain.param.ZhongAnQueryParam;
import com.yunche.loan.domain.vo.BankCodeVO;
import com.yunche.loan.domain.vo.FileVO;
import com.yunche.loan.domain.vo.SDCOrders;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.junit.Test;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-20 14:34
 * @description: 视频压缩
 **/

public class Vediotest
{
    @Test
    public void osstest(){
        /*//根据路径从oss上获取文件输入流---进行base64编码
        OSSObject ossObject = OSSUnit.getObject(OSSUnit.getOSSClient(), "img/2018/201808/20180824/YNiiRpcyhf.jpg");
        StringBuilder objectContent = null;
        try {
            InputStream inputStream = ossObject.getObjectContent();
            objectContent = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                objectContent.append(line);
            }
            inputStream.close();

            System.out.println("====读取到的内容为"+objectContent.toString());
        }catch (Exception e){
            throw new BizException("读取oss文件失败");
        }*/

        /*String a = "上饶市";
        int i = a.indexOf("牛");
        String substring = a.substring(0, 2);
        String string = "2016-10-24 21:59:06";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");*/

        /*SDCOrders ss = new SDCOrders();
        ss.setOrderId(new Long("1901291036128970829"));
        ss.setPartnerId(new Long("56"));
        List<SDCOrders> sdOrders = Lists.newArrayList(ss);
        Long orderId = new Long("1901291036128970829");
        List<SDCOrders> collect = sdOrders
                .stream()
                //.filter(e -> !e.getOrderId().equals(orderId))
                .collect(Collectors.toList());

        System.out.println("====="+collect.size()+ JSON.toJSONString(collect));*/
        //System.out.println("====="+(null==2));

        //众安
       /* String param = "{\"order_id\":\"1901291042140362363\",\"customers\":[{\"name\":\"杨璐嘉\",\"tel\":\"13677347959\",\"idcard\":\"430521198412249220\",\"customertype\":\"特殊关联人\",\"ralationship\":\"1\",\"loanmoney\":\"1\"}]}";
        ZhongAnQueryParam zhongAnQueryParam = new ZhongAnQueryParam();

        Type type =new TypeToken<ZhongAnQueryParam>(){}  .getType();
        Gson gson = new Gson();
        zhongAnQueryParam = gson.fromJson(param, type);

        List<ZhongAnCusParam> customers = zhongAnQueryParam.getCustomers();


        //测试

        try {
            for (ZhongAnCusParam zhongAnCusParam : customers) {

                Random random = new Random();

                Map map = ZhongAnHttpUtil.queryInfo(zhongAnCusParam.getName(), zhongAnCusParam.getTel(), zhongAnCusParam.getIdcard(),
                        zhongAnQueryParam.getOrder_id(), zhongAnCusParam.getLoanmoney(), zhongAnCusParam.getCustomertype(), zhongAnCusParam.getRalationship(),
                        System.currentTimeMillis() + "" + random.nextInt(10000));

                System.out.println("map内容="+gson.toJson(map));



            }
        }catch (Exception ex)
        {
            System.out.println("====错误");
        }
*/

        /*FileVO fileVO = new FileVO();

        List<String> urls = Lists.newArrayList();

        fileVO.setUrls(urls);


        System.out.println(fileVO);*/


        /*List<BankCodeVO> list = Lists.newArrayList();
        List<BankCodeVO> list2 = Lists.newArrayList();
        BankCodeVO bankCodeVO = new BankCodeVO();
        list.add(bankCodeVO);
        bankCodeVO.setId(1);

        list
                .stream()
                .forEach(e ->{
                    e.setCode("3333");
                    list2.add(e);
                });
        System.out.println(list2);*/

    }

    @Test
    public void osstest2(){
        //根据路径从oss上获取文件输入流---进行base64编码
        InputStream inputStream =null;

        byte[] data = null;
        System.out.println("大小为");
        try {
            inputStream = OSSUnit.getOSS2InputStream("img/2018/201808/20180824/YNiiRpcyhf.jpg");

            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();

            // 对字节数组Base64编码

            BASE64Encoder encoder = new BASE64Encoder();

            String encode = encoder.encode(data);// 返回Base64编码过的字节数组字符串

            System.out.println("转化后大小"+encode.length());


            System.out.println("====读取到的内容为"+encode);
        }catch (Exception e){
            throw new BizException("读取oss文件失败");
        }
    }


    //基本设置参数及输出
    @Test
    public void ffmpeg() throws IOException {
        FFmpeg ffmpeg = new FFmpeg("/Users/zhongmingxiao/Desktop/ffmpeg-4.0.2-macos64-shared/bin/ffmpeg");
        FFprobe ffprobe = new FFprobe("/Users/zhongmingxiao/Desktop/ffmpeg-4.0.2-macos64-shared/bin/ffprobe");

        FFmpegBuilder builder = new FFmpegBuilder()

                .setInput("G:\\aaa.mp4")     // Filename, or a FFmpegProbeResult
                .overrideOutputFiles(true) // Override the output if it exists

                .addOutput("G:\\zmx.mp4")   // Filename for the destination
                .setFormat("mp4")        // Format is inferred from filename, or can be set
                .setTargetSize(400_000)  // Aim for a 250KB file

                .disableSubtitle()       // No subtiles

                .setAudioChannels(1)         // Mono audio
                .setAudioCodec("aac")        // using the aac codec
                .setAudioSampleRate(48_000)  // at 48KHz
                .setAudioBitRate(32768)      // at 32 kbit/s

                .setVideoCodec("libx264")     // Video using x264
                .setVideoFrameRate(24, 1)     // at 24 frames per second
                .setVideoResolution(640, 480) // at 640x480 resolution

                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

// Run a one-pass encode
        executor.createJob(builder).run();

// Or run a two-pass encode (which is better quality at the cost of being slower)
        executor.createTwoPassJob(builder).run();

    }

    @Test
    public  void ffmpeg_vedioInformation() throws IOException {
        FFprobe ffprobe = new FFprobe("/Users/zhongmingxiao/Desktop/ffmpeg-4.0.2-macos64-shared/bin/ffprobe");
        FFmpegProbeResult probeResult = ffprobe.probe("/Users/zhongmingxiao/Downloads/yc.mp4");

        FFmpegFormat format = probeResult.getFormat();
        System.out.format("%nFile: '%s' ; Format: '%s' ; Duration: %.3fs ;大小：%s",
                format.filename,
                format.format_long_name,
                format.duration,
                format.size
        );

        FFmpegStream stream = probeResult.getStreams().get(0);
        System.out.format("%nCodec: '%s' ; Width: %dpx ; Height: %dpx",
                stream.codec_long_name,
                stream.width,
                stream.height
        );
    }


    @Test
    public void get_progress_while_encoding() throws IOException {
        FFmpeg ffmpeg = new FFmpeg("/Users/zhongmingxiao/Desktop/ffmpeg-4.0.2-macos64-shared/bin/ffmpeg");
        FFprobe ffprobe = new FFprobe("/Users/zhongmingxiao/Desktop/ffmpeg-4.0.2-macos64-shared/bin/ffprobe");
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        FFmpegProbeResult in = ffprobe.probe("/Users/zhongmingxiao/Downloads/90_穆志义_522427198709113830_25.716453_112.739954_20180911165316.mp4");

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in) // Or filename
                .addOutput("/Users/zhongmingxiao/Downloads/90_穆志义_522427198709113830_25.716453_112.739954_20180911165316_c.mp4")
                .setTargetSize(14_320_000)
                .setVideoResolution(320, 240)
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
    }
}
