package com.yamu.data.sample.service.resources.controller;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.service.resources.entity.bo.AnswerDistributionBO;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


/**
 * @author getiejun
 * @date 2021/10/15
 */
@RestController
@RequestMapping("/service/resource/home")
@Api(value = "首页", tags = "首页API")
public class ResourceHomeController {

    @Autowired
    private PopularDomainTopNTypeService popularDomainTopNTypeService;

    @Autowired
    private ResourceWebsiteTopNDetailService websiteTopNDetailService;

    @Autowired
    private ResourceDomainTopnDetailService domainTopnDetailService;

    @Autowired
    private AnswerDistributionService answerDistributionService;

    @Autowired
    private ResourceHomePageParseRealtimeServer realtimeServer;

    @Autowired
    private ResourceDownloadReportService resourceDownloadReportService;

    @GetMapping("domainTopNType/v1")
    public ResponseEntity findDomainTopNType(PopularDomainTopNType domainNameWebsiteDetail) {
        JSONObject finalResult = popularDomainTopNTypeService.findNetInParseReport(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("websiteTopNType/v1")
    public ResponseEntity findWebsiteTopNType(ResourceWebsiteTopNDetail websiteTopNDetail) {
        JSONObject finalResult = websiteTopNDetailService.findNetInParseReport(websiteTopNDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("domainTopN/v1")
    public ResponseEntity findDomainTopN(ResourceDomainTopnDetail domainTopNDetail) {
        JSONObject finalResult = domainTopnDetailService.findNetInParseReport(domainTopNDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("domainNetRate/v1")
    public ResponseEntity findDomainNetRate(ResourceDomainTopnDetail domainTopNDetail) {
        JSONObject finalResult = domainTopnDetailService.findNetRate(domainTopNDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("domainParse/v1")
    public ResponseEntity findDomainParse(ResourceDomainTopnDetail domainTopNDetail) {
        JSONObject finalResult = domainTopnDetailService.findDomainParse(domainTopNDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("domainParseRate/v1")
    public ResponseEntity findDomainParseRate(ResourceDomainTopnDetail domainTopNDetail) {
        JSONObject finalResult = domainTopnDetailService.findDomainParseRate(domainTopNDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("domainSuccessRate/v1")
    public ResponseEntity findDomainSuccessRate(ResourceDomainTopnDetail domainTopNDetail) {
        JSONObject finalResult = domainTopnDetailService.findDomainSuccessRate(domainTopNDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("domainNetInRate/v1")
    public ResponseEntity findDomainNetInRate(ResourceDomainTopnDetail domainTopNDetail) {
        JSONObject finalResult = domainTopnDetailService.findDomainNetInRate(domainTopNDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("domainWithInRate/v1")
    public ResponseEntity findDomainWithInRate(ResourceDomainTopnDetail domainTopNDetail) {
        JSONObject finalResult = domainTopnDetailService.findDomainWithInRate(domainTopNDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("map/v1")
    public ResponseEntity findProvinceResourceMap(AnswerDistributionBO answerDistributionBO) {
        JSONObject finalResult = answerDistributionService.findProvinceResourceMap(answerDistributionBO);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("domainDetailRate/v1")
    public ResponseEntity domainDetailRate(ResourceDomainTopnDetail domainTopNDetail) {
        JSONObject finalResult = domainTopnDetailService.domainDetailRate(domainTopNDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("parseRealtime/v1")
    public ResponseEntity findParseRealtime() {
        JSONObject finalResult = realtimeServer.findParseRealtime();
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("downloadPdf/v1")
    @ApiOperation("弹框选择导出pdf")
    public void downloadPdf(ResourceWebsiteReport resourceWebsiteReport, HttpServletResponse response) throws Exception {
        resourceDownloadReportService.download(resourceWebsiteReport, response);
    }

    @GetMapping("downloadExcel/v1")
    @ApiOperation("弹框选择导出excel")
    public void downloadExcel(ResourceWebsiteReport resourceWebsiteReport, HttpServletResponse response) throws Exception {
        resourceDownloadReportService.downloadExcel(resourceWebsiteReport, response);
    }
}
