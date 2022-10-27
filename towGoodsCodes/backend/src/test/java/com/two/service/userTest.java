package com.two.service;

import com.two.service.controller.UserNameController;
import com.two.service.entity.po.UserNameParam;
import com.two.service.entity.vo.UserNameListDataVO;
import com.two.service.entity.vo.UserNameListVO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TwoGoodsApplication.class)
@WebAppConfiguration
public class userTest {
    @Autowired
    UserNameController userNameController;

    @Test
    void dataList() {

        UserNameParam uerNameParam = new UserNameParam();
        uerNameParam.setId("001");
        uerNameParam.setName("liming");

        userNameController.dataList(uerNameParam);
    }


}
