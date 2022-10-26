package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.entity.DynamicQueryTimeBO;
import com.yamu.data.sample.service.resources.entity.po.PartnerDomainTopNChange;
import com.yamu.data.sample.service.resources.entity.po.PartnerDomainTopNChangeDetail;
import com.yamu.data.sample.service.resources.mapper.PartnerDomainTopNChangeDetailMapper;
import com.yamu.data.sample.service.resources.mapper.PartnerDomainTopNChangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author yuyuan.Dong
 * @Date 2021/7/7
 * @DESC
 */
@Service
public class PartnerDomainTopNChangeService {

    @Autowired
    private PartnerDomainTopNChangeMapper partnerDomainTopNChangeMapper;

    @Autowired
    private PartnerDomainTopNChangeDetailMapper partnerDomainTopNChangeDetailMapper;

    private static final String RESOURCE_POPULAR_COMPANY_TOP_N_TABLE_PREFIX = "rpt_resource_company_topn";

    private static final String RESOURCE_POPULAR_COMPANY_DETAIL_TABLE_PREFIX = "rpt_resource_company_topn_detail";

    public PageResult findRankNumber(PartnerDomainTopNChange domainTopNChange) {
        checkFindRankNumberParam(domainTopNChange);
        String resultTable = DynamicQueryTimeBO.splitTime(domainTopNChange.getStartTime(), domainTopNChange.getEndTime(), RESOURCE_POPULAR_COMPANY_TOP_N_TABLE_PREFIX).toSqlString();
        Long total = partnerDomainTopNChangeMapper.countFindRankNumberByParam(domainTopNChange, resultTable);
        List<PartnerDomainTopNChange> dataList= partnerDomainTopNChangeMapper.findRankNumberByParam(domainTopNChange, resultTable);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkFindRankNumberParam(PartnerDomainTopNChange domainTopNChange) {
        if(StrUtil.isEmpty(domainTopNChange.getStartTime()) || StrUtil.isEmpty(domainTopNChange.getEndTime())) {
            domainTopNChange.setDefaultTime();
        }
        if(ObjectUtil.isEmpty(domainTopNChange.getRankNumber())) {
            domainTopNChange.setRankNumber(100L);
        }
    }

    public List<PartnerDomainTopNChange> downloadByParam(PartnerDomainTopNChange domainTopNChange) {
        checkDownloadByParamMethodParam(domainTopNChange);
        String resultTable = DynamicQueryTimeBO.splitTime(domainTopNChange.getStartTime(), domainTopNChange.getEndTime(), RESOURCE_POPULAR_COMPANY_TOP_N_TABLE_PREFIX).toSqlString();
        List<PartnerDomainTopNChange> dataList= partnerDomainTopNChangeMapper.findRankNumberByParam(domainTopNChange, resultTable);
        return dataList;
    }

    private void checkDownloadByParamMethodParam(PartnerDomainTopNChange domainTopNChange) {
        if(StrUtil.isEmpty(domainTopNChange.getStartTime()) || StrUtil.isEmpty(domainTopNChange.getEndTime())) {
            domainTopNChange.setDefaultTime();
        }
        domainTopNChange.setLimit(10000L);
        domainTopNChange.setOffset(0L);
    }

    public PageResult findDomainDetail(PartnerDomainTopNChangeDetail domainTopNChangeDetail) {
        checkFindDomainDetailParam(domainTopNChangeDetail);
        String resultTable = DynamicQueryTimeBO.splitTime(domainTopNChangeDetail.getStartTime(), domainTopNChangeDetail.getEndTime(), RESOURCE_POPULAR_COMPANY_DETAIL_TABLE_PREFIX).toSqlString();
        Long total = partnerDomainTopNChangeDetailMapper.countFindDomainDetailByParam(domainTopNChangeDetail, resultTable);
        List<PartnerDomainTopNChangeDetail> dataList = partnerDomainTopNChangeDetailMapper.findDomainDetailByParam(domainTopNChangeDetail, resultTable);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkFindDomainDetailParam(PartnerDomainTopNChangeDetail domainTopNChangeDetail) {
        if(StrUtil.isEmpty(domainTopNChangeDetail.getStartTime()) || StrUtil.isEmpty(domainTopNChangeDetail.getEndTime())) {
            domainTopNChangeDetail.setDefaultTime();
        }
    }
}
