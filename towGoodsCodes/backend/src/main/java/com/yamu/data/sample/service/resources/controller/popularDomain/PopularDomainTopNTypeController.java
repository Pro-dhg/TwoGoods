package com.yamu.data.sample.service.resources.controller.popularDomain;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainTopNCdnBusinessDetail;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainTopNType;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainTopNTypeDetail;
import com.yamu.data.sample.service.resources.entity.po.ResourceWebsiteTopNCdnBusinessDetail;
import com.yamu.data.sample.service.resources.entity.vo.ResourceTopNTypeUserSourceVO;
import com.yamu.data.sample.service.resources.service.PopularDomainTopNTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yuyuan.Dong
 * @Date 2021/7/23
 * @DESC
 */
@RestController
@RequestMapping("/service/resource/popularDomainTopNType")
@Api(value = "域名TopN分类分析", tags = "域名TopN分类分析API")
public class PopularDomainTopNTypeController {


    @Autowired
    private PopularDomainTopNTypeService popularDomainTopNTypeService;

    @GetMapping("rankNum/v1")
    public ResponseEntity findRankNumber(PopularDomainTopNType domainNameWebsiteDetail) {
        JSONObject finalResult = popularDomainTopNTypeService.findRankNumber(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("tableDetail/v1")
    public ResponseEntity findTableDetail(PopularDomainTopNType domainNameWebsiteDetail) {
        PageResult pageResult = popularDomainTopNTypeService.findTableDetail(domainNameWebsiteDetail);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("rateReport/v1")
    public ResponseEntity findRateReport(PopularDomainTopNType domainNameWebsiteDetail, boolean isTopN) throws Exception {
        JSONObject finalResult = popularDomainTopNTypeService.findRateReport(domainNameWebsiteDetail,isTopN);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("resourceReport/v1")
    public ResponseEntity findResourceReport(PopularDomainTopNType domainNameWebsiteDetail) throws Exception {
        JSONObject finalResult = popularDomainTopNTypeService.findResourceReport(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("parseReport/v1")
    public ResponseEntity findParseReport(PopularDomainTopNType domainNameWebsiteDetail) throws Exception {
        JSONObject finalResult = popularDomainTopNTypeService.findParseReport(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("findCdnReport/v1")
    @ApiOperation("cdn厂商")
    public ResponseEntity findCdnReport(PopularDomainTopNCdnBusinessDetail domainNameWebsiteDetail){
        JSONObject finalResult = popularDomainTopNTypeService.findCdnReport(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("findCdnDetail/v1")
    @ApiOperation("cdn厂商明细")
    public ResponseEntity findCdnDetail(PopularDomainTopNCdnBusinessDetail domainNameWebsiteDetail){
        PageResult resourceWebsiteDetail = popularDomainTopNTypeService.findCdnDetail(domainNameWebsiteDetail);
        return ResponseEntity.ok(resourceWebsiteDetail);
    }

    @GetMapping("download/v1")
    public void downloadRankNumber(PopularDomainTopNType domainNameWebsiteDetail, HttpServletResponse response) throws Exception {
//        checkDownloadMethodParam(domainNameWebsiteDetail);
        List<PopularDomainTopNType> dataList = popularDomainTopNTypeService.downloadByParam(domainNameWebsiteDetail);
        dataList.stream().forEach(PopularDomainTopNType::buildRate);
        List<String> csvLines = dataList.stream().map(PopularDomainTopNType::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(PopularDomainTopNType.CSV_NAME, domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime(), PopularDomainTopNType.CSV_HEAD, csvLines, response);
    }

//    private void checkDownloadMethodParam(PopularDomainTopNType domainNameWebsiteDetail) throws UnsupportedEncodingException {
//        if(ObjectUtil.isNotEmpty(domainNameWebsiteDetail.getDomainType())) {
//            domainNameWebsiteDetail.setDomainType(URLDecoder.decode(domainNameWebsiteDetail.getDomainType(), "utf-8"));
//        }
//    }

    @GetMapping("domainNetOutIsp/v1")
    public ResponseEntity findOperator(PopularDomainTopNTypeDetail domainNameWebsiteTopNTypeDetail) {
        JSONObject finalResult = popularDomainTopNTypeService.findOperator(domainNameWebsiteTopNTypeDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("domainNetOut/v1")
    public ResponseEntity findOutDomainTable(PopularDomainTopNTypeDetail domainNameWebsiteTopNTypeDetail) {
        PageResult pageResult = popularDomainTopNTypeService.findOutDomainTable(domainNameWebsiteTopNTypeDetail);
        return ResponseEntity.ok(pageResult);
    }


    @GetMapping("domainNetOutDetail/v1")
    public ResponseEntity findOutDomainTableDetail(PopularDomainTopNTypeDetail domainNameWebsiteTopNTypeDetail) {
        PageResult pageResult = popularDomainTopNTypeService.findOutDomainTableDetail(domainNameWebsiteTopNTypeDetail);
        return ResponseEntity.ok(pageResult);
    }


    @GetMapping("downloadNetOut/v1")
    public void downloadNetOut(PopularDomainTopNTypeDetail domainNameWebsiteTopNTypeDetail,PopularDomainTopNCdnBusinessDetail domainNameWebsiteDetail, HttpServletResponse response) throws Exception {
        popularDomainTopNTypeService.download(domainNameWebsiteTopNTypeDetail,domainNameWebsiteDetail, response);
    }

    @GetMapping("findUserSource/v1")
    public ResponseEntity findUserSource(ResourceTopNTypeUserSourceVO resourceTopNTypeUserSourceVO){
        JSONObject finalResult = popularDomainTopNTypeService.findUserSource(resourceTopNTypeUserSourceVO);
        return ResponseEntity.ok(finalResult);
    }

}
