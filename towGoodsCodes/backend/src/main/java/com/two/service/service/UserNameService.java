package com.two.service.service;

import com.two.service.entity.po.UserNameParam;
import com.two.service.entity.vo.UserNameListDataVO;
import com.two.service.entity.vo.UserNameListVO;
import com.two.service.mapper.UserNameMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * @Author dhg
 * @Date 2022/10/19
 */
@Service
@Slf4j
public class UserNameService {

    @Autowired
    UserNameMapper userNameMapper;

    public UserNameListDataVO dataList(UserNameParam uerNameParam) {

        String tableName = "two_goods.dim_user_user_local" ;
        UserNameListDataVO userNameListDataVO = new UserNameListDataVO();
        Long total = userNameMapper.getTotal(uerNameParam,tableName);
        List<UserNameListVO> dataList = userNameMapper.getDataList(uerNameParam,tableName);

        userNameListDataVO.setTotal(total);
        userNameListDataVO.setData(dataList);

        return userNameListDataVO;
    }

}
