package com.yamu.data.sample.service.resources.service;

import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.entity.DynamicQueryTimeBO;
import com.yamu.data.sample.service.resources.entity.po.ResourceCdnDistribution;
import com.yamu.data.sample.service.resources.mapper.ResourceCdnDistributionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xh.wu
 * @date 2021/10/20
 */
@Service
@Slf4j
public class ResourceCdnDistributionService {

    @Autowired
    private ResourceCdnDistributionMapper mapper;

    public PageResult queryCompany(ResourceCdnDistribution distribution) {
        String mainTable = DynamicQueryTimeBO.splitTime(distribution.getStartTime(), distribution.getEndTime(), ResourceCdnDistribution.MAIN_TABLE_PREFIX).toSqlString();
        String subTable = DynamicQueryTimeBO.splitTime(distribution.getStartTime(), distribution.getEndTime(), ResourceCdnDistribution.SUB_TABLE_PREFIX).toSqlString();
        distribution.setMainTable(mainTable);
        distribution.setSubTable(subTable);
        Long total = mapper.countCompany(distribution);
        total = total == null ? 0 : total;
        List<ResourceCdnDistribution> list = new ArrayList<>();
        if (total > 0) {
            list = mapper.queryCompany(distribution);
        }
        list = list == null ? new ArrayList<>() : list;
        list.forEach(ResourceCdnDistribution::buildRate);
        list.forEach(item -> item.setTimeRange(distribution.getStartTime() + "~" + distribution.getEndTime()));
        return new PageResult(total, list);
    }

    public PageResult queryDomain(ResourceCdnDistribution distribution) {
        String mainTable = DynamicQueryTimeBO.splitTime(distribution.getStartTime(), distribution.getEndTime(), ResourceCdnDistribution.MAIN_TABLE_PREFIX).toSqlString();
        String subTable = DynamicQueryTimeBO.splitTime(distribution.getStartTime(), distribution.getEndTime(), ResourceCdnDistribution.SUB_TABLE_PREFIX).toSqlString();
        distribution.setMainTable(mainTable);
        distribution.setSubTable(subTable);
        Long total = mapper.countDomainByCompany(distribution);
        total = total == null ? 0 : total;
        List<ResourceCdnDistribution> list = new ArrayList<>();
        if (total > 0) {
            list = mapper.queryDomainByCompany(distribution);
        }
        list = list == null ? new ArrayList<>() : list;
        list.forEach(ResourceCdnDistribution::buildRate);
        return new PageResult(total, list);
    }

    public List<ResourceCdnDistribution> queryDownload(ResourceCdnDistribution distribution) {
        String mainTable = DynamicQueryTimeBO.splitTime(distribution.getStartTime(), distribution.getEndTime(), ResourceCdnDistribution.MAIN_TABLE_PREFIX).toSqlString();
        String subTable = DynamicQueryTimeBO.splitTime(distribution.getStartTime(), distribution.getEndTime(), ResourceCdnDistribution.SUB_TABLE_PREFIX).toSqlString();
        distribution.setMainTable(mainTable);
        distribution.setSubTable(subTable);
        distribution.setOffset(0L);
        distribution.setLimit(10000L);
        List<ResourceCdnDistribution> list = mapper.queryDownload(distribution);
        return list == null ? new ArrayList<>() : list;
    }
}
