package com.yunche.loan;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.mapper.LoanStatementDOMapper;
import com.yunche.loan.service.BankSolutionService;
import com.yunche.loan.service.CollectionService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Test extends BaseTest {
    @Resource
    private CollectionService collectionService;

    @Resource
    private BankSolutionService bankSolutionService;

    @Resource
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @org.junit.Test
    public void test() throws IOException {
        //bankSolutionService.commonBusinessApply(new Long("1806291133480804371"));
        bankSolutionService.creditAutomaticCommit(new Long("1806291133480804371"));
    }


    public static void main(String[] args) throws IOException {
        testDiGui();
    }

    public static void testDiGui() throws IOException {
        String json = "{\"198\":{\"id\":198,\"name\":\"总经理\"},\"231\":{\"id\":231,\"name\":\"小东东\"},\"199\":{\"id\":199,\"name\":\"渠道部经理\"},\"232\":{\"id\":232,\"name\":\"小东东\"},\"211\":{\"id\":211,\"name\":\"张三\",\"parentId\":212},\"233\":{\"id\":233,\"name\":\"任我行\"},\"212\":{\"id\":212,\"name\":\"李涵\",\"parentId\":211},\"234\":{\"id\":234,\"name\":\"易翠\"},\"235\":{\"id\":235,\"name\":\"电催专员\",\"parentId\":215},\"214\":{\"id\":214,\"name\":\"季名\"},\"236\":{\"id\":236,\"name\":\"王刚啊啊啊\",\"parentId\":215},\"215\":{\"id\":215,\"name\":\"飞飞\",\"parentId\":208},\"238\":{\"id\":238,\"name\":\"王刚\"},\"217\":{\"id\":217,\"name\":\"苏苏\",\"parentId\":214},\"239\":{\"id\":239,\"name\":\"阿杜\",\"parentId\":238},\"218\":{\"id\":218,\"name\":\"jjij\",\"parentId\":201},\"219\":{\"id\":219,\"name\":\"菲菲\",\"parentId\":201},\"240\":{\"id\":240,\"name\":\"可爱哲哲\",\"parentId\":238},\"241\":{\"id\":241,\"name\":\"刚妹妹\"},\"242\":{\"id\":242,\"name\":\"季白\"},\"1\":{\"id\":1,\"name\":\"admin\"},\"243\":{\"id\":243,\"name\":\"蜗牛\",\"parentId\":242},\"200\":{\"id\":200,\"name\":\"电审总监\"},\"244\":{\"id\":244,\"name\":\"沉默\",\"parentId\":243},\"201\":{\"id\":201,\"name\":\"电审经理\"},\"223\":{\"id\":223,\"name\":\"乔七七\"},\"245\":{\"id\":245,\"name\":\"乌龟\",\"parentId\":242},\"202\":{\"id\":202,\"name\":\"电审组长\"},\"224\":{\"id\":224,\"name\":\"谭清水\",\"parentId\":212},\"203\":{\"id\":203,\"name\":\"电审专员\"},\"225\":{\"id\":225,\"name\":\"白子画\",\"parentId\":223},\"247\":{\"id\":247,\"name\":\"许栩\",\"parentId\":244},\"204\":{\"id\":204,\"name\":\"财务经理\"},\"206\":{\"id\":206,\"name\":\"培训经理\"},\"228\":{\"id\":228,\"name\":\"飞飞\",\"parentId\":211},\"208\":{\"id\":208,\"name\":\"催收主管\"},\"209\":{\"id\":209,\"name\":\"银行驻点\"}}";

        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.readValue(json, Map.class); //json转换成map
        String loginUserId = "242";
        Set<String> set = Sets.newHashSet();
        Set<String> tempSet = Sets.newHashSet();
        set.add(loginUserId);
        int i = 0;
        while (true) {
            i++;
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry result = (Map.Entry) it.next();
                Object parentId = ((Map) result.getValue()).get("parentId");
                if (parentId != null) {
                    for (String str : set) {
                        if (parentId.toString().equals(str)) {
                            tempSet.add(((Map) result.getValue()).get("id").toString());
                        }
                    }
                    set.addAll(tempSet);
                }
            }
            if (i > set.size()) {
                break;
            }
            if (i > map.size()) {
                break;
            }
        }
        System.out.println(set);
    }

    @Resource
    private LoanStatementDOMapper loanStatementDOMapper;

    public  void tests() throws IOException {
//        List list = loanStatementDOMapper.statisticsTelephoneVerifyNodeOrders("2016-08-09", "2018-07-01");
//        System.out.println(list);


    }
}
