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
//        fileKey.add("img/2018/201805/20180504/k6FY5XC2J5.jpg");
//        fileKey.add("img/2018/201805/20180504/WNf4THHX8M.jpg");
//        System.out.println("开始时间："+System.currentTimeMillis());
//        Long startTime =System.currentTimeMillis();
//        try {
//            Runtime.getRuntime().exec("mkdir /tmp/fdfd");
//        } catch (IOException e) {
//            System.out.println("创建路径 /tmp/aaa 失败");
//        }
//        ImageUtil.mergetImage2PicByConvert("/tmp/fdfd/","787878.jpg",fileKey);
//        ImageUtil.mergeImage2Pic("/tmp/aaa","hahha.jpg",fileKey);
//        ImageUtil.mergeImage2Pic("/tmp/aaa","bbbbb.jpg",fileKey);
//        ImageUtil.mergeImage2Pic_NO_COMPROCESS("77779.jpg",fileKey);



//        Long endTime =System.currentTimeMillis();
//        System.out.println("结束时间："+System.currentTimeMillis());
//        System.out.println("用时："+(endTime-startTime)/1000);
//        ImageUtil.mergeImage2Pic(fileKey);
//        ImageUtil.mergeImage2Doc(fileKey);
        ArrayList<String> image = Lists.newArrayList();
//        image.add("img/2019/201901/20190108/xZ3BnEzkTW.jpg");
//        image.add("img/2019/201901/20190108/YzSirD6Qc3.jpg");
//        image.add("img/2019/201901/20190108/rn2knDHf5z.jpg");
            image.add("img/2019/201901/20190107/3rHwKQeP2N.jpg");
            image.add("img/2019/201901/20190107/TE23SkbMyi.jpg");
//            image.add("IMG/2019/201901/20190110/20190110091512/12345/1547082912052.139160.jpg");
//            image.add("IMG/2019/201901/20190110/20190110091515/12345/1547082915746.907959.jpg");
        ImageUtil.mergetImage2PicByConvert("/tmp/",GeneratorIDUtil.execute()+".jpg",image);
        ImageUtil.mergeImage2Doc(image);
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

//    @Test
//    public void func5(){
//        try {
//            IMOperation operation = new IMOperation();
//            Operation operation1 = operation.addImage("/tmp/77777.jpg", "/tmp/77778.jpg");
//            ConvertCmd cmd = new ConvertCmd();
//            cmd.setSearchPath("/tmp/aaa.jpg");
//            cmd.run(operation1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IM4JavaException e) {
//            e.printStackTrace();
//        }
//
//    }
}


