package com.yamu.data.sample.service.resources.controller;

import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.resources.common.annotations.EscapeChars;
import com.yamu.data.sample.service.resources.entity.po.ResourceCdnDistribution;
import com.yamu.data.sample.service.resources.service.ResourceCdnDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * CDN资源分布分析
 * @author xh.wu
 * @date 2021/10/20
 */
@RestController
@RequestMapping("/service/resource/cdnDistribution")
public class ResourceCdnDistributionController {

    @Autowired
    private ResourceCdnDistributionService resourceCdnDistributionService;

    @GetMapping("/findCompany/v1")
    public ResponseEntity findCompany(@EscapeChars({"cname", "domainName", "cdnCompany"}) ResourceCdnDistribution param) {
        PageResult pageResult = resourceCdnDistributionService.queryCompany(param);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("/findDomain/v1")
    public ResponseEntity findDomain(@EscapeChars({"cname", "domainName", "cdnCompany"}) ResourceCdnDistribution param) {
        PageResult pageResult = resourceCdnDistributionService.queryDomain(param);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("/download/v1")
    public void download(@EscapeChars({"cname", "domainName", "cdnCompany"}) ResourceCdnDistribution param, HttpServletResponse response) throws Exception {
        List<ResourceCdnDistribution> list = resourceCdnDistributionService.queryDownload(param);
        list.forEach(item -> item.setTimeRange(param.getStartTime() + "~" + param.getEndTime()));
        list.forEach(ResourceCdnDistribution::buildRate);
        List<String> collect = list.stream().map(ResourceCdnDistribution::getCsvLineForTotal).collect(Collectors.toList());
        CsvUtils.exportCsv(ResourceCdnDistribution.CSV_NAME, param.getStartTime(), param.getEndTime(), ResourceCdnDistribution.CSV_HEAD, collect, response);
    }
}
