package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.StrUtil;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.entity.DynamicQueryTimeBO;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.FocusCompanyDetail;
import com.yamu.data.sample.service.resources.entity.po.FocusCompanyTopN;
import com.yamu.data.sample.service.resources.mapper.FocusCompanyTopNMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zhangyanping
 * Date 2020-07-1
 */
@Service
public class FocusCompanyTopNService {

    @Autowired
    private FocusCompanyTopNMapper focusCompanyTopNMapper;

    private static final String RECURSION_PARSE_TOP_N_TABLE_PREFIX = "rpt_resource_focus_company_topn";

    private static final String RECURSION_PARSE_DETAIL_TABLE_PREFIX = "rpt_resource_focus_company_topn_detail";


    /**
     * 重点公司排名TopN报表
     * @param focusCompanyTopN
     * @return
     */
    public PageResult findRankNumber(FocusCompanyTopN focusCompanyTopN) {
        checkFindRankNumberParam(focusCompanyTopN);
        String resultTable = DynamicQueryTimeBO.splitTime(focusCompanyTopN.getStartTime(), focusCompanyTopN.getEndTime(), RECURSION_PARSE_TOP_N_TABLE_PREFIX).toSqlString();

        // 计算环比时间
        Map<String, String> timeToYOY = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(focusCompanyTopN.getStartTime(), focusCompanyTopN.getEndTime());
        String easierResultTable = DynamicQueryTimeBO.splitTime(timeToYOY.get("easierStart"), timeToYOY.get("easierEnd"), RECURSION_PARSE_TOP_N_TABLE_PREFIX).toSqlString();

        Long total = focusCompanyTopNMapper.countFindRankNumberByParam(focusCompanyTopN, resultTable, easierResultTable);
        List<FocusCompanyTopN> dataList = focusCompanyTopNMapper.findRankNumberByParam(focusCompanyTopN, resultTable, easierResultTable);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkFindRankNumberParam(FocusCompanyTopN focusCompanyTopN) {
        if (StrUtil.isEmpty(focusCompanyTopN.getStartTime()) || StrUtil.isEmpty(focusCompanyTopN.getEndTime())) {
            focusCompanyTopN.setDefaultTime();
        }
    }

    public List<FocusCompanyTopN> downloadByParam(FocusCompanyTopN focusCompanyTopN) {
        checkDownloadByParamMethodParam(focusCompanyTopN);
        String resultTable = DynamicQueryTimeBO.splitTime(focusCompanyTopN.getStartTime(),
                focusCompanyTopN.getEndTime(), RECURSION_PARSE_TOP_N_TABLE_PREFIX).toSqlString();
        // 计算环比时间
        Map<String, String> timeToYOY = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(
                focusCompanyTopN.getStartTime(), focusCompanyTopN.getEndTime());

        String easierResultTable = DynamicQueryTimeBO.splitTime(timeToYOY.get("easierStart"),
                timeToYOY.get("easierEnd"), RECURSION_PARSE_TOP_N_TABLE_PREFIX).toSqlString();
        // 查询
        List<FocusCompanyTopN> dataList = focusCompanyTopNMapper.findRankNumberByParam(focusCompanyTopN,
                resultTable, easierResultTable);
        return dataList;
    }

    private void checkDownloadByParamMethodParam(FocusCompanyTopN focusCompanyTopN) {
        if (StrUtil.isEmpty(focusCompanyTopN.getStartTime()) || StrUtil.isEmpty(focusCompanyTopN.getEndTime())) {
            focusCompanyTopN.setDefaultTime();
        }
        focusCompanyTopN.setLimit(10000L);
        focusCompanyTopN.setOffset(0L);
    }

    /**
     * 重点公司排名公司详细信息
     *
     * @param focusCompanyDetail
     * @return
     */
    public PageResult findDetail(FocusCompanyDetail focusCompanyDetail) {
        checkFindDetailParam(focusCompanyDetail);
        String resultTable = DynamicQueryTimeBO.splitTime(focusCompanyDetail.getStartTime(), focusCompanyDetail.getEndTime(), RECURSION_PARSE_DETAIL_TABLE_PREFIX).toSqlString();
        Long total = focusCompanyTopNMapper.countFindDetailByParam(focusCompanyDetail, resultTable);
        List<FocusCompanyDetail> dataList = focusCompanyTopNMapper.findDetailByParam(focusCompanyDetail, resultTable);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }
    private void checkFindDetailParam(FocusCompanyDetail focusCompanyDetail) {
        if (StrUtil.isEmpty(focusCompanyDetail.getStartTime()) || StrUtil.isEmpty(focusCompanyDetail.getEndTime())) {
            focusCompanyDetail.setDefaultTime();
        }
    }

}
