package com.yamu.data.sample.service.resources.controller;


import com.yamu.data.sample.service.common.entity.PageResult;
import com.yamu.data.sample.service.resources.entity.bo.AbnormalJikeParam;
import com.yamu.data.sample.service.resources.entity.vo.AbnormalJikeList;
import com.yamu.data.sample.service.resources.service.AbnormalJikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author dys
 * @date 2022/09/22
 */
@RestController
@RequestMapping("/service/resource/abnormalJike")
@Api(value = "异常集客挖掘", tags = "异常集客挖掘API")
public class AbnormalJikeController {

    @Autowired
    private AbnormalJikeService abnormalJikeService;

    @PostMapping("list")
    @ApiOperation("列表数据")
    public PageResult<AbnormalJikeList> list(@RequestBody AbnormalJikeParam abnormalJikeParam){
        return abnormalJikeService.list(abnormalJikeParam);
    }

    @PostMapping("download")
    @ApiOperation("导出")
    public void download(@RequestBody AbnormalJikeParam abnormalJikeParam, HttpServletResponse response) throws IOException {
        abnormalJikeService.download(abnormalJikeParam,response);
    }
}
