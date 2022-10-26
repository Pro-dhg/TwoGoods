package com.yamu.data.sample.service.resources.controller.partnerDomain;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.resources.entity.po.PartnerDomainCnameFlow;
import com.yamu.data.sample.service.resources.service.PartnerDomainCnameFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author wanghe
 * @Date 2021/8/27
 * @DESC
 */
@Controller
@RequestMapping("/service/resource/partnerDomainCnameFlow")
public class PartnerDomainCnameFlowController {
    @Autowired
    private PartnerDomainCnameFlowService partnerDomainCnameFlowService;

    /**
     * 特定域名CNAME明细表.
     *
     * @param partnerDomainCnameFlow 特定域名流量分析po
     * @return
     */
    @GetMapping("findTableDetail/v1")
    public ResponseEntity findTableDetail(PartnerDomainCnameFlow partnerDomainCnameFlow) {
        PageResult pageResult = partnerDomainCnameFlowService.findTableDetail(partnerDomainCnameFlow);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询某域名所有cname及访问量.
     *
     * @param partnerDomainCnameFlow
     * @return
     */
    @GetMapping("/findCnameTableDetail/v1")
    public ResponseEntity findCnameTableDetail(PartnerDomainCnameFlow partnerDomainCnameFlow) {
        PageResult pageResult = partnerDomainCnameFlowService.findCnameTableDetail(partnerDomainCnameFlow);
        return ResponseEntity.ok(pageResult);
    }


    /**
     * 流量趋势.
     *
     * @param partnerDomainCnameFlow 特定域名流量分析po
     * @return
     */
    @GetMapping("flowReport/v1")
    public ResponseEntity findFlowReport(PartnerDomainCnameFlow partnerDomainCnameFlow) {
        JSONObject finalResult = partnerDomainCnameFlowService.findFlowReport(partnerDomainCnameFlow);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 本网率趋势.
     *
     * @param partnerDomainCnameFlow 特定域名流量分析po
     * @return
     */
    @GetMapping("netInReport/v1")
    public ResponseEntity findNetInReport(PartnerDomainCnameFlow partnerDomainCnameFlow) {

        JSONObject finalResult = partnerDomainCnameFlowService.findNetInReport(partnerDomainCnameFlow);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 下载接口.
     *
     * @param partnerDomainCnameFlow
     * @param response
     * @throws Exception
     */
    @GetMapping("download/v1")
    public void download(PartnerDomainCnameFlow partnerDomainCnameFlow, HttpServletResponse response) throws Exception {
        partnerDomainCnameFlowService.download(partnerDomainCnameFlow, response);
    }

    /**
     * 查询topn域名.
     *
     * @param partnerDomainCnameFlow
     * @return
     */
    @GetMapping("topDomain/v1")
    public ResponseEntity topDomain(PartnerDomainCnameFlow partnerDomainCnameFlow) {
        List<String> domainList = partnerDomainCnameFlowService.topDomain(partnerDomainCnameFlow);
        return ResponseEntity.ok(domainList);

    }

}
