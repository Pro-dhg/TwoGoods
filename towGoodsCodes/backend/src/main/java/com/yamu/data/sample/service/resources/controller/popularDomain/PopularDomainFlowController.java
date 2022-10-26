package com.yamu.data.sample.service.resources.controller.popularDomain;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainFlow;
import com.yamu.data.sample.service.resources.service.PopularDomainFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author getiejun
 * @date 2021/8/11
 */
@RestController
@RequestMapping("/service/resource/popularDomainFlow")
public class PopularDomainFlowController {

    @Autowired
    private PopularDomainFlowService popularDomainFlowService;

    @GetMapping("tableDetail/v1")
    public ResponseEntity findTableDetail(PopularDomainFlow popularDomainFlow) {
        PageResult pageResult = popularDomainFlowService.findTableDetail(popularDomainFlow);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("flowReport/v1")
    public ResponseEntity findFlowReport(PopularDomainFlow popularDomainFlow) {
        JSONObject finalResult = popularDomainFlowService.findFlowReport(popularDomainFlow);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("netInReport/v1")
    public ResponseEntity findNetInReport(PopularDomainFlow popularDomainFlow) {
        JSONObject finalResult = popularDomainFlowService.findNetInReport(popularDomainFlow);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("topDomain/v1")
    public ResponseEntity findTopDomain(PopularDomainFlow popularDomainFlow) {
        List<String> domainList = popularDomainFlowService.findTopDomain(popularDomainFlow);
        return ResponseEntity.ok(domainList);
    }

    @GetMapping("download/v1")
    public void downloadRankNumber(PopularDomainFlow popularDomainFlow, HttpServletResponse response) throws Exception {
        List<PopularDomainFlow> dataList = popularDomainFlowService.downloadByParam(popularDomainFlow);
        dataList.stream().forEach(PopularDomainFlow::buildRate);
        dataList.stream().forEach(PopularDomainFlow::buildConvertFlow);
        List<String> csvLines = dataList.stream().map(PopularDomainFlow::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(PopularDomainFlow.CSV_NAME, DateUtils.formatDate(popularDomainFlow.getStartTime(), DateUtils.DEFAULT_DAY_FMT),
                DateUtils.formatDate(popularDomainFlow.getEndTime(), DateUtils.DEFAULT_DAY_FMT), PopularDomainFlow.CSV_HEAD, csvLines, response);
    }
}
