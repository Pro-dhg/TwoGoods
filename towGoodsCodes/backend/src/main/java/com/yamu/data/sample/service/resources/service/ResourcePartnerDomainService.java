package com.yamu.data.sample.service.resources.service;

import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.resources.entity.po.ResourcePartnerDomain;
import com.yamu.data.sample.service.resources.mapper.ResourcePartnerDomainMapper;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Lishuntao
 * @Date 2021/1/21
 */
@Service
public class ResourcePartnerDomainService {

    @Autowired
    ResourcePartnerDomainMapper mapper;

    //
    public List<ResourcePartnerDomain> findAll(ResourcePartnerDomain queryParam) {
        List<ResourcePartnerDomain> dataList = mapper.findAll(queryParam);
        if (dataList == null) {
            return Lists.newArrayList();
        } else {
            return dataList;
        }
    }

    public PageResult findRate(ResourcePartnerDomain queryParam) {
        PageResult pageResult = new PageResult();
        Long total =  mapper.countAllGroupByParseTime(queryParam);
        if (total == null || total == 0) {
            pageResult.setTotal(0);
            pageResult.setData(Lists.newArrayList());
            return pageResult;
        }
        List<ResourcePartnerDomain> dataList = mapper.findGroupByParseTime(queryParam);
        dataList.stream().forEach(ResourcePartnerDomain::buildRate);
        pageResult.setTotal(total);
        pageResult.setData(dataList);
        return pageResult;
    }

    public List<ResourcePartnerDomain> findAllGroupByParseTime(ResourcePartnerDomain queryParam) {
        List<ResourcePartnerDomain> dataList = mapper.findAllGroupByParseTime(queryParam);
        if (dataList == null) {
            return Lists.newArrayList();
        } else {
            return dataList;
        }
    }

    public List<ResourcePartnerDomain> findAllGroupByIsp(ResourcePartnerDomain queryParam) {
        List<ResourcePartnerDomain> dataList = mapper.findAllGroupByIsp(queryParam);
        setUnknownLast(dataList);
        if (dataList == null) {
            return Lists.newArrayList();
        } else {
            return dataList;
        }
    }

    private void setUnknownLast(List<ResourcePartnerDomain> dataList){
        for (ResourcePartnerDomain resourcePartnerDomain : dataList) {
            if("未知".equals(resourcePartnerDomain.getIsp())){
                ResourcePartnerDomain data = resourcePartnerDomain;
                dataList.remove(resourcePartnerDomain);
                dataList.add(data);
                break;
            }
        }
    }
}
