package com.yamu.data.sample.service.resources.controller;

import com.yamu.data.sample.service.resources.entity.po.NotRecordDomainNameListData;
import com.yamu.data.sample.service.resources.entity.po.RecordDomainNameDetailList;
import com.yamu.data.sample.service.resources.entity.po.RecordDomainNameParam;
import com.yamu.data.sample.service.resources.service.NotRecordDomainNameService;
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
@RequestMapping("/service/resource/notRecordDomainName")
@Api(value = "未备案域分析", tags = "未备案域分析API")
public class NotRecordDomainNameController {

    @Autowired
    NotRecordDomainNameService notRecordDomainNameService;

    @GetMapping("dataList/v1")
    @ApiOperation("列表")
    public NotRecordDomainNameListData dataList(RecordDomainNameParam recordDomainNameParam){
        NotRecordDomainNameListData notRecordDomainNameListData = notRecordDomainNameService.dataList(recordDomainNameParam);
        return notRecordDomainNameListData;
    }

    @GetMapping("dataDetailList/v1")
    @ApiOperation("明细图标")
    public List<RecordDomainNameDetailList> dataDetailList(RecordDomainNameParam recordDomainNameParam){
        List<RecordDomainNameDetailList> list = notRecordDomainNameService.dataDetailList(recordDomainNameParam);
        return list;
    }

    @GetMapping("download/v1")
    @ApiOperation("导出")
    public void download(RecordDomainNameParam recordDomainNameParam, HttpServletResponse response) throws IOException{
        notRecordDomainNameService.download(recordDomainNameParam,response);
    }
}
