package com.yunche.loan;

import com.google.common.collect.Lists;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.rtf.RtfWriter2;
import com.yunche.loan.config.util.GeneratorIDUtil;
import com.yunche.loan.config.util.ImageUtil;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//public class ImageTest extends BaseTest{
public class ImageTest {

    @Test
    public void fun1(){
        //img/2018/201805/20180504/k6FY5XC2J5.jpg
        //["img/2018/201805/20180504/WNf4THHX8M.jpg"]
        ArrayList<String> fileKey = Lists.newArrayList();
//        fileKey.add("IMG/2018/201807/20180713/null/1531466823334.jpg");
//        fileKey.add("img/2018/201807/20180716/ANd6tRAxFe.jpg");
//        fileKey.add("img/2018/201807/20180718/brGBD2zs4K.jpg");

//        ImageUtil.mergeImage2Pic(fileKey);
//        ImageUtil.mergeImage2Doc(fileKey);
        fileKey.add("img/2018/201805/20180504/k6FY5XC2J5.jpg");
        fileKey.add("img/2018/201805/20180504/WNf4THHX8M.jpg");
//        ImageUtil.mergeImage2Pic(fileKey);
        ImageUtil.mergeImage2Doc(fileKey);
//        ArrayList<String> image = Lists.newArrayList();
//        image.add("img/2018/201807/20180716/3D5rCiMtZB.jpg");
//        image.add("img/2018/201807/20180716/ANd6tRAxFe.jpg");
//        image.add("img/2018/201807/20180716/xhmYjt8tbe.jpg");
//        ImageUtil.mergeImage2Doc(image);
    }
    @Test
    public void func2(){
        try{

            int width =550;
//            Runtime.getRuntime().exec("touch /tmp/aaa.doc");
            Document document = new Document(PageSize.A4);
            File file = new File("/tmp/1010.doc");
            RtfWriter2.getInstance(document,new FileOutputStream(file));
            document.open();

            ArrayList<String> objects = Lists.newArrayList();
            objects.add("img/2018/201807/20180716/ANd6tRAxFe.jpg");
            objects.add("img/2018/201807/20180718/brGBD2zs4K.jpg");
//            objects.add("img/2018/201805/20180504/k6FY5XC2J5.jpg");
//            objects.add("img/2018/201805/20180504/WNf4THHX8M.jpg");
//            objects.add("/tmp/1531466823334.jpg");
//            objects.add("/tmp/brGBD2zs4K.jpg");
//            String s = ImageUtil.mergeImage2Pic(objects);
           List<String> aa = objects.parallelStream().map(e->{
               String singleFile = ImageUtil.getSingleFile(GeneratorIDUtil.execute() + ".jpg", e, null);
               return singleFile;
            }).collect(Collectors.toList());
//            ImageUtil.getSingleFile(GeneratorIDUtil.execute());
            int tmpHeight =0 ;
            for(String tmp :aa){

                Image img = Image.getInstance(tmp);
                double rate  = (double) width/(double) img.getWidth();
                Double iii = rate *(double) img.getHeight();
                img.setAlignment(Image.ALIGN_LEFT);// 设置图片显示位置
                img.setAbsolutePosition(0, 0);
                img.scaleAbsolute(width, iii.intValue());
                document.add(img);
                tmpHeight+=iii.intValue();
            }
            document.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
    public void func3(){
        ImageUtil.compress("/tmp/4545.png","/tmp/9999.png");
        return ;
    }
    @Test
    public void func4(){
      String name = "9889huh.jpg";
      if(name.contains(".")){
          String substring = name.substring(name.lastIndexOf("."));
          System.out.println(substring);
      }else{
          System.out.println("iiii");
      }

    }
}


