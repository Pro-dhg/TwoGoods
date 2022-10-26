package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.ObjectUtil;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.ResourceDomainTopn;
import com.yamu.data.sample.service.resources.mapper.ResourceDomainTopnMapper;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * @Author Lishuntao
 * @Date 2021/1/21
 */
@Service
public class ResourceDomainTopnService {

    @Autowired
    ResourceDomainTopnMapper mapper;

    //
    public List<ResourceDomainTopn> findAllGroupByIsp(ResourceDomainTopn queryParam) {
        List<ResourceDomainTopn> dataList = mapper.findAllGroupByIsp(queryParam);
        setUnknownLast(dataList);
        if (dataList == null) {
            return Lists.newArrayList();
        } else {
            return dataList;
        }
    }

    public List<ResourceDomainTopn> findAllGroupByParseTime(ResourceDomainTopn queryParam) {
        List<ResourceDomainTopn> dataList = mapper.findAllGroupByParseTime(queryParam);
        if (dataList == null) {
            return Lists.newArrayList();
        } else {
            return dataList;
        }
    }

    public List<ResourceDomainTopn> findAll(ResourceDomainTopn queryParam) {
        List<ResourceDomainTopn> dataList = mapper.findAll(queryParam);
        if (dataList == null) {
            return Lists.newArrayList();
        } else {
            return dataList;
        }
    }

    public PageResult findAllGroupByParseTimeByPage(ResourceDomainTopn queryParam) {
        PageResult pageResult= new PageResult();
        Long total = Long.valueOf("0");
        List<ResourceDomainTopn> page = org.apache.commons.compress.utils.Lists.newArrayList();
        if(ObjectUtil.equals(queryParam.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            total = Long.valueOf(1);
            page = mapper.findAllGroupByParseTimePageAll(queryParam);
            if (page.get(0).getParseTotalCnt()== BigInteger.ZERO){
                pageResult.setTotal(0);
                pageResult.setData(Lists.newArrayList());
                return pageResult;
            }
            page.stream().forEach(popularCompanyTrendParam -> {
                popularCompanyTrendParam.setTimeRange(queryParam.getStartTime() + "~" + queryParam.getEndTime());
            });
        }else{
            total = mapper.countGroupByParseTime(queryParam);
            if (total==0){
                pageResult.setTotal(0);
                pageResult.setData(Lists.newArrayList());
                return pageResult;
            }
            page = mapper.findAllGroupByParseTimePage(queryParam);
            page.stream().forEach(popularCompanyTrendParam -> {
                popularCompanyTrendParam.setTimeRange(DateUtils.formatDataToString(popularCompanyTrendParam.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        page.stream().forEach(ResourceDomainTopn::buildRate);
        pageResult.setTotal(total.intValue());
        pageResult.setData(page);
        return pageResult;
    }

    private void setUnknownLast(List<ResourceDomainTopn> dataList){
        for (ResourceDomainTopn resourceDomainTopn : dataList) {
            if("未知".equals(resourceDomainTopn.getAnswerFirstIsp())){
                ResourceDomainTopn data = resourceDomainTopn;
                dataList.remove(resourceDomainTopn);
                dataList.add(data);
                break;
            }
        }
    }
}
