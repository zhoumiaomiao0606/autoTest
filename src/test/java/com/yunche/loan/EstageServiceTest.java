package com.yunche.loan;

import com.yunche.loan.estage.processor.EstageCreditApplyProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Description:
 * author: yu.hb
 * Date: 2019-03-07
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class EstageServiceTest {

    @Resource
    private EstageCreditApplyProcessor estageCreditApplyProcessor;

    @Test
    public void testCreditApply() {
        estageCreditApplyProcessor.processInternal(1805211701545169109L);
    }

}
