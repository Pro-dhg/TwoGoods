package com.yamu.data.sample.service.resources.controller.popularCompany;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.resources.entity.po.PopularCompanyTrend;
import com.yamu.data.sample.service.resources.service.PopularCompanyTrendService;
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
 * @author getiejun
 * Date 2020-07-1
 */
@RestController
@RequestMapping("/service/resource/popularCompanyTrend")
public class PopularCompanyTrendController {

    @Autowired
    private PopularCompanyTrendService popularCompanyTrendService;

    @GetMapping("tableDetail/v1")
    public ResponseEntity findTrendList(PopularCompanyTrend popularCompanyTrend) throws YamuException {
        PageResult pageResult = popularCompanyTrendService.findTrendList(popularCompanyTrend);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 热点公司资源分布图表.
     *
     * @param popularCompanyTrend
     * @param isCount
     * @return
     */
    @GetMapping("resourceReport/v1")
    public ResponseEntity findResourceReport(PopularCompanyTrend popularCompanyTrend, boolean isCount) throws ParseException {
        JSONObject finalResult = popularCompanyTrendService.findResourceReport(popularCompanyTrend, isCount);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("accessReport/v1")
    public ResponseEntity findAccessReport(PopularCompanyTrend popularCompanyTrend) throws ParseException {
        JSONObject finalResult = popularCompanyTrendService.findAccessReport(popularCompanyTrend);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("rateReport/v1")
    public ResponseEntity findRateReport(PopularCompanyTrend popularCompanyTrend) throws ParseException {
        JSONObject finalResult = popularCompanyTrendService.findRateReport(popularCompanyTrend);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("parseReport/v1")
    public ResponseEntity findParseReport(PopularCompanyTrend popularCompanyTrend) throws ParseException {
        JSONObject finalResult = popularCompanyTrendService.findParseReport(popularCompanyTrend);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("download/v1")
    public void downloadRankNumber(PopularCompanyTrend popularCompanyTrend, HttpServletResponse response) throws Exception {
        //checkDownloadMethodParam(popularCompanyTrend);
        List<PopularCompanyTrend> dataList = popularCompanyTrendService.downloadByParam(popularCompanyTrend);
        dataList.stream().forEach(PopularCompanyTrend::buildRate);
        List<String> csvLines = dataList.stream().map(PopularCompanyTrend::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(PopularCompanyTrend.CSV_NAME, popularCompanyTrend.getStartTime(), popularCompanyTrend.getEndTime(), PopularCompanyTrend.CSV_HEAD, csvLines, response);
    }

//    private void checkDownloadMethodParam(PopularCompanyTrend popularCompanyTrend) throws UnsupportedEncodingException {
//        if(ObjectUtil.isNotEmpty(popularCompanyTrend.getCompanyShortName())) {
//            popularCompanyTrend.setCompanyShortName(URLDecoder.decode(popularCompanyTrend.getCompanyShortName(), "utf-8"));
//        }
//    }


}
