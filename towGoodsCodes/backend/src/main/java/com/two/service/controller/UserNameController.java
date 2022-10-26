package com.two.service.controller;

import com.two.service.entity.po.UserNameParam;
import com.two.service.entity.vo.UserNameListDataVO;
import com.two.service.service.UserNameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author dhg
 * @Date 2022/10/19
 * @DESC cname域名反查域名
 */
@RestController
@RequestMapping("/service/user/userName/")
@Api(value = "热点域名分析", tags = "CNAME域名反查域名API")
public class UserNameController {

    @Autowired
    private UserNameService UserNameService ;

    @PostMapping("dataList")
    @ApiOperation("列表")
    public UserNameListDataVO dataList(@RequestBody UserNameParam uerNameParam) {
        UserNameListDataVO userNameListData = UserNameService.dataList(uerNameParam);
        return userNameListData;
    }


}
