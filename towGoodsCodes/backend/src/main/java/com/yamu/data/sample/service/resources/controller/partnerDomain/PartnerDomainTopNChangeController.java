package com.yamu.data.sample.service.resources.controller.partnerDomain;

import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.resources.entity.po.PartnerDomainTopNChange;
import com.yamu.data.sample.service.resources.entity.po.PartnerDomainTopNChangeDetail;
import com.yamu.data.sample.service.resources.entity.po.PopularCompanyTopN;
import com.yamu.data.sample.service.resources.service.PartnerDomainTopNChangeService;
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
 * @Date 2021/7/7
 * @DESC
 */

@RestController
@RequestMapping("/service/resource/domainTopNChange")
public class PartnerDomainTopNChangeController {

    @Autowired
    private PartnerDomainTopNChangeService domainTopNChangeService;

    @GetMapping("rankNum/v1")
    public ResponseEntity findRankNumber(PartnerDomainTopNChange domainTopNChange) {
        PageResult pageResult = domainTopNChangeService.findRankNumber(domainTopNChange);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("download/v1")
    public void downloadRankNumber(PartnerDomainTopNChange domainTopNChange, HttpServletResponse response) throws Exception {
//        checkDownloadMethodParam(domainTopNChange);
        List<PartnerDomainTopNChange> dataList = domainTopNChangeService.downloadByParam(domainTopNChange);
        List<String> csvLines = dataList.stream().map(PartnerDomainTopNChange::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(PopularCompanyTopN.CSV_NAME, domainTopNChange.getStartTime(), domainTopNChange.getEndTime(), PopularCompanyTopN.CSV_HEAD, csvLines, response);
    }

    @GetMapping("detail/v1")
    public ResponseEntity findRankNumber(PartnerDomainTopNChangeDetail domainTopNChangeDetail) {
        PageResult pageResult = domainTopNChangeService.findDomainDetail(domainTopNChangeDetail);
        return ResponseEntity.ok(pageResult);
    }

//    private void checkDownloadMethodParam(PartnerDomainTopNChange popularCompanyTopN) throws UnsupportedEncodingException {
//        if(ObjectUtil.isNotEmpty(popularCompanyTopN.getDomainName())) {
//            popularCompanyTopN.setDomainName(URLDecoder.decode(popularCompanyTopN.getDomainName(), "utf-8"));
//        }
//    }
}
