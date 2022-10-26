package com.yamu.data.sample.service.resources.controller.partnerDomain;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.result.ErrorResult;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.resources.entity.po.PartnerDomainTopNWebsite;
import com.yamu.data.sample.service.resources.service.PartnerDomainTopNWebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ZhangYanping
 * @Date: 2021/7/5 23:44
 * @Desc: 特定域名TopN网站分析
 */

@RestController
@RequestMapping("/service/resource/domainTopNWebsite")
public class PartnerDomainTopNWebsiteController {

    @Autowired
    private PartnerDomainTopNWebsiteService service;

    @RequestMapping("detailTable/v1")
    public ResponseEntity findDetailReportByPage(PartnerDomainTopNWebsite param) {
        // 网站名称: 不能超过200个字符
        if (ObjectUtil.isNotEmpty(param.getWebsiteName())) {
            if (param.getWebsiteName().length() > 200) {
                return new ResponseEntity(new ErrorResult(ErrorResult.SYSTEM_FAILURE, "网站名称不能超过200个字符"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        PageResult result = service.findDetailReportByPage(param);
        return ResponseEntity.ok(result);
    }

    // top20网站趋势
    @GetMapping("top20ResourceReport/v1")
    public ResponseEntity findTop20ReportGroupByParseTimeByParam(PartnerDomainTopNWebsite param, boolean isCount) {
        JSONObject finalResult = service.findTop20ReportGroupByParseTimeByParam(param);
        return ResponseEntity.ok(finalResult);
    }

    // 资源
    @GetMapping("resourceReport/v1")
    public ResponseEntity findResourceReport(PartnerDomainTopNWebsite param, boolean isCount) {
        JSONObject finalResult = service.findResourceReport(param, isCount);
        return ResponseEntity.ok(finalResult);
    }

    // TopN
    @GetMapping("topNRateReport/v1")
    public ResponseEntity findTopNRateReport(PartnerDomainTopNWebsite param) {
        JSONObject finalResult = service.findRateReport(param);
        return ResponseEntity.ok(finalResult);
    }

    // 某网站
    @GetMapping("rateReport/v1")
    public ResponseEntity findRateReport(PartnerDomainTopNWebsite param) {
        JSONObject finalResult = service.findRateReport(param);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("parseReport/v1")
    public ResponseEntity findParseReport(PartnerDomainTopNWebsite param) {
        JSONObject finalResult = service.findParseReport(param);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("download/v1")
    public void downloadRankNumber(PartnerDomainTopNWebsite param, HttpServletResponse response) throws Exception {
//        checkDownloadMethodParam(param);
        List<PartnerDomainTopNWebsite> dataList = service.downloadByParam(param);
        dataList.stream().forEach(PartnerDomainTopNWebsite::buildRate);
        List<String> csvLines = dataList.stream().map(PartnerDomainTopNWebsite::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(PartnerDomainTopNWebsite.CSV_NAME, param.getStartTime(), param.getEndTime(), PartnerDomainTopNWebsite.CSV_HEAD, csvLines, response);
    }

//    private void checkDownloadMethodParam(PartnerDomainTopNWebsite param) throws UnsupportedEncodingException {
//        if (ObjectUtil.isNotEmpty(param.getWebsiteName())) {
//            param.setWebsiteName(URLDecoder.decode(param.getWebsiteName(), "utf-8"));
//        }
//    }
}
