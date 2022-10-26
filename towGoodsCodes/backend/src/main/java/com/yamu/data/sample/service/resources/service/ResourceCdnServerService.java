package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.google.common.collect.Maps;
import com.yamu.data.sample.service.common.util.BusinessUtils;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.resources.entity.vo.*;
import com.yamu.data.sample.service.resources.mapper.ResourceCdnServerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author dys
 * @Date 2022/06/24
 */
@Service
public class ResourceCdnServerService {

    @Autowired
    ResourceCdnServerMapper resourceCdnServerMapper;

    public ResourceCdnServerListData dataList(ResourceCdnServerParam resourceCdnServerParam){
        String tableName = getTableName(resourceCdnServerParam.getQueryType());
        if(resourceCdnServerParam.getBusinessStr() !=null && !"".equals(resourceCdnServerParam.getBusinessStr())){
            List<String>  businessList = Arrays.asList(resourceCdnServerParam.getBusinessStr().split(","));
            resourceCdnServerParam.setBusinessList(businessList);
        }
        ResourceCdnServerListData resourceCdnServerListData = new ResourceCdnServerListData();
        Long total = resourceCdnServerMapper.getTotal(resourceCdnServerParam,tableName);
        List<ResourceCdnServerList> list = resourceCdnServerMapper.getList(resourceCdnServerParam,tableName);
        for (ResourceCdnServerList data : list) {
            data.setParseTime(resourceCdnServerParam.getStartTime() + "~" + resourceCdnServerParam.getEndTime());
        }
        resourceCdnServerListData.setTotal(total);
        resourceCdnServerListData.setData(list);
        return resourceCdnServerListData;
    }

    public List<ResourceCdnServerTrend> trend(ResourceCdnServerParam resourceCdnServerParam) throws ParseException {
        if(resourceCdnServerParam.getBusinessStr() !=null && !"".equals(resourceCdnServerParam.getBusinessStr())){
            List<String>  businessList = Arrays.asList(resourceCdnServerParam.getBusinessStr().split(","));
            resourceCdnServerParam.setBusinessList(businessList);
        }
        String tableName = getTableName(resourceCdnServerParam.getQueryType());
        List<ResourceCdnServerTrend> list = resourceCdnServerMapper.getTrend(resourceCdnServerParam,tableName);
        for (ResourceCdnServerTrend data : list) {
            data.setParseTime(BusinessUtils.formatTime(data.getParseTime(),resourceCdnServerParam.getQueryType()));
        }
        return list;
    }

    public List<ResourceCdnServerDispatchTrend> dispatchTrend(ResourceCdnServerParam resourceCdnServerParam) throws ParseException {
        if(resourceCdnServerParam.getBusinessStr() !=null && !"".equals(resourceCdnServerParam.getBusinessStr())){
            List<String>  businessList = Arrays.asList(resourceCdnServerParam.getBusinessStr().split(","));
            resourceCdnServerParam.setBusinessList(businessList);
        }
        String tableName = getTableName(resourceCdnServerParam.getQueryType());
        List<ResourceCdnServerDispatchTrend> list = resourceCdnServerMapper.getDispatchTrend(resourceCdnServerParam,tableName);
        for (ResourceCdnServerDispatchTrend data : list) {
            data.setParseTime(BusinessUtils.formatTime(data.getParseTime(),resourceCdnServerParam.getQueryType()));
        }
        return list;
    }

    public ResourceCdnServerDetailListData detailList(ResourceCdnServerParam resourceCdnServerParam){
        String tableName = getTableName(resourceCdnServerParam.getQueryType());
        ResourceCdnServerDetailListData resourceCdnServerDetailListData = new ResourceCdnServerDetailListData();
        Long total = resourceCdnServerMapper.getDetailListTotal(resourceCdnServerParam,tableName);
        List<ResourceCdnServerDetailList> list = resourceCdnServerMapper.getDetailList(resourceCdnServerParam,tableName);
        resourceCdnServerDetailListData.setTotal(total);
        resourceCdnServerDetailListData.setData(list);
        return resourceCdnServerDetailListData;
    }

    public void download(ResourceCdnServerParam resourceCdnServerParam, HttpServletResponse response) throws IOException, ParseException{
        resourceCdnServerParam.setOffset(0L);
        resourceCdnServerParam.setLimit(10000L);
        String fileName = "CDN厂商网内外节点分布" + StrUtil.UNDERLINE + DateUtils.formatDataToString(new Date(),"yyyyMMddHHmm") + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        List<ResourceCdnServerList> list = dataList(resourceCdnServerParam).getData();
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.merge(8, "导出时间段   开始时间:"+resourceCdnServerParam.getStartTime().substring(0,resourceCdnServerParam.getStartTime().length()-3)
                +",结束时间:"+resourceCdnServerParam.getEndTime().substring(0,resourceCdnServerParam.getEndTime().length()-3));
        writer.setHeaderAlias(getListHeaderAlias());
        writer.renameSheet("CDN厂商网内外节点分布");
        writer.setOnlyAlias(true);
        writer.write(list, true);

        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    private Map<String, String> getListHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("parseTime", "时间");
        aliasMapResult.put("business", "CDN厂商");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("netInParseTotalCnt", "网内次数");
        aliasMapResult.put("netOutParseTotalCnt", "网外次数");
        aliasMapResult.put("serverCnt", "资源节点个数");
        aliasMapResult.put("netInServerCnt", "网内资源节点个数");
        aliasMapResult.put("netOutServerCnt", "网外资源节点个数");
        return aliasMapResult;
    }

    private String getTableName(String queryType){
        return "rpt_resource_cdn_business_server_distribute_" + queryType;
    }
}
