package com.yamu.data.sample.service.resources.controller.partnerDomain;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.resources.entity.po.ResourceDistributionProvinceData;
import com.yamu.data.sample.service.resources.entity.po.ResourceIpDetail;
import com.yamu.data.sample.service.resources.entity.po.SpecificDomainWebsiteDetail;
import com.yamu.data.sample.service.resources.entity.po.SpecificDomainWebsiteNetOutDetail;
import com.yamu.data.sample.service.resources.entity.vo.ResourceDistributionProvinceVO;
import com.yamu.data.sample.service.resources.entity.vo.ResourceSpecificWebsiteUserSourceVO;
import com.yamu.data.sample.service.resources.service.SpecificDomainWebsiteNetOutDetailService;
import com.yamu.data.sample.service.resources.service.SpecificDomainWebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;

/**
 * @Author wanghe
 * @Date 2021/7/21
 * @DESC 特定域名TopN网站分析.
 * @DataBase
 */
@Controller
@RequestMapping("/service/resource/resourceSpecificDomainWebsite")
public class SpecificDomainWebsiteController {

    @Autowired
    private SpecificDomainWebsiteService specificDomainWebsiteService;

    @Autowired
    private SpecificDomainWebsiteNetOutDetailService websiteNetOutDetailService;

    /**
     * topn网站排名趋势.
     *
     * @param domainNameWebsiteDetail
     * @return
     */
    @GetMapping("rankNum/v1")
    public ResponseEntity findRankNumber(SpecificDomainWebsiteDetail domainNameWebsiteDetail) {
        JSONObject finalResult = specificDomainWebsiteService.findRankNumber(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 网站数据明细表.
     *
     * @param domainNameWebsiteDetail
     * @return
     */
    @GetMapping("tableDetail/v1")
    public ResponseEntity findTableDetail(SpecificDomainWebsiteDetail domainNameWebsiteDetail) {
        PageResult pageResult = specificDomainWebsiteService.findTableDetail(domainNameWebsiteDetail);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 本网本省趋势.
     *
     * @param domainNameWebsiteDetail
     * @return
     */
    @GetMapping("rateReport/v1")
    public ResponseEntity findRateReport(SpecificDomainWebsiteDetail domainNameWebsiteDetail, boolean isTopN) throws ParseException {
        JSONObject finalResult = specificDomainWebsiteService.findRateReport(domainNameWebsiteDetail, isTopN);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 资源分布.
     *
     * @param domainNameWebsiteDetail
     * @return
     * @throws ParseException
     */
    @GetMapping("resourceReport/v1")
    public ResponseEntity findResourceReport(SpecificDomainWebsiteDetail domainNameWebsiteDetail) throws ParseException {
        JSONObject finalResult = specificDomainWebsiteService.findResourceReport(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * CDN次数,IDC次数
     *
     * @param domainNameWebsiteDetail
     * @return
     * @throws ParseException
     */
    @GetMapping("parseReport/v1")
    public ResponseEntity findParseReport(SpecificDomainWebsiteDetail domainNameWebsiteDetail) throws ParseException {
        JSONObject finalResult = specificDomainWebsiteService.findParseReport(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

//    @GetMapping("download/v1")
//    public void downloadRankNumber(SpecificDomainWebsiteDetail domainNameWebsiteDetail, HttpServletResponse response) throws Exception {
//        List<SpecificDomainWebsiteDetail> dataList = specificDomainWebsiteService.downloadByParam(domainNameWebsiteDetail);
//        dataList.stream().forEach(SpecificDomainWebsiteDetail::buildRate);
//        List<String> csvLines = dataList.stream().map(SpecificDomainWebsiteDetail::getCsvLineSting).collect(Collectors.toList());
//        CsvUtils.exportCsv(SpecificDomainWebsiteDetail.CSV_NAME, domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime(), SpecificDomainWebsiteDetail.CSV_HEAD, csvLines, response);
//    }

    @GetMapping("domainNetOut/v1")
    public ResponseEntity findDomainNetOut(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) throws ParseException {
        PageResult pageResult = websiteNetOutDetailService.findDomainNetOut(websiteNetOutDetail);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("domainNetOutDetail/v1")
    public ResponseEntity findDomainNetOutDetail(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) throws ParseException {
        PageResult pageResult = websiteNetOutDetailService.findDomainNetOutDetail(websiteNetOutDetail);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("domainNetOutIsp/v1")
    public ResponseEntity findIspOfDomainNetOut(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) throws ParseException {
        JSONObject finalResult = websiteNetOutDetailService.findIspOfDomainNetOut(websiteNetOutDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("download/v1")
    public void download(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail, HttpServletResponse response) throws Exception {
        websiteNetOutDetailService.download(websiteNetOutDetail, response);
    }

    @GetMapping("findUserSource/v1")
    public ResponseEntity findUserSource(ResourceSpecificWebsiteUserSourceVO resourceSpecificWebsiteUserSourceVO){
        JSONObject finalResult = websiteNetOutDetailService.findUserSource(resourceSpecificWebsiteUserSourceVO);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("resourceDistributionProvince/v1")
    public ResponseEntity resourceDistributionProvince(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        List<ResourceDistributionProvinceData> list = websiteNetOutDetailService.getResourceDistributionProvinceList(resourceDistributionProvinceVO);
        return  ResponseEntity.ok(list);
    }

    @GetMapping("findIpDetail/v1")
    public ResponseEntity findIpDetail(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        ResourceIpDetail resourceIpDetail = websiteNetOutDetailService.findIpDetail(resourceDistributionProvinceVO);
        return ResponseEntity.ok(resourceIpDetail);
    }

}
