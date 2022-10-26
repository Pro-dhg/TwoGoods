package com.yamu.data.sample.service.resources.controller.popularDomain;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.resources.entity.po.WithinProvinceDomainDetail;
import com.yamu.data.sample.service.resources.service.WithinProvinceDomainDetailServince;
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
 * @author dongyuyuan
 * Date 2020-07-1
 */
@RestController
@RequestMapping("/service/resource/withinProvinceDomainDetail")
public class WithinProvinceDomainDetailController {

    @Autowired
    private WithinProvinceDomainDetailServince provinceDomainDetailService;

    /**
     * 本省域名排名趋势
     * @param withinProvinceDomain
     * @return
     */
    @GetMapping("rankTrend/v1")
    public ResponseEntity findWithinProvinceDomainRank(WithinProvinceDomainDetail withinProvinceDomain) throws YamuException{
        return ResponseEntity.ok(provinceDomainDetailService.findWithinProvinceDomainRank(withinProvinceDomain));
    }

    /**
     * 本省域名解析明细报表
     * @param provinceDomainDetail
     * @return
     */
    @GetMapping("tableList/v1")
    public ResponseEntity findTrendList(WithinProvinceDomainDetail provinceDomainDetail) throws YamuException {
        PageResult pageResult = provinceDomainDetailService.findTrendList(provinceDomainDetail);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 本省域名资源分布图表
     * @param provinceDomainDetail
     * @return
     */
    @GetMapping("resourceReport/v1")
    public ResponseEntity findResourceReport(WithinProvinceDomainDetail provinceDomainDetail) throws YamuException, ParseException {
        JSONObject finalResult = provinceDomainDetailService.findResourceReport(provinceDomainDetail);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 本省域名本网率、本省率趋势图表
     * @param provinceDomainDetail
     * @return
     */
    @GetMapping("rateReport/v1")
    public ResponseEntity findRateReport(WithinProvinceDomainDetail provinceDomainDetail,boolean isTopN) throws YamuException, ParseException {
    JSONObject finalResult = null;
        if (isTopN){
            finalResult = provinceDomainDetailService.findRateTopNReport(provinceDomainDetail);
        }else {
            finalResult = provinceDomainDetailService.findRateReport(provinceDomainDetail);
        }
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 本省域名CDN、IDC解析次数趋势图表
     * @param provinceDomainDetail
     * @return
     */
    @GetMapping("parseReport/v1")
    public ResponseEntity findParseReport(WithinProvinceDomainDetail provinceDomainDetail) throws YamuException, ParseException {
        JSONObject finalResult = provinceDomainDetailService.findParseReport(provinceDomainDetail);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 导出
     * @param provinceDomainDetail
     * @param response
     * @throws Exception
     */
    @GetMapping("download/v1")
    public void downloadRankNumber(WithinProvinceDomainDetail provinceDomainDetail, HttpServletResponse response) throws Exception {
        List<WithinProvinceDomainDetail> dataList = provinceDomainDetailService.downloadByParam(provinceDomainDetail);
        List<String> csvLines = dataList.stream().map(WithinProvinceDomainDetail::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(WithinProvinceDomainDetail.CSV_NAME,provinceDomainDetail.getStartTime(), provinceDomainDetail.getEndTime(), WithinProvinceDomainDetail.CSV_HEAD, csvLines, response);
    }
}
