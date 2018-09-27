package com.yunche.loan.config.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.util.ImageUtil;
import com.yunche.loan.domain.vo.CreditPicExportVO;
import com.yunche.loan.domain.vo.UniversalMaterialRecordVO;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Data
public class ThreadTask implements Runnable{



    private CountDownLatch countDownLatch;
    private CreditPicExportVO creditPicExportVO;
    private String mergeFlag;
    private String localPath;
    private LoanQueryDOMapper loanQueryDOMapper;
    public ThreadTask(LoanQueryDOMapper loanQueryDOMapper,CountDownLatch c, CreditPicExportVO creditPicExportVO,String mergeFlag,String localPath){
        this.countDownLatch =c;
        this.creditPicExportVO =creditPicExportVO;
        this.mergeFlag = mergeFlag;
        this.localPath =localPath;
        this.loanQueryDOMapper = loanQueryDOMapper;
    }
    @Override
    public void run() {

        try {
            System.out.println(Thread.currentThread().getName()+"_"+Thread.currentThread().getId()+"合成开始"+creditPicExportVO.getLoanCustomerId());
            //查图片
            Set types = Sets.newHashSet();
            //1:合成身份证图片 , 2:合成图片
            if("1".equals(mergeFlag)){
                types.add(new Byte("2"));
                types.add(new Byte("3"));
            }else{
                types.add(new Byte("2"));
                types.add(new Byte("3"));
                types.add(new Byte("4"));
                types.add(new Byte("5"));
            }
            List<UniversalMaterialRecordVO> list = loanQueryDOMapper.selectUniversalCustomerFiles(creditPicExportVO.getLoanCustomerId(), types);
            List<String> urls = Lists.newLinkedList();
            for (UniversalMaterialRecordVO V : list) {
                urls.addAll(V.getUrls());
            }
            String fileName = creditPicExportVO.getOrderId()+creditPicExportVO.getCustomerName()+creditPicExportVO.getIdCard()+ IDict.K_SUFFIX.K_SUFFIX_JPG;
            if(!CollectionUtils.isEmpty(urls)){
                ImageUtil.mergetImage2PicByConvert(localPath,fileName,urls);
                System.out.println(Thread.currentThread()+"图片合成");
            }
            System.out.println(Thread.currentThread().getName()+"_"+Thread.currentThread().getId()+"合成完成"+creditPicExportVO.getLoanCustomerId());

        }finally {
            countDownLatch.countDown();
        }


    }
}
