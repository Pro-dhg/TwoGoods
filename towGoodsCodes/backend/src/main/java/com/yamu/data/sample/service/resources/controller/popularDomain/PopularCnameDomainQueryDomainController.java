package com.yamu.data.sample.service.resources.controller.popularDomain;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.PopularCnameDomainFlowTrend;
import com.yamu.data.sample.service.resources.entity.vo.PopularCnameDomainNetInTrend;
import com.yamu.data.sample.service.resources.mapper.PopularCnameDomainQueryDomainMapper;
import com.yamu.data.sample.service.resources.service.PopularCnameDomainQueryDomainService;
import com.yamu.data.sample.service.resources.service.PopularDomainCnameFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author dhg
 * @Date 2022/10/19
 * @DESC cname域名反查域名
 */
@RestController
@RequestMapping("/service/resource/cnameDomainQueryDomain")
@Api(value = "热点域名分析", tags = "CNAME域名反查域名API")
public class PopularCnameDomainQueryDomainController {

    @Autowired
    private PopularCnameDomainQueryDomainService popularCnameDomainQueryDomainService;

    @PostMapping("dataList")
    @ApiOperation("列表")
    public PopularCnameDomainListData dataList(@RequestBody PopularCnameDomainQueryParam popularCnameDomainQueryParam) throws YamuException {
        PopularCnameDomainListData popularCnameDomainQueryDomainListData = popularCnameDomainQueryDomainService.dataList(popularCnameDomainQueryParam);
        return popularCnameDomainQueryDomainListData;
    }

    @PostMapping("flowTrend")
    @ApiOperation("流量趋势")
    public List<PopularCnameDomainFlowTrend> flowTrend(@RequestBody PopularCnameDomainFlowTrendParam popularCnameDomainFlowTrendParam) {
        List<PopularCnameDomainFlowTrend> list = popularCnameDomainQueryDomainService.flowTrend(popularCnameDomainFlowTrendParam);
        return list;
    }
    @PostMapping("netInTrend")
    @ApiOperation("本网率趋势")
    public List<PopularCnameDomainNetInTrend> netInTrend(@RequestBody PopularCnameDomainFlowTrendParam popularCnameDomainFlowTrendParam) {
        List<PopularCnameDomainNetInTrend> list = popularCnameDomainQueryDomainService.netInTrend(popularCnameDomainFlowTrendParam);
        return list;
    }

    @PostMapping("download")
    @ApiOperation("导出")
    public void download(@RequestBody PopularCnameDomainParam popularCnameDomainParam, HttpServletResponse response) throws IOException {
        popularCnameDomainQueryDomainService.download(popularCnameDomainParam, response);
    }
    @PostMapping("allDownload")
    @ApiOperation("列表导出")
    public void allDownload(@RequestBody PopularCnameDomainQueryParam popularCnameDomainQueryParam, HttpServletResponse response) throws IOException {
        popularCnameDomainQueryDomainService.allDownload(popularCnameDomainQueryParam, response);
    }


}
