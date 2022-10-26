package com.yamu.data.sample.service.resources.controller.popularDomain;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.WebsiteSecondLevelDomainTopN;
import com.yamu.data.sample.service.resources.service.WebsiteSecondLevelDomainTopNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yuyuan.Dong
 * @Date 2021/8/18
 * @DESC
 */
@RestController
@RequestMapping("/service/resource/websiteSecondLevelDomain")
public class WebsiteSecondLevelDomainTopNController {

    @Autowired
    private WebsiteSecondLevelDomainTopNService secondLevelDomainTopNService;

    /**
     * 网站数据明细表.
     *
     * @param domainNameWebsiteDetail
     * @return
     */
    @GetMapping("tableDetail/v1")
    public ResponseEntity findTableDetail(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) {
        checkSelectParam(domainNameWebsiteDetail);
        PageResult pageResult = secondLevelDomainTopNService.findTableDetail(domainNameWebsiteDetail);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 网站子表数据明细表.
     *
     * @param domainNameWebsiteDetail
     * @return
     */
    @GetMapping("SecondTableDetail/v1")
    public ResponseEntity findSecondTableDetail(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) throws ParseException{
        checkSelectParam(domainNameWebsiteDetail);
        PageResult pageResult = secondLevelDomainTopNService.findSecondTableDetail(domainNameWebsiteDetail);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 本网本省趋势.
     *
     * @param domainNameWebsiteDetail
     * @return
     */
    @GetMapping("rateReport/v1")
    public ResponseEntity findRateReport(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) throws Exception {
        JSONObject finalResult = secondLevelDomainTopNService.findRateReport(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 资源分布.
     *
     * @param domainNameWebsiteDetail
     * @return
     * @throws ParseException
     */
    @GetMapping("resourceReport/v1")
    public ResponseEntity findResourceReport(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) throws ParseException {
        JSONObject finalResult = secondLevelDomainTopNService.findResourceReport(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * CDN次数,IDC次数
     *
     * @param domainNameWebsiteDetail
     * @return
     * @throws ParseException
     */
    @GetMapping("parseReport/v1")
    public ResponseEntity findParseReport(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) throws ParseException {
        JSONObject finalResult = secondLevelDomainTopNService.findParseReport(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("download/v1")
    public void downloadRankNumber(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail, HttpServletResponse response) throws Exception {
        checkSelectParam(domainNameWebsiteDetail);
        List<WebsiteSecondLevelDomainTopN> dataList = secondLevelDomainTopNService.downloadByParam(domainNameWebsiteDetail);
        dataList.stream().forEach(WebsiteSecondLevelDomainTopN::buildRate);
        List<String> csvLines = dataList.stream().map(WebsiteSecondLevelDomainTopN::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(WebsiteSecondLevelDomainTopN.CSV_NAME, domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime(), WebsiteSecondLevelDomainTopN.CSV_HEAD, csvLines, response);
    }

    // check select
    private void checkSelectParam(WebsiteSecondLevelDomainTopN param) {
        param.setWebsiteAppName(ReportUtils.escapeChar(param.getWebsiteAppName()));
        param.setSecondLevelDomain(ReportUtils.escapeChar(param.getSecondLevelDomain()));
        param.setSecondLevelDomainName(ReportUtils.escapeChar(param.getSecondLevelDomainName()));
    }
}
