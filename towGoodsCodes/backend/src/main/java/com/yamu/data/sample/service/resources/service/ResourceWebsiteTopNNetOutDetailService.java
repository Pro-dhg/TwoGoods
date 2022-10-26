package com.yamu.data.sample.service.resources.service;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.common.utils.ValidationResult;
import com.yamu.data.sample.common.utils.ValidationUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.ResourceWebsiteTopNNetOutDetail;
import com.yamu.data.sample.service.resources.mapper.ResourceWebsiteTopNNetOutDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author getiejun
 * Date 2020-10-28
 */
@Service
@Slf4j
public class ResourceWebsiteTopNNetOutDetailService {

    @Autowired
    private ResourceWebsiteTopNNetOutDetailMapper websiteTopNNetOutDetailMapper;

    private final String DEFAULT_INTERVAL_TYPE = "1d";

    private final String DEFAULT_QUERY_TYPE = "1h";

    public PageResult findDomainNetOut(ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail) throws YamuException {
        checkWebsiteTopNNetOutDetailParam(websiteTopNNetOutDetail);
        Long total = websiteTopNNetOutDetailMapper.countDomainNetOutList(websiteTopNNetOutDetail);
        List<ResourceWebsiteTopNNetOutDetail> dataList= websiteTopNNetOutDetailMapper.findDomainNetOutList(websiteTopNNetOutDetail);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    public JSONObject findIspOfDomainNetOut(ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail) throws YamuException {
        JSONObject jsonObject = new JSONObject();
        checkWebsiteTopNNetOutDetailParam(websiteTopNNetOutDetail);
        List<String> ispData = websiteTopNNetOutDetailMapper.findIspOfDomainNetOut(websiteTopNNetOutDetail);
        jsonObject.put("data", ispData);
        return jsonObject;
    }

    public PageResult findDomainNetOutDetail(ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail) throws YamuException {
        checkWebsiteTopNNetOutDetailParam(websiteTopNNetOutDetail);
        Long total = websiteTopNNetOutDetailMapper.countDomainNetOutDetailList(websiteTopNNetOutDetail);
        List<ResourceWebsiteTopNNetOutDetail> dataList= websiteTopNNetOutDetailMapper.findDomainNetOutDetailList(websiteTopNNetOutDetail);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkWebsiteTopNNetOutDetailParam(ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail) throws YamuException{
        websiteTopNNetOutDetail.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        String websiteAppName = websiteTopNNetOutDetail.getWebsiteAppName();
        websiteTopNNetOutDetail.setWebsiteAppName(ReportUtils.escapeChar(websiteAppName));
        ValidationResult validationResult = ValidationUtils.validateEntity(websiteTopNNetOutDetail);
        if(validationResult.isHasErrors()) {
            log.error(">>ResourceWebsiteTopNNetOutDetailService checkWebsiteTopNNetOutDetailParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
    }
}
