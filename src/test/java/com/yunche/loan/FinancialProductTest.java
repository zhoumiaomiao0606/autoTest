package com.yunche.loan;

import com.yunche.loan.web.controller.FinancialProductController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
@RunWith(SpringRunner.class)    // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest(classes = App.class)    // 指定我们SpringBoot工程的Application启动类
@Transactional
@Rollback
public class FinancialProductTest extends MockMvcResultMatchers {

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(new FinancialProductController()).build();
    }

    @Test
    public void testFinancialProductController() throws Exception {
        // 测试UserController
        RequestBuilder request = null;

        // 1、get查一下user列表，应该为空
//        request = MockMvcRequestBuilders.get("/financialproduct/getById");
//        mvc.perform(request)
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string("[]"));

        // 2、post提交一个user
//        request = MockMvcRequestBuilders.post("/financialProduct/create")
//                .param("prodId", "1")
//                .param("bankName", "中国工商银行股份有限公司哈尔滨顾乡支行")
//                .param("mnemonicCode", "ICBU")
//                .param("account", "111111111")
//                .param("signPhone", "18951929432")
//                .param("signBankCode", "22222222")
//                .param("bizType", "0")
//                .param("categorySuperior", "信用卡分期产品")
//                .param("categoryJunior", "基准利率产品")
//                .param("rate", "0利率3成首付")
//                .param("mortgageTerm", "24")
//                .param("areaId", "150000000000");
//        mvc.perform(request)
//                .andExpect(MockMvcResultMatchers.content().string("创建成功"));

        // 3、get获取user列表，应该有刚才插入的数据
//        request = MockMvcRequestBuilders.get("/users/");
//        mvc.perform(request)
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string(("[{\"id\":1,\"name\":\"测试大师\",\"age\":20}]"));
//
//        // 4、put修改id为1的user
//        request = MockMvcRequestBuilders.put("/users/1")
//                .param("name", "测试终极大师")
//                .param("age", "30");
//        mvc.perform(request)
//                .andExpect(MockMvcResultMatchers.content().string("success"));
//
        // 5、get一个id为1的user
        request = MockMvcRequestBuilders.get("/financialProduct/getById/1");
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.content().string("{\"id\":1,\"name\":\"测试终极大师\",\"age\":30}"));
//
//        // 6、del删除id为1的user
//        request = MockMvcRequestBuilders.delete("/users/1");
//        mvc.perform(request)
//                .andExpect(MockMvcResultMatchers.content().string("success"));
//
        // 7、get查一下user列表，应该为空
//        request = MockMvcRequestBuilders.get("/financialProduct/getByCondition");
//        mvc.perform(request)
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string("[]"));

    }
}
