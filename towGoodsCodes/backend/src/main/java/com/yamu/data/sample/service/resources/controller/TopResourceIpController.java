package com.yamu.data.sample.service.resources.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.result.PageResult;

import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.resources.entity.po.TopResourceIp;
import com.yamu.data.sample.service.resources.entity.po.TopResourceIpExcelData;
import com.yamu.data.sample.service.resources.service.TopResourceIpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author yuyuan.Dong
 * @Date 2022/2/16
 * @DESC
 */
@RestController
@RequestMapping("/service/resource/TopResourceIp")
@Api(value = "TopN资源IP", tags = "TopN资源IpAPI")
public class TopResourceIpController {

    @Autowired
    private TopResourceIpService resourceIpService;

    @GetMapping("parseReport/v1")
    @ApiOperation("TopN资源IP解析趋势")
    public ResponseEntity findParseReport(TopResourceIp resourceIp) throws Exception {
        JSONObject finalResult = resourceIpService.findTopIpTrend(resourceIp);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("tableDetail/v1")
    @ApiOperation("TopN资源IP解析明细")
    public ResponseEntity findTableDetail(TopResourceIp resourceIp) {
        PageResult pageResult = resourceIpService.findTableDetail(resourceIp);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("download/v1")
    @ApiOperation("TopN资源IP导出")
    public void downloadRankNumber(TopResourceIp resourceIp, HttpServletResponse response) throws Exception {
        List<TopResourceIpExcelData> dataList = resourceIpService.downloadByParam(resourceIp);
        String fileName = "TopN资源IP" + StrUtil.UNDERLINE + DateUtils.formatDataToString(new Date(),"yyyyMMddHHmm") + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();
        Map topNUserIpResult = Maps.newLinkedHashMap();
        topNUserIpResult.put("answerFirst", "资源IP地址");
        topNUserIpResult.put("answerFirstProvince", "省份");
        topNUserIpResult.put("answerFirstCity", "城市");
        topNUserIpResult.put("answerFirstIsp", "运营商");
        topNUserIpResult.put("parseTotalCnt", "解析次数");
        topNUserIpResult.put("rankNumber", "排行");
        topNUserIpResult.put("domainNameCount", "涉及域名个数");
        topNUserIpResult.put("websiteAppNameCount", "涉及网站/应用个数");
        writer.setHeaderAlias(topNUserIpResult);
        writer.renameSheet("TopN用户IP");
        writer.merge(8, "导出时间段   开始时间:"+resourceIp.getStartTime().substring(0,resourceIp.getStartTime().length()-3)
                +",结束时间:"+resourceIp.getEndTime().substring(0,resourceIp.getEndTime().length()-3));
        writer.write(dataList, true);
        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }
}
