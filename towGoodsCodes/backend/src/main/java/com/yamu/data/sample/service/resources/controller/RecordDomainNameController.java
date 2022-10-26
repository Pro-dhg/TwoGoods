package com.yamu.data.sample.service.resources.controller;

import com.yamu.data.sample.service.resources.entity.po.RecordDomainNameDetailList;
import com.yamu.data.sample.service.resources.entity.po.RecordDomainNameListData;
import com.yamu.data.sample.service.resources.entity.po.RecordDomainNameParam;
import com.yamu.data.sample.service.resources.service.RecordDomainNameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author dys
 * @date 2022/05/12
 */
@RestController
@RequestMapping("/service/resource/recordDomainName")
@Api(value = "备案域分析", tags = "备案域分析API")
public class RecordDomainNameController {

    @Autowired
    RecordDomainNameService recordDomainNameService;

    @GetMapping("dataList/v1")
    @ApiOperation("列表")
    public RecordDomainNameListData dataList(RecordDomainNameParam recordDomainNameParam){
        RecordDomainNameListData recordDomainNameListData = recordDomainNameService.dataList(recordDomainNameParam);
        return recordDomainNameListData;
    }

    @GetMapping("dataDetailList/v1")
    @ApiOperation("明细图标")
    public List<RecordDomainNameDetailList> dataDetailList(RecordDomainNameParam recordDomainNameParam){
        List<RecordDomainNameDetailList> list = recordDomainNameService.dataDetailList(recordDomainNameParam);
        return list;
    }

    @GetMapping("download/v1")
    @ApiOperation("导出")
    public void download(RecordDomainNameParam recordDomainNameParam, HttpServletResponse response) throws IOException {
        recordDomainNameService.download(recordDomainNameParam,response);
    }
}
