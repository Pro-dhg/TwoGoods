package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.StrUtil;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.common.utils.ValidationResult;
import com.yamu.data.sample.common.utils.ValidationUtils;
import com.yamu.data.sample.service.common.entity.DynamicQueryTimeBO;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.PopularCompanyTopN;
import com.yamu.data.sample.service.resources.entity.po.PopularCompanyTopNDetail;
import com.yamu.data.sample.service.resources.mapper.PopularCompanyDetailMapper;
import com.yamu.data.sample.service.resources.mapper.PopularCompanyTopNMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author getiejun
 * Date 2020-07-1
 */
@Service
@Slf4j
public class PopularCompanyTopNService {

    @Autowired
    private PopularCompanyTopNMapper popularCompanyTopNMapper;

    @Autowired
    private PopularCompanyDetailMapper popularCompanyDetailMapper;

    private static final String RESOURCE_POPULAR_COMPANY_TOP_N_TABLE_PREFIX = "rpt_resource_company_topn";

    private static final String RESOURCE_POPULAR_COMPANY_DETAIL_TABLE_PREFIX = "rpt_resource_company_topn_detail";

    public PageResult findRankNumber(PopularCompanyTopN popularCompanyTopN) throws YamuException {
        checkFindRankNumberParam(popularCompanyTopN);
        String resultTable = DynamicQueryTimeBO.splitTime(popularCompanyTopN.getStartTime(),
                popularCompanyTopN.getEndTime(),
                RESOURCE_POPULAR_COMPANY_TOP_N_TABLE_PREFIX).toSqlString();

        // 计算环比时间
        Map<String, String> timeToYOY = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(
                popularCompanyTopN.getStartTime(),
                popularCompanyTopN.getEndTime());

        String easierResultTable = DynamicQueryTimeBO.splitTime(timeToYOY.get("easierStart"),
                timeToYOY.get("easierEnd"),
                RESOURCE_POPULAR_COMPANY_TOP_N_TABLE_PREFIX).toSqlString();

        Long total = popularCompanyTopNMapper.countFindRankNumberByParam(
                popularCompanyTopN,
                resultTable,
                easierResultTable);

        List<PopularCompanyTopN> dataList = popularCompanyTopNMapper.findRankNumberByParam(
                popularCompanyTopN,
                resultTable,
                easierResultTable);

        return PageResult.buildPageResult(total, dataList);
    }

    private void checkFindRankNumberParam(PopularCompanyTopN popularCompanyTopN) throws YamuException {
        if (StrUtil.isEmpty(popularCompanyTopN.getStartTime()) || StrUtil.isEmpty(popularCompanyTopN.getEndTime())) {
            popularCompanyTopN.setDefaultTime();
        }
        ValidationResult validationResult = ValidationUtils.validateEntity(popularCompanyTopN);
        if (validationResult.isHasErrors()) {
            log.error(">>PopularCompanyTopNService checkFindRankNumberParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
    }

    public List<PopularCompanyTopN> downloadByParam(PopularCompanyTopN popularCompanyTopN) {
        checkDownloadByParamMethodParam(popularCompanyTopN);
        String resultTable = DynamicQueryTimeBO.splitTime(popularCompanyTopN.getStartTime(),
                popularCompanyTopN.getEndTime(),
                RESOURCE_POPULAR_COMPANY_TOP_N_TABLE_PREFIX).toSqlString();

        // 计算环比时间
        Map<String, String> timeToYOY = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(
                popularCompanyTopN.getStartTime(),
                popularCompanyTopN.getEndTime());

        String easierResultTable = DynamicQueryTimeBO.splitTime(timeToYOY.get("easierStart"),
                timeToYOY.get("easierEnd"),
                RESOURCE_POPULAR_COMPANY_TOP_N_TABLE_PREFIX).toSqlString();

        return popularCompanyTopNMapper.findRankNumberByParam(
                popularCompanyTopN,
                resultTable,
                easierResultTable);
    }

    private void checkDownloadByParamMethodParam(PopularCompanyTopN popularCompanyTopN) {
        if (StrUtil.isEmpty(popularCompanyTopN.getStartTime()) || StrUtil.isEmpty(popularCompanyTopN.getEndTime())) {
            popularCompanyTopN.setDefaultTime();
        }
        popularCompanyTopN.setLimit(10000L);
        popularCompanyTopN.setOffset(0L);
    }

    public PageResult findDomainDetail(PopularCompanyTopNDetail popularCompanyDetail) {
        checkFindDomainDetailParam(popularCompanyDetail);
        String resultTable = DynamicQueryTimeBO.splitTime(popularCompanyDetail.getStartTime(), popularCompanyDetail.getEndTime(), RESOURCE_POPULAR_COMPANY_DETAIL_TABLE_PREFIX).toSqlString();
        Long total = popularCompanyDetailMapper.countFindDomainDetailByParam(popularCompanyDetail, resultTable);
        List<PopularCompanyTopNDetail> dataList = popularCompanyDetailMapper.findDomainDetailByParam(popularCompanyDetail, resultTable);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkFindDomainDetailParam(PopularCompanyTopNDetail popularCompanyTopNDetail) {
        if (StrUtil.isEmpty(popularCompanyTopNDetail.getStartTime()) || StrUtil.isEmpty(popularCompanyTopNDetail.getEndTime())) {
            popularCompanyTopNDetail.setDefaultTime();
        }
    }
}
