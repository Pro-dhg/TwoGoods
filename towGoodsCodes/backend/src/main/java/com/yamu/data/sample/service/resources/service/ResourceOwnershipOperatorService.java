package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.resources.entity.bo.*;
import com.yamu.data.sample.service.resources.entity.po.ResourceDistributionProvinceData;
import com.yamu.data.sample.service.resources.entity.po.ResourceDistributionProvinceDetail;
import com.yamu.data.sample.service.resources.entity.vo.ResourceOwnershipOperatorVO;
import com.yamu.data.sample.service.resources.mapper.ResourceOwnershipOperatorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author dys
 * @Date 2022/3/15
 */
@Service
public class ResourceOwnershipOperatorService extends BaseService{

    @Autowired
    private ResourceOwnershipOperatorMapper resourceOwnershipOperatorMapper;

    public OwnershipOperatorDataBO dataList(ResourceOwnershipOperatorVO resourceOwnershipOperatorVO){
        resourceOwnershipOperatorVO = getAbroad(resourceOwnershipOperatorVO);
        OwnershipOperatorDataBO ownershipOperatorDataBO = new OwnershipOperatorDataBO();
        String queryTable = getQueryTableStr(resourceOwnershipOperatorVO.getTopNType(),resourceOwnershipOperatorVO.getQueryType());
        Long total = resourceOwnershipOperatorMapper.getDataListTotal(resourceOwnershipOperatorVO,queryTable);
        List<OwnershipOperatorListBO> list = resourceOwnershipOperatorMapper.getDataList(resourceOwnershipOperatorVO,queryTable);
        for (OwnershipOperatorListBO ownershipOperatorListBO : list) {
            ownershipOperatorListBO.setTime(resourceOwnershipOperatorVO.getStartTime() + "~" + resourceOwnershipOperatorVO.getEndTime());
        }
        ownershipOperatorDataBO.setTotal(total);
        ownershipOperatorDataBO.setData(list);
        return ownershipOperatorDataBO;
    }

    public List<OperatorProportionBO> operatorProportion(ResourceOwnershipOperatorVO resourceOwnershipOperatorVO){
        resourceOwnershipOperatorVO = getAbroad(resourceOwnershipOperatorVO);
        String queryTable = getQueryTableStr(resourceOwnershipOperatorVO.getTopNType(),resourceOwnershipOperatorVO.getQueryType());
        List<OperatorProportionBO> list = new ArrayList<>();
        if(!"ownership".equals(resourceOwnershipOperatorVO.getStatistics())){
            list = resourceOwnershipOperatorMapper.getOperatorProportion(resourceOwnershipOperatorVO,queryTable);
        }
        return list;
    }

    public List<ResourceDistributionProvinceData> ownershipDistribution(ResourceOwnershipOperatorVO resourceOwnershipOperatorVO){
        resourceOwnershipOperatorVO = getAbroad(resourceOwnershipOperatorVO);
        List<ResourceDistributionProvinceData> list = new ArrayList<>();
        if(!"isp".equals(resourceOwnershipOperatorVO.getStatistics())){
            String queryTable = getQueryTableStr(resourceOwnershipOperatorVO.getTopNType(),resourceOwnershipOperatorVO.getQueryType());
            list = resourceOwnershipOperatorMapper.resourceDistributionProvince(resourceOwnershipOperatorVO,queryTable);
            List<ResourceDistributionProvinceDetail> detailList = resourceOwnershipOperatorMapper.resourceDistributionProvinceDetail(resourceOwnershipOperatorVO,queryTable);
            for (ResourceDistributionProvinceData resourceDistributionProvinceData : list) {
                List<ResourceDistributionProvinceDetail> dataList = new ArrayList<>();
                for (ResourceDistributionProvinceDetail resourceDistributionProvinceDetail : detailList) {
                    if(resourceDistributionProvinceData.getAnswerFirstProvince().equals(resourceDistributionProvinceDetail.getAnswerFirstProvince())){
                        dataList.add(resourceDistributionProvinceDetail);
                    }
                }
                resourceDistributionProvinceData.setData(dataList);
            }
        }
        return list;
    }

    public void download(ResourceOwnershipOperatorVO resourceOwnershipOperatorVO, HttpServletResponse response) throws IOException{
        resourceOwnershipOperatorVO = getAbroad(resourceOwnershipOperatorVO);
        resourceOwnershipOperatorVO.setOffset(0L);
        resourceOwnershipOperatorVO.setLimit(10000L);
        String fileName = "归属分布分析" + StrUtil.UNDERLINE + DateUtils.formatDataToString(new Date(),"yyyyMMddHHmm") + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        String queryTable = getQueryTableStr(resourceOwnershipOperatorVO.getTopNType(),resourceOwnershipOperatorVO.getQueryType());
        List<OwnershipOperatorListBO> list = resourceOwnershipOperatorMapper.getDataList(resourceOwnershipOperatorVO,queryTable);
        for (OwnershipOperatorListBO ownershipOperatorListBO : list) {
            ownershipOperatorListBO.setTime(resourceOwnershipOperatorVO.getStartTime() + "~" + resourceOwnershipOperatorVO.getEndTime());
        }
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.merge(8, "导出时间段   开始时间:"+resourceOwnershipOperatorVO.getStartTime().substring(0,resourceOwnershipOperatorVO.getStartTime().length()-3)
                +",结束时间:"+resourceOwnershipOperatorVO.getEndTime().substring(0,resourceOwnershipOperatorVO.getEndTime().length()-3));
        writer.addHeaderAlias("time", "时间段");
        if("isp".equals(resourceOwnershipOperatorVO.getStatistics()) || "oAndIsp".equals(resourceOwnershipOperatorVO.getStatistics())){
            writer.addHeaderAlias("answerFirstIsp", "运营商");
        }
        if("ownership".equals(resourceOwnershipOperatorVO.getStatistics()) || "oAndIsp".equals(resourceOwnershipOperatorVO.getStatistics())){
            writer.addHeaderAlias("answerFirstProvince", "省份");
            writer.addHeaderAlias("answerFirstCity", "城市");
        }
        writer.addHeaderAlias("parseTotalCnt", "解析次数");
        if(resourceOwnershipOperatorVO.getNetType() != null && "ipv4".equals(resourceOwnershipOperatorVO.getNetType())){
            writer.addHeaderAlias("v4Cnt", "IPv4解析次数");
        }else if(resourceOwnershipOperatorVO.getNetType() != null && "ipv6".equals(resourceOwnershipOperatorVO.getNetType())){
            writer.addHeaderAlias("v6Cnt", "IPv6解析次数");
        }else{
            writer.addHeaderAlias("v4Cnt", "IPv4解析次数");
            writer.addHeaderAlias("v6Cnt", "IPv6解析次数");
        }
        writer.addHeaderAlias("parseSuccessCnt", "成功次数");
        writer.addHeaderAlias("parseFailCnt", "失败次数");
        writer.renameSheet("归属分布分析");
        writer.setOnlyAlias(true);
        writer.write(list, true);
        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    private String getQueryTableStr(String topNType,String queryType){
        String tableName = "";
        if("域名".equals(topNType) || "公司".equals(topNType)){
            tableName = "rpt_resource_domain_topn_isp_";
        }else{
            tableName = "rpt_resource_website_topn_isp_";
        }
        String queryTable = tableName + queryType;
        return queryTable;
    }

    private ResourceOwnershipOperatorVO getAbroad(ResourceOwnershipOperatorVO resourceOwnershipOperatorVO){
        List<String> list = resourceOwnershipOperatorVO.getProvinceList();
        boolean abroadFlag = false;
        if(list != null && list.size() > 0){
            for (String abroadStr : list) {
                if("境外".equals(abroadStr)){
                    abroadFlag = true;
                    list.remove(abroadStr);
                    break;
                }
            }
        }
        resourceOwnershipOperatorVO.setProvinceList(list);
        resourceOwnershipOperatorVO.setAbroadFlag(abroadFlag);
        return resourceOwnershipOperatorVO;
    }
}
