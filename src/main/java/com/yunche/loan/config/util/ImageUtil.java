package com.yunche.loan.config.util;

import com.aliyun.oss.OSSClient;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.rtf.RtfWriter2;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ImageUtil {

    private  static String downLoadBasepath="/tmp";

    private static String IMAGEMAGICK_CLASSPATH="/usr/local/imagemagick/bin/";

    public static final String PIC_SUFFIX=".jpg";
    public static final String DOC_SUFFIX=".doc";

    private static final int DEFAULT_WIDTH=5000;
    private static final Logger LOG = LoggerFactory.getLogger(ImageUtil.class);
    private  static  String videoBucketName;
    public static  final String ZIP_SUFFIX = ".zip";
    public static  final String MP4_SUFFIX = ".mp4";
    private static  final String FORMATNAME="jpg";
    static {
        ResourceBundle bundle = PropertyResourceBundle.getBundle("oss");
        downLoadBasepath = bundle.containsKey("downLoadBasepath") == false ? "" : bundle.getString("downLoadBasepath");
        videoBucketName = bundle.containsKey("videoBucketName") == false ? "" : bundle.getString("videoBucketName");
    }
    public static final String  mergeImage2Doc(List<String> imageList) {
        return mergeImage2Doc(generateName()+DOC_SUFFIX,imageList);
    }

    public static final  String  mergeImage2Pic(List<String> imageList) {
        return mergeImage2Pic(generateName()+PIC_SUFFIX,imageList);
    }

    public static final void mergetImage2PicByConvert(String localPath,String name,List<String> imageList){
        List<String> fileList= Lists.newLinkedList();
        if(CollectionUtils.isEmpty(imageList)){
            return;
        }
        String tmpName =GeneratorIDUtil.execute()+IDict.K_SUFFIX.K_SUFFIX_JPG;
        try {
            //创建文件对象
          fileList = imageList.stream().map(pic -> {
                File file=null;
                try {
                    file = new File(localPath +"DEL"+GeneratorIDUtil.execute() + IDict.K_SUFFIX.K_SUFFIX_JPG);
                    InputStream oss2InputStream = OSSUnit.getOSS2InputStream(pic);
                    FileUtils.copyInputStreamToFile(oss2InputStream, file);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return localPath+file.getName();
            }).collect(Collectors.toList());



            RuntimeUtils.delete(localPath+name);
            IMOperation operation = new IMOperation();
            operation.addRawArgs("convert");
            operation.append();
//            operation.sample();
//            operation.addRawArgs("50%x50%");//质量大小缩小一半
            operation.resize();
            operation.addRawArgs("1024x768");//指定图片大小

            operation.quality();//设置图片大小
            operation.addRawArgs("100%");

            for(int i=0;i<fileList.size();i++){
                operation.addImage(fileList.get(i));
            }
            operation.addImage(localPath+tmpName);
            ConvertCmd cmd = new ConvertCmd();
            cmd.setSearchPath(IMAGEMAGICK_CLASSPATH);
            LOG.info("convert 合成图片开始【"+localPath+name+"】"+operation.toString());

             cmd.run(operation);

            LOG.info("convert 合成图片结束【"+localPath+name+"】");
            FileUtils.copyFile(new File(localPath+tmpName),new File(localPath+name));
        } catch (IOException e) {
            throw new BizException(e);
        } catch (InterruptedException e) {
            throw new BizException(e);
        } catch (IM4JavaException e) {
            throw new BizException(e);
        }finally {
            fileList.stream().forEach(e->{
                RuntimeUtils.delete(e);
                RuntimeUtils.delete(localPath+tmpName);
            });

        }


    }
    // org.im4java.core.CommandException: org.im4java.core.CommandException: convert: no images defined `' @ error/convert.c/ConvertImageCommand/3288.
        /**
         * 图片合并成jpg
         * @param imageList
         */
    public static final  String  mergeImage2Pic(String name,List<String> imageList){

        String generateName = generateName()+PIC_SUFFIX;

        FileOutputStream out = null;
        String fileName=null;//临时文件名
        try{
            //创建文件对象
            List<Image> images = imageList.stream().map(pic->{
                Image src =null;
                try {
                     src = ImageIO.read(OSSUnit.getOSS2InputStream(pic));
                } catch (IOException e) {
                    Preconditions.checkArgument(false,"读图片异常");
                }
                return src;
            }).collect(Collectors.toList());
            //获取待合并图片中最大宽度 & 图片总高度之和
            int maxWidth = 0;
            int maxHeight = 0;
            int totalHeight = 0;

            for(int i=0;i<images.size();i++){

                int width = images.get(i).getWidth(null);

                int height = images.get(i).getHeight(null);

                Double rate = (double) DEFAULT_WIDTH / (double)width;
                if(rate.intValue()<=0){
                    rate =1.0;
                }
                int rateHeight = rate.intValue()*height;
                if(width>maxWidth){
                    maxWidth = width;
                }
                if(height>maxHeight){
                    maxHeight = height;
                }
                totalHeight += rateHeight;
            }
            //构造一个类型为预定义图像类型之一的 BufferedImage。 高度为各个图片高度之和
            BufferedImage tag = new BufferedImage(DEFAULT_WIDTH, totalHeight, BufferedImage.TYPE_INT_RGB);
            //创建输出流

            fileName = downLoadBasepath+File.separator+generateName;
            out = new FileOutputStream(fileName);
            //绘制合成图像
//            Graphics graphics = tag.getGraphics();
            Graphics graphics = tag.createGraphics();
            int tmpHeight=0;
            for(int i=0;i<images.size();i++){
                Image image = images.get(i);
                Double rate = (double) DEFAULT_WIDTH / (double)image.getWidth(null);
                if(rate.intValue()<=0){
                    rate =1.0;
                }
                int rateHeight = rate.intValue()*image.getHeight(null);
                Image scaledInstance = image.getScaledInstance(DEFAULT_WIDTH, rateHeight, Image.SCALE_SMOOTH);
                graphics.drawImage(scaledInstance, 0, tmpHeight, DEFAULT_WIDTH, rateHeight, null);
//                graphics.drawImage(scaledInstance, 0, tmpHeight, null);
                tmpHeight+=rateHeight;
            }
            // 释放此图形的上下文以及它使用的所有系统资源。
            graphics.dispose();
            //将绘制的图像生成至输出流
            boolean write = ImageIO.write(tag, FORMATNAME, out);
            //压缩
            ImageUtil.compress(fileName,downLoadBasepath+File.separator+name);
        }catch(Exception e){
           throw new BizException(e.getMessage());
        }finally {
            //关闭输出流
            try {
                if(out!=null){
                    out.close();
                }
                FileUtil.deleteFile(fileName);
            } catch (IOException e) {
                Preconditions.checkArgument(false,e.getMessage());
            }
        }

        return downLoadBasepath+File.separator+name;
    }

    /**
     * 图片合并成jpg(不进行压缩)
     * @param imageList
     */
    public static final  String  mergeImage2Pic_NO_COMPROCESS(String name,List<String> imageList){



        FileOutputStream out = null;
        String fileName=null;//临时文件名
        try{
            //创建文件对象
            List<Image> images = imageList.stream().map(pic->{
                Image src =null;
                try {
                    src = ImageIO.read(OSSUnit.getOSS2InputStream(pic));
                } catch (IOException e) {
                    Preconditions.checkArgument(false,"读图片异常");
                }
                return src;
            }).collect(Collectors.toList());
            //获取待合并图片中最大宽度 & 图片总高度之和
            int maxWidth = 0;
            int maxHeight = 0;
            int totalHeight = 0;

            for(int i=0;i<images.size();i++){

                int width = images.get(i).getWidth(null);

                int height = images.get(i).getHeight(null);

                Double rate = (double) DEFAULT_WIDTH / (double)width;
                if(rate.intValue()<=0){
                    rate =1.0;
                }
                int rateHeight = rate.intValue()*height;
                if(width>maxWidth){
                    maxWidth = width;
                }
                if(height>maxHeight){
                    maxHeight = height;
                }
                totalHeight += rateHeight;
            }
            //构造一个类型为预定义图像类型之一的 BufferedImage。 高度为各个图片高度之和
            BufferedImage tag = new BufferedImage(DEFAULT_WIDTH, totalHeight, BufferedImage.TYPE_INT_RGB);
            //创建输出流

            fileName = downLoadBasepath+File.separator+name;
            out = new FileOutputStream(fileName);
            //绘制合成图像
//            Graphics graphics = tag.getGraphics();
            Graphics graphics = tag.createGraphics();
            int tmpHeight=0;
            for(int i=0;i<images.size();i++){
                Image image = images.get(i);
                Double rate = (double) DEFAULT_WIDTH / (double)image.getWidth(null);
                if(rate.intValue()<=0){
                    rate =1.0;
                }
                int rateHeight = rate.intValue()*image.getHeight(null);
                Image scaledInstance = image.getScaledInstance(DEFAULT_WIDTH, rateHeight, Image.SCALE_SMOOTH);
                graphics.drawImage(scaledInstance, 0, tmpHeight, DEFAULT_WIDTH, rateHeight, null);
//                graphics.drawImage(scaledInstance, 0, tmpHeight, null);
                tmpHeight+=rateHeight;
            }
            // 释放此图形的上下文以及它使用的所有系统资源。
            graphics.dispose();
            //将绘制的图像生成至输出流
            boolean write = ImageIO.write(tag, FORMATNAME, out);
            //压缩
//            ImageUtil.compress(fileName,downLoadBasepath+File.separator+name);
        }catch(Exception e){
            throw new BizException(e.getMessage());
        }finally {
            //关闭输出流
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                Preconditions.checkArgument(false,e.getMessage());
            }
        }
        return fileName;
    }




    /**
     * 图片合并成jpg,指定路径合成
     * @param imageList
     */
    public static synchronized final  void  mergeImage2Pic(String localPath,String name,List<String> imageList){



        FileOutputStream out = null;
        String fileName=null;//临时文件名
        try{
            //创建文件对象
            List<Image> images = imageList.stream().map(pic->{
                Image src =null;
                try {
                    src = ImageIO.read(OSSUnit.getOSS2InputStream(pic));
                } catch (IOException e) {
                    Preconditions.checkArgument(false,"读图片异常");
                }
                return src;
            }).collect(Collectors.toList());
            //获取待合并图片中最大宽度 & 图片总高度之和
            int maxWidth = 0;
            int maxHeight = 0;
            int totalHeight = 0;

            for(int i=0;i<images.size();i++){

                int width = images.get(i).getWidth(null);

                int height = images.get(i).getHeight(null);

                Double rate = (double) DEFAULT_WIDTH / (double)width;
                if(rate.intValue()<=0){
                    rate =1.0;
                }
                int rateHeight = rate.intValue()*height;
                if(width>maxWidth){
                    maxWidth = width;
                }
                if(height>maxHeight){
                    maxHeight = height;
                }
                totalHeight += rateHeight;
            }
            //构造一个类型为预定义图像类型之一的 BufferedImage。 高度为各个图片高度之和
            BufferedImage tag = new BufferedImage(DEFAULT_WIDTH, totalHeight, BufferedImage.TYPE_INT_RGB);
            //创建输出流
            fileName = localPath+File.separator+name;
            out = new FileOutputStream(fileName);
            //绘制合成图像
            Graphics graphics = tag.createGraphics();
            int tmpHeight=0;
            for(int i=0;i<images.size();i++){
                Image image = images.get(i);
                Double rate = (double) DEFAULT_WIDTH / (double)image.getWidth(null);
                if(rate.intValue()<=0){
                    rate =1.0;
                }
                int rateHeight = rate.intValue()*image.getHeight(null);
                Image scaledInstance = image.getScaledInstance(DEFAULT_WIDTH, rateHeight, Image.SCALE_SMOOTH);
                graphics.drawImage(scaledInstance, 0, tmpHeight, DEFAULT_WIDTH, rateHeight, null);
                tmpHeight+=rateHeight;
            }
            // 释放此图形的上下文以及它使用的所有系统资源。
            graphics.dispose();
            //将绘制的图像生成至输出流
            ImageIO.write(tag, FORMATNAME, out);
            tag.flush();
            tag=null;
        }catch(Exception e){
            throw new BizException(e.getMessage());
        }finally {
            //关闭输出流
            try {
                if(out!=null){
                    out.close();
                }
                System.gc();
            } catch (IOException e) {
                Preconditions.checkArgument(false,e.getMessage());
            }
        }
    }
//    /**
//     * 图片合成word文档
//     * @param imageList
//     */
//    public static final String  mergeImage2Doc(String name,List<String> imageList) {
//        InputStream is =null;
//        String fileName=null;
//        try {
//            WordprocessingMLPackage  wordMLPackage =  WordprocessingMLPackage.createPackage();
//            for(int i=0;i<imageList.size();i++){
//                String pic = imageList.get(i);
//                is = OSSUnit.getOSS2InputStream(pic);
//
//                int length =  is.available();
//                if (length > Integer.MAX_VALUE) {
//                    Preconditions.checkArgument(false,"File too large!!");
//                }
//                byte[] bytes = new byte[length*1000];
//
//                int offset = 0;
//                int numRead = 0;
//                while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
//                    offset += numRead;
//                }
//                BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
//                int docPrId = 1;
//                int cNvPrId = 2;
//                Inline inline = imagePart.createImageInline("AAA","BBB", docPrId, cNvPrId, false);
//                ObjectFactory factory = new ObjectFactory();
//                P paragraph = factory.createP();
//                R run = factory.createR();
//                paragraph.getContent().add(run);
//                Drawing drawing = factory.createDrawing();
//                run.getContent().add(drawing);
//                drawing.getAnchorOrInline().add(inline);
//                wordMLPackage.getMainDocumentPart().addObject(paragraph);
//            }
//            fileName = downLoadBasepath+File.separator+generateName()+DOC_SUFFIX;
//            wordMLPackage.save(new File(fileName));
//
//        } catch (Exception e) {
//            Preconditions.checkArgument(false,e.getMessage());
//
//        }finally {
//            if(is!=null){
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    Preconditions.checkArgument(false,e.getMessage());
//                }
//            }
//        }
//
//        ImageUtil.compress(fileName,downLoadBasepath+File.separator+name);
//        return downLoadBasepath+File.separator+name;
//    }
    /**
     * 图片合成word文档
     * @param imageList
     */
    public static final String  mergeImage2Doc(String name,List<String> imageList) {
        String returnKey=null;
        try{
            int width =550;
            Document document = new Document(PageSize.A4);
            returnKey = downLoadBasepath+File.separator+name;
            File file = new File(returnKey);
            RtfWriter2.getInstance(document,new FileOutputStream(file));
            document.open();

            List<String> aa = imageList.parallelStream().map(e->{
                String singlePic = ImageUtil.getSinglePic(e);
//                String singleFile = ImageUtil.getSingleFile(GeneratorIDUtil.execute() + ".jpg", e, null);
                return singlePic;
            }).collect(Collectors.toList());

            int tmpHeight =0 ;
            for(String tmp :aa){

                com.lowagie.text.Image img = com.lowagie.text.Image.getInstance(tmp);
                double rate  = (double) width/(double) img.getWidth();
                Double iii = rate *(double) img.getHeight();
                img.setAlignment(com.lowagie.text.Image.ALIGN_LEFT);// 设置图片显示位置
                img.setAbsolutePosition(0, 0);
                img.scaleAbsolute(width, iii.intValue());
                document.add(img);
                tmpHeight+=iii.intValue();
            }
            document.close();

        }catch (Exception e){
            throw new BizException(e.getMessage());
        }
        return returnKey;
    }

    private static  String generateName(){
        String str=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int randnum = (int)((Math.random()*9+1)*100000);
        return str+randnum;
    }

    /**
     * 获取视频文件
     * @param name
     * @param key
     * @return
     */
    public static final String  getSingleFile(String name,String key,String fileType) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        OSSClient ossClient =null;

        String hz =null;
        String tmpFile=null;
        String returnKey=downLoadBasepath+File.separator+name;
        if(name.contains(".")){
            hz = name.substring(name.lastIndexOf(".")+1);
            if("jpg,JPG".contains(hz)){
                tmpFile = downLoadBasepath+File.separator+GeneratorIDUtil.execute()+"."+hz;
            }else{
                tmpFile = returnKey;
            }

        }else{
            tmpFile = returnKey;
        }
        InputStream oss2InputStream =null;
        try {

            ossClient = OSSUnit.getOSSClient();
            if(StringUtils.isNotBlank(fileType) && fileType.equals(IDict.K_PIC_ID.VIDEO_INTERVIEW)){
                oss2InputStream = OSSUnit.getOSS2InputStream(ossClient, videoBucketName, key);
            }else{
                oss2InputStream = OSSUnit.getOSS2InputStream(key);
            }
            in = new BufferedInputStream(oss2InputStream);
            out = new BufferedOutputStream(new FileOutputStream(tmpFile));
            int len ;
            while ((len = in.read()) != -1) {
                out.write(len);
            }
           if("jpg,JPG".contains(hz)){
               ImageUtil.compress(tmpFile,returnKey);
           }else{
               returnKey = tmpFile;
           }

        } catch (FileNotFoundException e) {
            throw new BizException("文件不存在");
        }catch (IOException e2){
            throw new BizException("文件解析失败");
        }finally {
            try {
                if(in!=null){
                    in.close();
                }
                if(out!=null){
                    out.close();
                }
                if(ossClient!=null){
                    ossClient.shutdown();
                }
                if(!tmpFile.equals(returnKey)){
                    FileUtil.deleteFile(tmpFile);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnKey;
    }

    /**
     * 获取视频文件
     * @param key
     * @return
     */
    public static final String  getSinglePic(String key) {
        String fileName=GeneratorIDUtil.execute()+".jpg";
        ImageUtil.mergetImage2PicByConvert(downLoadBasepath+File.separator,fileName,Lists.newArrayList(key));
        return downLoadBasepath+File.separator+fileName;
    }


    /**
     * 根据指定大小压缩图片
     *
     * @param imageBytes  源图片字节数组
     * @param desFileSize 指定图片大小，单位kb
     * @param imageId     影像编号
     * @return 压缩质量后的图片字节数组
     */
    public static byte[] compressPicForScale(byte[] imageBytes, long desFileSize, String imageId) {
        if (imageBytes == null || imageBytes.length <= 0 || imageBytes.length < desFileSize * 1024) {
            return imageBytes;
        }
        long srcSize = imageBytes.length;
        double accuracy = getAccuracy(srcSize / 1024);
        try {
            while (imageBytes.length > desFileSize * 1024) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
                Thumbnails.of(inputStream)
                        .scale(accuracy)
                        .outputQuality(accuracy)
                        .toOutputStream(outputStream);
                imageBytes = outputStream.toByteArray();

            }
            LOG.info("【图片压缩】imageId={} | 图片原大小={}kb | 压缩后大小={}kb",
                    imageId, srcSize / 1024, imageBytes.length / 1024);
        } catch (Exception e) {
            LOG.error("【图片压缩】msg=图片压缩失败!", e);
        }
        return imageBytes;
    }

    /**
     * 自动调节精度(经验数值)
     *
     * @param size 源图片大小
     * @return 图片压缩质量比
     */
    private static double getAccuracy(long size) {
        double accuracy;
        if (size < 900) {
            accuracy = 0.85;
        } else if (size < 2047) {
            accuracy = 0.6;
        } else if (size < 3275) {
            accuracy = 0.44;
        } else {
            accuracy = 0.4;
        }
        return accuracy;
    }


    public static void compress(String sources,String targetName){
        try {
            byte[] bytes = FileUtils.readFileToByteArray(new File(sources));

            byte[] xes = ImageUtil.compressPicForScale(bytes, 300, targetName);

            FileUtils.writeByteArrayToFile(new File(targetName), xes);
//            FileUtils.forceDelete(new File(sources));
        } catch (IOException e) {
           throw  new BizException("图片压缩失败了...");
        }

    }

    /**
     *
     * @param data1
     * @param data2
     * @return data1 与 data2拼接的结果
     */
    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;

    }

}