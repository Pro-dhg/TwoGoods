package com.yamu.data.sample.service.resources.controller;


import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.service.common.entity.PageResult;
import com.yamu.data.sample.service.resources.entity.bo.CdnTopNServiceCompanyParam;
import com.yamu.data.sample.service.resources.entity.po.ResourceCdnCacheCompany;
import com.yamu.data.sample.service.resources.entity.vo.CdnBusinessTopNServiceCompanyList;
import com.yamu.data.sample.service.resources.entity.vo.CdnBusinessTopNServiceDomainList;
import com.yamu.data.sample.service.resources.service.ResourceCdnCacheCompanyService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

/**
 * @Author yuyuan.Dong
 * @Date 2021/7/20
 * @DESC
 */
@RestController
@RequestMapping("/service/resource/cdnCacheCompanyService")
public class ResourceCdnCacheCompanyController {

    @Autowired
    private ResourceCdnCacheCompanyService cdnCacheCompanyService;


    /**
     * cdn趋势图表
     * @param parmer
     * @return
     * @throws Exception
     */
    @GetMapping("cdn/v1")
    public ResponseEntity cdnAnalysis(ResourceCdnCacheCompany parmer) throws Exception {
        return cdnCacheCompanyService.cdnAnalysis(parmer);
    }
    /**
     * cache趋势图表
     * @param parmer
     * @return
     * @throws Exception
     */
    @GetMapping("cache/v1")
    public ResponseEntity cacheAnalysis(ResourceCdnCacheCompany parmer) throws Exception {
        return cdnCacheCompanyService.cacheAnalysis(parmer);
    }

    /**
     * 详情表
     * @param parmer
     * @return
     * @throws YamuException
     */
    @GetMapping("cdnCacheDetail/v1")
    public ResponseEntity cdnCacheDetail(ResourceCdnCacheCompany parmer) throws YamuException,ParseException{
        return ResponseEntity.ok(cdnCacheCompanyService.cdnCacheDetail(parmer));
    }


    /**
     * 本网、本省趋势分析
     * @param parmer
     * @return
     * @throws YamuException
     */
    @GetMapping("rateReport/v1")
    public ResponseEntity findRateReport(ResourceCdnCacheCompany parmer) throws YamuException, ParseException {
        JSONObject finalResult = cdnCacheCompanyService.findRateReport(parmer);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 解析次数趋势
     * @param parmer
     * @return
     * @throws YamuException
     */
    @GetMapping("parseReport/v1")
    public ResponseEntity parseReport(ResourceCdnCacheCompany parmer) throws YamuException, ParseException {
        JSONObject finalResult = cdnCacheCompanyService.findParseReport(parmer);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * topN服务网站
     * @param parmer
     * @return
     * @throws YamuException
     */
    @GetMapping("topNServiceReport/v1")
    public ResponseEntity topNServiceReport(ResourceCdnCacheCompany parmer) throws YamuException, ParseException {
        JSONObject finalResult = cdnCacheCompanyService.findtopNServiceReport(parmer);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * topN服务网站详情表
     * @param parmer
     * @return
     * @throws YamuException
     */
    @GetMapping("topNServiceDetail/v1")
    public ResponseEntity topNServiceDetail(ResourceCdnCacheCompany parmer) throws YamuException,ParseException{
        return ResponseEntity.ok(cdnCacheCompanyService.topNServiceDetail(parmer));
    }

    @GetMapping("download/v1")
    public void downloadRankNumber(ResourceCdnCacheCompany parmer, HttpServletResponse response) throws Exception {
        cdnCacheCompanyService.download(parmer,response);
    }

    @GetMapping("topNServiceCompany")
    @ApiOperation("topN服务公司")
    public PageResult<CdnBusinessTopNServiceCompanyList> topNServiceCompany(CdnTopNServiceCompanyParam cdnTopNServiceCompanyParam){
        return cdnCacheCompanyService.topNServiceCompany(cdnTopNServiceCompanyParam);
    }

    @GetMapping("topNServiceDomain")
    @ApiOperation("topN服务域名")
    public PageResult<CdnBusinessTopNServiceDomainList> topNServiceDomain(CdnTopNServiceCompanyParam cdnTopNServiceCompanyParam){
        return cdnCacheCompanyService.topNServiceDomain(cdnTopNServiceCompanyParam);
    }

}
