package com.yamu.data.sample.service.resources.controller.popularDomain;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainCnameFlow;
import com.yamu.data.sample.service.resources.service.PopularDomainCnameFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author wanghe
 * @Date 2021/8/24
 * @DESC 热点域名CNAME流量分析.
 */
@Controller
@RequestMapping("/service/resource/popularDomainCnameFlow")
public class PopularDomainCnameFlowController {

    @Autowired
    private PopularDomainCnameFlowService popularDomainCnameFlowService;

    /**
     * 热点域名CNAME明细表.
     *
     * @param popularDomainCnameFlow
     * @return
     */
    @GetMapping("/findTableDetail/v1")
    public ResponseEntity findTableDetail(PopularDomainCnameFlow popularDomainCnameFlow) {
        PageResult pageResult = popularDomainCnameFlowService.findTableDetail(popularDomainCnameFlow);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询某域名所有cname及访问量.
     *
     * @param popularDomainCnameFlow
     * @return
     */
    @GetMapping("/findCnameTableDetail/v1")
    public ResponseEntity findCnameTableDetail(PopularDomainCnameFlow popularDomainCnameFlow) {
        PageResult pageResult = popularDomainCnameFlowService.findCnameTableDetail(popularDomainCnameFlow);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 流量趋势.
     *
     * @param popularDomainCnameFlow
     * @return
     */
    @GetMapping("/flowReport/v1")
    public ResponseEntity findFlowReport(PopularDomainCnameFlow popularDomainCnameFlow) {
        JSONObject finalResult = popularDomainCnameFlowService.findFlowReport(popularDomainCnameFlow);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 本网率趋势.
     *
     * @param popularDomainCnameFlow
     * @return
     */
    @GetMapping("/netInReport/v1")
    public ResponseEntity findNetInReport(PopularDomainCnameFlow popularDomainCnameFlow) {
        JSONObject finalResult = popularDomainCnameFlowService.findNetInReport(popularDomainCnameFlow);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 主表导出10000条,子表导出10000条相关域名别名数据.
     * 导出文件格式为.xls
     *
     * @param popularDomainCnameFlow
     * @param response
     * @throws Exception
     */
    @GetMapping("/download/v1")
    public void download(PopularDomainCnameFlow popularDomainCnameFlow, HttpServletResponse response) throws IOException {
        popularDomainCnameFlowService.download(popularDomainCnameFlow, response);
    }

    /**
     * 查询topn域名.
     *
     * @param popularDomainCnameFlow
     * @return
     */
    @GetMapping("topDomain/v1")
    public ResponseEntity topDomain(PopularDomainCnameFlow popularDomainCnameFlow) {
        List<String> domainList = popularDomainCnameFlowService.topDomain(popularDomainCnameFlow);
        return ResponseEntity.ok(domainList);
    }

}
