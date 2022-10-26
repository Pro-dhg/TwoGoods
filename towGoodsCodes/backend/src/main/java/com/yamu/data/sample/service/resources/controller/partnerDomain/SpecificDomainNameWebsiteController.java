package com.yamu.data.sample.service.resources.controller.partnerDomain;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.resources.entity.po.SpecificDomainNameWebsiteDetail;
import com.yamu.data.sample.service.resources.entity.po.SpecificDomainWebsiteNetOutDetail;
import com.yamu.data.sample.service.resources.entity.vo.ResourceSpecificTopNTypeUserSourceVO;
import com.yamu.data.sample.service.resources.service.SpecificDomainNameWebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author getiejun
 * @date 2021/7/21
 */
@RestController
@RequestMapping("/service/resource/specificDomainTopNType")
public class SpecificDomainNameWebsiteController {

    @Autowired
    private SpecificDomainNameWebsiteService specificDomainNameWebsiteService;

    @GetMapping("rankNum/v1")
    public ResponseEntity findRankNumber(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) {
        JSONObject finalResult = specificDomainNameWebsiteService.findRankNumber(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("tableDetail/v1")
    public ResponseEntity findTableDetail(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) {
        PageResult pageResult = specificDomainNameWebsiteService.findTableDetail(domainNameWebsiteDetail);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 网站分类,本省本网趋势.
     *
     * @param domainNameWebsiteDetail
     * @param isTopN
     * @return
     * @throws Exception
     */
    @GetMapping("rateReport/v1")
    public ResponseEntity findRateReport(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail, boolean isTopN) throws Exception {
        JSONObject finalResult = specificDomainNameWebsiteService.findRateReport(domainNameWebsiteDetail, isTopN);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 资源分布.
     *
     * @param domainNameWebsiteDetail
     * @return
     * @throws Exception
     */
    @GetMapping("resourceReport/v1")
    public ResponseEntity findResourceReport(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) throws Exception {
        JSONObject finalResult = specificDomainNameWebsiteService.findResourceReport(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * CDN次数,IDC次数.
     *
     * @param domainNameWebsiteDetail
     * @return
     * @throws Exception
     */
    @GetMapping("parseReport/v1")
    public ResponseEntity findParseReport(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) throws Exception {
        JSONObject finalResult = specificDomainNameWebsiteService.findParseReport(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("download/v1")
    public void downloadRankNumber(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail, HttpServletResponse response) throws Exception {
//        checkDownloadMethodParam(domainNameWebsiteDetail);
        List<SpecificDomainNameWebsiteDetail> dataList = specificDomainNameWebsiteService.downloadByParam(domainNameWebsiteDetail);
        dataList.forEach(SpecificDomainNameWebsiteDetail::buildRate);
        List<String> csvLines = dataList.stream().map(SpecificDomainNameWebsiteDetail::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(SpecificDomainNameWebsiteDetail.CSV_NAME, domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime(), SpecificDomainNameWebsiteDetail.CSV_HEAD, csvLines, response);
    }

//    private void checkDownloadMethodParam(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) throws UnsupportedEncodingException {
//        if (ObjectUtil.isNotEmpty(domainNameWebsiteDetail.getDomainType())) {
//            domainNameWebsiteDetail.setDomainType(URLDecoder.decode(domainNameWebsiteDetail.getDomainType(), "utf-8"));
//        }
//    }

    @GetMapping("domainNetOutIsp/v1")
    public ResponseEntity findIspOfDomainNetOut(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) {
        JSONObject finalResult = specificDomainNameWebsiteService.findIspOfDomainNetOut(websiteNetOutDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("domainNetOut/v1")
    public ResponseEntity findDomainNetOut(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) {
        PageResult pageResult = specificDomainNameWebsiteService.findDomainNetOut(websiteNetOutDetail);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("domainNetOutDetail/v1")
    public ResponseEntity findDomainNetOutDetail(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail){
        PageResult pageResult = specificDomainNameWebsiteService.findDomainNetOutDetail(websiteNetOutDetail);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("downloadNetOut/v1")
    public void downloadNetOut(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail, HttpServletResponse response) throws Exception {
        specificDomainNameWebsiteService.download(websiteNetOutDetail, response);
    }

    @GetMapping("findUserSource/v1")
    public ResponseEntity findUserSource(ResourceSpecificTopNTypeUserSourceVO resourceSpecificTopNTypeUserSourceVO){
        JSONObject finalResult = specificDomainNameWebsiteService.findUserSource(resourceSpecificTopNTypeUserSourceVO);
        return ResponseEntity.ok(finalResult);
    }

}
