package com.yamu.data.sample.service.resources.controller;

import com.yamu.data.sample.service.resources.entity.vo.*;
import com.yamu.data.sample.service.resources.service.ResourceDomainNameServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @author dys
 * @date 2022/06/24
 */
@RestController
@RequestMapping("/service/resource/resourceDomainNameServer")
@Api(value = "CDN域名网内外节点分布", tags = "CDN域名网内外节点分布API")
public class ResourceDomainNameServerController {

    @Autowired
    ResourceDomainNameServerService resourceDomainNameServerService;

    @GetMapping("dataList/v1")
    @ApiOperation("列表")
    public ResourceCdnServerListData dataList(ResourceCdnServerParam resourceCdnServerParam){
        ResourceCdnServerListData resourceCdnServerListData = resourceDomainNameServerService.dataList(resourceCdnServerParam);
        return resourceCdnServerListData;
    }

    @GetMapping("trend/v1")
    @ApiOperation("解析趋势")
    public List<ResourceCdnServerTrend> trend(ResourceCdnServerParam resourceCdnServerParam) throws ParseException {
        List<ResourceCdnServerTrend> list = resourceDomainNameServerService.trend(resourceCdnServerParam);
        return list;
    }

    @GetMapping("dispatchTrend/v1")
    @ApiOperation("节点调度趋势")
    public List<ResourceCdnServerDispatchTrend> dispatchTrend(ResourceCdnServerParam resourceCdnServerParam) throws ParseException {
        List<ResourceCdnServerDispatchTrend> list = resourceDomainNameServerService.dispatchTrend(resourceCdnServerParam);
        return list;
    }

    @GetMapping("detailList/v1")
    @ApiOperation("ip地址明细")
    public ResourceCdnServerDetailListData detailList(ResourceCdnServerParam resourceCdnServerParam){
        ResourceCdnServerDetailListData resourceCdnServerDetailListData = resourceDomainNameServerService.detailList(resourceCdnServerParam);
        return resourceCdnServerDetailListData;
    }

    @GetMapping("download/v1")
    @ApiOperation("导出")
    public void download(ResourceCdnServerParam resourceCdnServerParam, HttpServletResponse response) throws IOException, ParseException{
        resourceDomainNameServerService.download(resourceCdnServerParam,response);
    }
}
