package com.yamu.data.sample.service.resources.controller.popularDomain;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.ResourceDistributionProvinceVO;
import com.yamu.data.sample.service.resources.entity.vo.ResourceWebsiteUserSourceVO;
import com.yamu.data.sample.service.resources.service.ResourceWebsiteTopNDetailService;
import com.yamu.data.sample.service.resources.service.ResourceWebsiteTopNNetOutDetailService;
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

/**
 * @author lishuntao
 * Date 2020-07-1
 */
@RestController
@RequestMapping("/service/resource/websiteTopNDetail")
@Api(value = "TopN网站分析", tags = "TopN网站分析API")
public class ResourceWebsiteTopNDetailController {

    @Autowired
    private ResourceWebsiteTopNDetailService websiteTopNDetailService;

    @Autowired
    private ResourceWebsiteTopNNetOutDetailService websiteTopNNetOutDetailService;

    /**
     * topN网站排名分析
     * @param withinProvinceDomain
     * @return
     */
    @GetMapping("rankTrend/v1")
    public ResponseEntity findWithinProvinceDomainRank(ResourceWebsiteTopNDetail withinProvinceDomain) throws YamuException{
        return ResponseEntity.ok(websiteTopNDetailService.findwebsiteTopNRankTrend(withinProvinceDomain));
    }

    @GetMapping("tableList/v1")
    public ResponseEntity findTrendList(ResourceWebsiteTopNDetail websiteTopNDetail) throws YamuException {
        PageResult pageResult = websiteTopNDetailService.findTrendList(websiteTopNDetail);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("resourceReport/v1")
    public ResponseEntity findResourceReport(ResourceWebsiteTopNDetail websiteTopNDetail) throws YamuException, ParseException {
        JSONObject finalResult = websiteTopNDetailService.findResourceReport(websiteTopNDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("rateReport/v1")
    public ResponseEntity findRateReport(ResourceWebsiteTopNDetail websiteTopNDetail,boolean isTopN) throws YamuException, ParseException {
        JSONObject finalResult = websiteTopNDetailService.findRateReport(websiteTopNDetail,isTopN);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("parseReport/v1")
    public ResponseEntity findParseReport(ResourceWebsiteTopNDetail websiteTopNDetail) throws YamuException, ParseException {
        JSONObject finalResult = websiteTopNDetailService.findParseReport(websiteTopNDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("domainNetOut/v1")
    public ResponseEntity findDomainNetOut(ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail) throws YamuException {
        PageResult pageResult = websiteTopNNetOutDetailService.findDomainNetOut(websiteTopNNetOutDetail);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("domainNetOutDetail/v1")
    public ResponseEntity findDomainNetOutDetail(ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail) throws YamuException {
        PageResult pageResult = websiteTopNNetOutDetailService.findDomainNetOutDetail(websiteTopNNetOutDetail);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("domainNetOutIsp/v1")
    public ResponseEntity findIspOfDomainNetOut(ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail) throws YamuException {
        JSONObject finalResult = websiteTopNNetOutDetailService.findIspOfDomainNetOut(websiteTopNNetOutDetail);
        return ResponseEntity.ok(finalResult);
    }

//    @GetMapping("download/v1")
//    public void downloadRankNumber(ResourceWebsiteTopNDetail websiteTopNDetail, HttpServletResponse response) throws Exception {
//        List<ResourceWebsiteTopNDetail> dataList = websiteTopNDetailService.downloadByParam(websiteTopNDetail);
//        List<String> csvLines = dataList.stream().map(ResourceWebsiteTopNDetail::getCsvLineSting).collect(Collectors.toList());
//        CsvUtils.exportCsv(ResourceWebsiteTopNDetail.CSV_NAME, websiteTopNDetail.getStartTime(), websiteTopNDetail.getEndTime(), ResourceWebsiteTopNDetail.CSV_HEAD, csvLines, response);
//    }

    @GetMapping("findUserSource/v1")
    public ResponseEntity findUserSource(ResourceWebsiteUserSourceVO resourceWebsiteUserSourceVO){
        JSONObject finalResult = websiteTopNDetailService.findUserSource(resourceWebsiteUserSourceVO);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("findCdnReportAll/v1")
    @ApiOperation("cdn厂商汇总")
    public ResponseEntity findCdnReportAll(ResourceWebsiteTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail){
        JSONObject finalResult = websiteTopNDetailService.findCdnReportAll(resourceWebsiteTopNCdnBusinessDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("findCdnReport/v1")
    @ApiOperation("cdn厂商")
    public ResponseEntity findCdnReport(ResourceWebsiteTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail){
        JSONObject finalResult = websiteTopNDetailService.findCdnReport(resourceWebsiteTopNCdnBusinessDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("findCdnDetail/v1")
    @ApiOperation("cdn厂商明细")
    public ResponseEntity findCdnDetail(ResourceWebsiteTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail){
        PageResult resourceWebsiteDetail = websiteTopNDetailService.findCdnDetail(resourceWebsiteTopNCdnBusinessDetail);
        return ResponseEntity.ok(resourceWebsiteDetail);
    }

    @GetMapping("download/v1")
    public void download(ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail,ResourceWebsiteTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail, HttpServletResponse response) throws Exception {
        websiteTopNDetailService.download(websiteTopNNetOutDetail,resourceWebsiteTopNCdnBusinessDetail, response);
    }

    @GetMapping("downloadNoResource/v1")
    public void downloadNoResource(ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail, HttpServletResponse response) throws Exception {
        websiteTopNDetailService.downloadNoResource(websiteTopNNetOutDetail, response);
    }

    @GetMapping("resourceDistributionProvince/v1")
    public List<ResourceDistributionProvinceData> resourceDistributionProvince(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        List<ResourceDistributionProvinceData> list = websiteTopNDetailService.getResourceDistributionProvinceList(resourceDistributionProvinceVO);
        return list;
    }

    @GetMapping("findIpDetail/v1")
    public ResourceIpDetail findIpDetail(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        ResourceIpDetail resourceIpDetail = websiteTopNDetailService.findIpDetail(resourceDistributionProvinceVO);
        return resourceIpDetail;
    }

}
