package com.yamu.data.sample.service.resources.controller.partnerDomain;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.resources.entity.po.PartnerDomainFlow;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainFlow;
import com.yamu.data.sample.service.resources.service.PartnerDomainFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author wanghe
 * @Date 2021/8/18
 * @DESC 特定域名流量分析.
 */
@RestController
@RequestMapping("/service/resource/partnerDomainFlow")
public class PartnerDomainFlowController {

    @Autowired
    private PartnerDomainFlowService partnerDomainFlowService;

    /**
     * 明细表.
     *
     * @param partnerDomainFlow
     * @return
     */
    @GetMapping("tableDetail/v1")
    public ResponseEntity findTableDetail(PartnerDomainFlow partnerDomainFlow) {
        PageResult pageResult = partnerDomainFlowService.findTableDetail(partnerDomainFlow);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 流量趋势.
     *
     * @param partnerDomainFlow
     * @return
     */
    @GetMapping("flowReport/v1")
    public ResponseEntity findFlowReport(PartnerDomainFlow partnerDomainFlow) {
        JSONObject result = partnerDomainFlowService.findFlowReport(partnerDomainFlow);
        return ResponseEntity.ok(result);
    }

    /**
     * 本网率趋势.
     *
     * @param partnerDomainFlow
     * @return
     */
    @GetMapping("netInReport/v1")
    public ResponseEntity findNetInReport(PartnerDomainFlow partnerDomainFlow) {
        JSONObject finalResult = partnerDomainFlowService.findNetInReport(partnerDomainFlow);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 获取TopN域名
     * @param popularDomainFlow
     * @return
     */
    @GetMapping("topDomain/v1")
    public ResponseEntity findTopDomain(PartnerDomainFlow popularDomainFlow) {
        List<String> domainList = partnerDomainFlowService.findTopDomain(popularDomainFlow);
        return ResponseEntity.ok(domainList);
    }

    /**
     * 明细表下载.
     *
     * @param partnerDomainFlow
     * @param response
     * @throws Exception
     */
    @GetMapping("download/v1")
    public void downloadRankNumber(PartnerDomainFlow partnerDomainFlow, HttpServletResponse response) throws Exception {
        List<PartnerDomainFlow> dataList = partnerDomainFlowService.downloadByParam(partnerDomainFlow);
        dataList.stream().forEach(PartnerDomainFlow::buildRate);
        dataList.stream().forEach(PartnerDomainFlow::buildConvertFlow);
        List<String> csvLines = dataList.stream().map(PartnerDomainFlow::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(PopularDomainFlow.CSV_NAME, DateUtils.formatDate(partnerDomainFlow.getStartTime(), DateUtils.DEFAULT_DAY_FMT),
                DateUtils.formatDate(partnerDomainFlow.getEndTime(), DateUtils.DEFAULT_DAY_FMT), PopularDomainFlow.CSV_HEAD, csvLines, response);
    }
}
