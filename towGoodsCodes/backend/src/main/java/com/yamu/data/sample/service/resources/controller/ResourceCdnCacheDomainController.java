package com.yamu.data.sample.service.resources.controller;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.service.common.entity.PageResult;
import com.yamu.data.sample.service.resources.entity.bo.CdnTopNServiceParam;
import com.yamu.data.sample.service.resources.entity.po.ResourceCdnCacheCompany;
import com.yamu.data.sample.service.resources.entity.po.ResourceCdnCacheDomain;
import com.yamu.data.sample.service.resources.entity.vo.CdnTopNServiceCompanyList;
import com.yamu.data.sample.service.resources.entity.vo.CdnTopNServiceDomainList;
import com.yamu.data.sample.service.resources.service.CdnCacheDomainService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

/**
 * @author czl
 * Date 2020-07-16
 */
@RestController
@RequestMapping("/service/resource/resourceCdnCacheDomain")
public class ResourceCdnCacheDomainController {

    @Autowired
    private CdnCacheDomainService cdnCacheDomainService;

    /**
     * cdn趋势图表
     * @param parmer
     * @return
     * @throws Exception
     */
    @GetMapping("cdn/v1")
    public ResponseEntity cdnAnalysis(ResourceCdnCacheDomain parmer) throws Exception {
        return cdnCacheDomainService.cdnAnalysis(parmer);
    }
    /**
     * cache趋势图表
     * @param parmer
     * @return
     * @throws Exception
     */
    @GetMapping("cache/v1")
    public ResponseEntity cacheAnalysis(ResourceCdnCacheDomain parmer) throws Exception {
        return cdnCacheDomainService.cacheAnalysis(parmer);
    }

    /**
     * 详情表
     * @param parmer
     * @return
     * @throws YamuException
     */
    @GetMapping("cdnCacheDetail/v1")
    public ResponseEntity cdnCacheDetail(ResourceCdnCacheDomain parmer) throws YamuException {
        return ResponseEntity.ok(cdnCacheDomainService.cdnCacheDetail(parmer));
    }


    /**
     * 本网、本省趋势分析
     * @param parmer
     * @return
     * @throws YamuException
     */
    @GetMapping("rateReport/v1")
    public ResponseEntity findRateReport(ResourceCdnCacheDomain parmer) throws YamuException, ParseException {
        JSONObject finalResult = cdnCacheDomainService.findRateReport(parmer);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 解析次数趋势
     * @param parmer
     * @return
     * @throws YamuException
     */
    @GetMapping("parseReport/v1")
    public ResponseEntity parseReport(ResourceCdnCacheDomain parmer) throws YamuException, ParseException {
        JSONObject finalResult = cdnCacheDomainService.findParseReport(parmer);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * topN服务网站
     * @param parmer
     * @return
     * @throws YamuException
     */
    @GetMapping("topNServiceReport/v1")
    public ResponseEntity topNServiceReport(ResourceCdnCacheDomain parmer) throws YamuException, ParseException {
        JSONObject finalResult = cdnCacheDomainService.findtopNServiceReport(parmer);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * topN服务网站详情表
     * @param parmer
     * @return
     * @throws YamuException
     */
    @GetMapping("topNServiceDetail/v1")
    public ResponseEntity topNServiceDetail(ResourceCdnCacheDomain parmer) throws YamuException{
        return ResponseEntity.ok(cdnCacheDomainService.topNServiceDetail(parmer));
    }

    @GetMapping("download/v1")
    public void downloadRankNumber(ResourceCdnCacheDomain parmer, HttpServletResponse response) throws Exception {
        cdnCacheDomainService.download(parmer,response);
    }

    @GetMapping("topNServiceCompany")
    @ApiOperation("topN服务公司")
    public PageResult<CdnTopNServiceCompanyList> topNServiceCompany(CdnTopNServiceParam cdnTopNServiceParam){
        return cdnCacheDomainService.topNServiceCompany(cdnTopNServiceParam);
    }

    @GetMapping("topNServiceDomain")
    @ApiOperation("topN服务域名")
    public PageResult<CdnTopNServiceDomainList> topNServiceDomain(CdnTopNServiceParam cdnTopNServiceParam){
        return cdnCacheDomainService.topNServiceDomain(cdnTopNServiceParam);
    }

}
