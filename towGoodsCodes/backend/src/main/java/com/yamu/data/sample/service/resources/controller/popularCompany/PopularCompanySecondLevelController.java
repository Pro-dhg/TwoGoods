package com.yamu.data.sample.service.resources.controller.popularCompany;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.common.utils.csv.ExportCsvFileUtil;
import com.yamu.data.sample.service.resources.entity.bo.SecondDomainTableListBO;
import com.yamu.data.sample.service.resources.entity.po.PopularCompanySecondLevel;
import com.yamu.data.sample.service.resources.service.PopularCompanySecondLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * @Date 2022/3/16
 * @DESC
 */
@RestController
@RequestMapping("/service/resource/PopularCompanySecondLevel")
@Api(value = "TopN公司子域名分析", tags = "TopN公司子域名分析API")
public class PopularCompanySecondLevelController {

    @Autowired
    private PopularCompanySecondLevelService secondLevelDomainTopNService;

    /**
     * 公司数据明细表.
     *
     * @param companySecondLevel
     * @return
     */
    @GetMapping("tableDetail/v1")
    @ApiOperation("公司数据明细表")
    public SecondDomainTableListBO findTableDetail(PopularCompanySecondLevel companySecondLevel) {
        checkSelectParam(companySecondLevel);
        SecondDomainTableListBO pageResult = secondLevelDomainTopNService.findTableDetail(companySecondLevel);
        return pageResult;
    }

    /**
     * 公司子表数据明细表.
     *
     * @param companySecondLevel
     * @return
     */
    @GetMapping("SecondTableDetail/v1")
    @ApiOperation("公司子表数据明细表")
    public ResponseEntity findSecondTableDetail(PopularCompanySecondLevel companySecondLevel) throws ParseException {
        checkSelectParam(companySecondLevel);
        PageResult pageResult = secondLevelDomainTopNService.findSecondTableDetail(companySecondLevel);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 本网本省趋势.
     *
     * @param companySecondLevel
     * @return
     */
    @GetMapping("rateReport/v1")
    @ApiOperation("本网本省趋势")
    public ResponseEntity findRateReport(PopularCompanySecondLevel companySecondLevel) throws Exception {
        JSONObject finalResult = secondLevelDomainTopNService.findRateReport(companySecondLevel);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 资源分布.
     *
     * @param companySecondLevel
     * @return
     * @throws ParseException
     */
    @GetMapping("resourceReport/v1")
    @ApiOperation("资源分布")
    public ResponseEntity findResourceReport(PopularCompanySecondLevel companySecondLevel) throws ParseException {
        JSONObject finalResult = secondLevelDomainTopNService.findResourceReport(companySecondLevel);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * CDN次数,IDC次数
     *
     * @param companySecondLevel
     * @return
     * @throws ParseException
     */
    @GetMapping("parseReport/v1")
    @ApiOperation("CDN次数,IDC次数")
    public ResponseEntity findParseReport(PopularCompanySecondLevel companySecondLevel) throws ParseException {
        JSONObject finalResult = secondLevelDomainTopNService.findParseReport(companySecondLevel);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("download/v1")
    @ApiOperation("导出")
    public void downloadRankNumber(PopularCompanySecondLevel companySecondLevel, HttpServletResponse response) throws Exception {
        checkSelectParam(companySecondLevel);
        List<PopularCompanySecondLevel> dataList = secondLevelDomainTopNService.downloadByParam(companySecondLevel);
        dataList.stream().forEach(PopularCompanySecondLevel::buildRate);
        List<String> csvLines = dataList.stream().map(PopularCompanySecondLevel::getCsvLineSting).collect(Collectors.toList());
        ExportCsvFileUtil.exportCsv(PopularCompanySecondLevel.CSV_NAME, companySecondLevel.getStartTime(), companySecondLevel.getEndTime(), PopularCompanySecondLevel.CSV_HEAD, csvLines, response);
    }

    // check select
    private void checkSelectParam(PopularCompanySecondLevel param) {
        param.setCompanyShortName(ReportUtils.escapeChar(param.getCompanyShortName()));
        param.setSecondLevelDomain(ReportUtils.escapeChar(param.getSecondLevelDomain()));
        param.setSecondLevelDomainName(ReportUtils.escapeChar(param.getSecondLevelDomainName()));
    }

    /**
     * 服务节点ip解析量.
     *
     * @param companySecondLevel
     * @return
     */
    @GetMapping("findDetails/v1")
    public ResponseEntity nodeServerDetail(PopularCompanySecondLevel companySecondLevel) throws ParseException {
        PageResult pageResult = secondLevelDomainTopNService.nodeServerDetail(companySecondLevel);
        return ResponseEntity.ok(pageResult);
    }
}
