package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;

import com.google.common.collect.Maps;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.resources.entity.bo.PartnerDomainPVBO;
import com.yamu.data.sample.service.resources.entity.po.PartnerDomainPV;
import com.yamu.data.sample.service.resources.mapper.ResourcePartnerDomainPVMapper;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
/**
 * @author getiejun
 * Date 2020-07-1
 */
@Service
public class ResourcePartnerDomainPVService {
    @Autowired
    private ResourcePartnerDomainPVMapper domainPVMapper;

    private static String ORDER_BY_PV="pv desc";

    public PageResult findByPage(PartnerDomainPV partnerDomainPV) {
        checkFindByPageMethodParam(partnerDomainPV);
        long total = domainPVMapper.countGroupByNetNameAndSiteNameAndTopNAndOrderByPV(partnerDomainPV);
        List<PartnerDomainPVBO> partnerDomainPVList = domainPVMapper.findBySelectiveGroupByNetNameAndSiteNameAndTopNAndOrderByPVAndPage(partnerDomainPV);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setData(partnerDomainPVList);
        return pageResult;
    }

    private void checkFindByPageMethodParam(PartnerDomainPV partnerDomainPV) {
        partnerDomainPV.setDefaultTime();
        if(ObjectUtil.isNotEmpty(partnerDomainPV)) {
            if(ObjectUtil.isNotEmpty(partnerDomainPV.getRankNumber()) && partnerDomainPV.getRankNumber() == 0) {
                partnerDomainPV.setRankNumber(null);
            }
        }
    }

    public Map<String, List<JSONObject>> downloadBySelective(PartnerDomainPV partnerDomainPV) {
        checkFindByPageMethodParam(partnerDomainPV);
        partnerDomainPV.setOffset(0L);
        partnerDomainPV.setLimit(10000L);
        List<PartnerDomainPVBO> partnerDomainPVList = domainPVMapper.downloadBySelective(partnerDomainPV);
        if(CollUtil.isEmpty(partnerDomainPVList)) {
            partnerDomainPVList = Lists.newArrayList();
            partnerDomainPVList.add(createDefaultPartnerDomainPVBO());
        }
        Map<String, List<JSONObject>> resultMap = Maps.newHashMap();
        if(CollUtil.isNotEmpty(partnerDomainPVList)) {
            partnerDomainPVList.stream().forEach(
                partnerDomain -> {
                    if (resultMap.containsKey(partnerDomain.getDomainType())) {
                        JSONObject jsonObject = convertPartnerDomainPVToExport(partnerDomain, resultMap.get(partnerDomain.getDomainType()).size() + 1);
                        resultMap.get(partnerDomain.getDomainType()).add(jsonObject);
                    } else {
                        JSONObject jsonObject = convertPartnerDomainPVToExport(partnerDomain, 1);
                        List<JSONObject> dataList = Lists.newArrayList();
                        dataList.add(jsonObject);
                        resultMap.put(partnerDomain.getDomainType(), dataList);
                    }
                });
        }
        return resultMap;
    }

    private PartnerDomainPVBO createDefaultPartnerDomainPVBO() {
        PartnerDomainPVBO partnerDomainPVBO = new PartnerDomainPVBO();
        partnerDomainPVBO.setDomainType("sheet");
        return partnerDomainPVBO;
    }

    private JSONObject convertPartnerDomainPVToJsonObjectType(PartnerDomainPVBO partnerDomain, int serialNumber) {
        JSONObject jsonObject = new JSONObject();
        if(partnerDomain.getDomainType().contains("全国")) {
            jsonObject = convertPartnerDomainPVToCountryType(partnerDomain, serialNumber);
        } else if(partnerDomain.getDomainType().contains("政府")) {
            jsonObject = convertPartnerDomainPVToGovernmentType(partnerDomain, serialNumber);
        } else if(partnerDomain.getDomainType().contains("重点")) {
            jsonObject = convertPartnerDomainPVToEmphasisType(partnerDomain, serialNumber);
        } else if(partnerDomain.getDomainType().contains("电子商务")) {
            jsonObject = convertPartnerDomainPVToBusinessType(partnerDomain, serialNumber);
        } else if(partnerDomain.getDomainType().contains("规模以上企业")) {
            jsonObject = convertPartnerDomainPVToCompanyType(partnerDomain, serialNumber);
        }
        return jsonObject;
    }

    private JSONObject convertPartnerDomainPVToExport(PartnerDomainPVBO partnerDomain, int serialNumber) {
        JSONObject jsonObject = new JSONObject();
        if(partnerDomain.getDomainType().equals("sheet")) {
            jsonObject.put("序号", null);
        } else {
            jsonObject.put("序号", serialNumber);
        }
        jsonObject.put("网站名称", partnerDomain.getSiteName());
        jsonObject.put("主体(单位/企业名称)", partnerDomain.getBusinessName());
        jsonObject.put("域名", partnerDomain.getDomainName());
        jsonObject.put("访问量", partnerDomain.getPv());
        return jsonObject;
    }

    private JSONObject convertPartnerDomainPVToEmphasisType(PartnerDomainPVBO partnerDomain, int serialNumber) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("序号", serialNumber);
        jsonObject.put("单位名称", partnerDomain.getSiteName());
        jsonObject.put("主体", partnerDomain.getBusinessName());
        jsonObject.put("域名", partnerDomain.getDomainName());
        jsonObject.put("访问量", partnerDomain.getPv());
        return jsonObject;
    }

    private JSONObject convertPartnerDomainPVToCompanyType(PartnerDomainPVBO partnerDomain, int serialNumber) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("序号", serialNumber);
        jsonObject.put("网站名称", partnerDomain.getSiteName());
        jsonObject.put("主体", partnerDomain.getBusinessName());
        jsonObject.put("域名", partnerDomain.getDomainName());
        jsonObject.put("访问量", partnerDomain.getPv());
        return jsonObject;
    }

    private JSONObject convertPartnerDomainPVToBusinessType(PartnerDomainPVBO partnerDomain, int serialNumber) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("序号", serialNumber);
        jsonObject.put("网站名称", partnerDomain.getSiteName());
        jsonObject.put("主体", partnerDomain.getBusinessName());
        jsonObject.put("域名", partnerDomain.getDomainName());
        jsonObject.put("访问量", partnerDomain.getPv());
        return jsonObject;
    }

    private JSONObject convertPartnerDomainPVToGovernmentType(PartnerDomainPVBO partnerDomain, int serialNumber) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("序号", serialNumber);
        jsonObject.put("单位名称", partnerDomain.getSiteName());
        jsonObject.put("网站主域名", partnerDomain.getDomainName());
        jsonObject.put("访问量", partnerDomain.getPv());
        return jsonObject;
    }

    private JSONObject convertPartnerDomainPVToCountryType(PartnerDomainPVBO partnerDomain, int serialNumber) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("序号", serialNumber);
        jsonObject.put("网站名称", partnerDomain.getSiteName());
        jsonObject.put("域名", partnerDomain.getDomainName());
        jsonObject.put("访问量", partnerDomain.getPv());
        return jsonObject;
    }

}
