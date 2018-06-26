package com.yunche.loan.config.util;

import com.google.common.base.Preconditions;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

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

    private static final String PIC_SUFFIX=".jpg";
    private static final String DOC_SUFFIX=".doc";

    static {
        ResourceBundle bundle = PropertyResourceBundle.getBundle("oss");
        downLoadBasepath = bundle.containsKey("downLoadBasepath") == false ? "" : bundle.getString("downLoadBasepath");
    }

    /**
     * 图片合并成jpg
     * @param imageList
     */
    public static void mergeImage2Pic(List<String> imageList){
        FileOutputStream out = null;
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
            int totalHeight = 0;
            for(int i=0;i<images.size();i++){
                int width = images.get(i).getWidth(null);
                int height = images.get(i).getHeight(null);
                if(width>maxWidth){
                    maxWidth = width;
                }
                totalHeight += height;
            }


            //构造一个类型为预定义图像类型之一的 BufferedImage。 高度为各个图片高度之和
            BufferedImage tag = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
            //创建输出流
            String fileName = downLoadBasepath+File.separator+generateName()+PIC_SUFFIX;
            out = new FileOutputStream(fileName);
            //绘制合成图像
            Graphics g = tag.createGraphics();
            int tmpHeight=0;
            for(int i=0;i<images.size();i++){
                Image image = images.get(i);
                g.drawImage(image, 0, tmpHeight, maxWidth, image.getHeight(null), null);
                tmpHeight+=image.getHeight(null);
            }
            // 释放此图形的上下文以及它使用的所有系统资源。
            g.dispose();
            //将绘制的图像生成至输出流
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag);
        }catch(Exception e){
            e.printStackTrace();
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
    }

    /**
     * 图片合成word文档
     * @param imageList
     */
    public static void mergeImage2Doc(List<String> imageList) {
        InputStream is =null;
        try {
            WordprocessingMLPackage  wordMLPackage =  WordprocessingMLPackage.createPackage();
            for(int i=0;i<imageList.size();i++){
                String pic = imageList.get(i);
                is = OSSUnit.getOSS2InputStream(pic);
                int length =  is.available();
                if (length > Integer.MAX_VALUE) {
                    Preconditions.checkArgument(false,"File too large!!");
                }
                byte[] bytes = new byte[length*1000];

                int offset = 0;
                int numRead = 0;
                while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                    offset += numRead;
                }
                BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
                int docPrId = 1;
                int cNvPrId = 1;
                Inline inline = imagePart.createImageInline("AAA","BBB", docPrId, cNvPrId, false);
                ObjectFactory factory = new ObjectFactory();
                P paragraph = factory.createP();
                R run = factory.createR();
                paragraph.getContent().add(run);
                Drawing drawing = factory.createDrawing();
                run.getContent().add(drawing);
                drawing.getAnchorOrInline().add(inline);
                wordMLPackage.getMainDocumentPart().addObject(paragraph);
            }
            String fileName = downLoadBasepath+File.separator+generateName()+DOC_SUFFIX;
            wordMLPackage.save(new File(fileName));

        } catch (Exception e) {
            Preconditions.checkArgument(false,e.getMessage());

        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    Preconditions.checkArgument(false,e.getMessage());
                }
            }
        }
    }

    private static  String generateName(){
        String str=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int randnum = (int)((Math.random()*9+1)*100000);
        return str+randnum;
    }
}