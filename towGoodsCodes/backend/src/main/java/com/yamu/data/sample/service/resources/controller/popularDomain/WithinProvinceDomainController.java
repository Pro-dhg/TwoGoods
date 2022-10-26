package com.yamu.data.sample.service.resources.controller.popularDomain;

import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.service.resources.service.WithinProvinceDomainService;
import com.yamu.data.sample.service.resources.entity.po.WithinProvinceDomain;
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
@RequestMapping("/service/resource/withinProvinceDomain")
public class WithinProvinceDomainController {

    @Autowired
    private WithinProvinceDomainService withinProvinceDomainService;

    /**
     * 本省域名排名趋势
     * @param withinProvinceDomain
     * @return
     */
    @GetMapping("rankTrend/v1")
    public ResponseEntity findWithinProvinceDomainRank(WithinProvinceDomain withinProvinceDomain) throws YamuException {
        return ResponseEntity.ok(withinProvinceDomainService.findWithinProvinceDomainRank(withinProvinceDomain));
    }
}
