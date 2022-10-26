package com.yamu.data.sample.service.resources.controller.popularDomain;

import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.service.resources.entity.po.ResourceWebsiteTopN;
import com.yamu.data.sample.service.resources.service.ResourceWebsiteTopNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author dongyuyuan
 * Date 2020-07-1
 */
@RestController
@RequestMapping("/service/resource/websiteTopN")
public class ResourceWebsiteTopNController {

    @Autowired
    private ResourceWebsiteTopNService websiteTopNService;

    /**
     * topN网站排名分析
     * @param withinProvinceDomain
     * @return
     */
    @GetMapping("rankTrend/v1")
    public ResponseEntity findWithinProvinceDomainRank(ResourceWebsiteTopN withinProvinceDomain) throws YamuException {
        return ResponseEntity.ok(websiteTopNService.findwebsiteTopNRankTrend(withinProvinceDomain));
    }
}
