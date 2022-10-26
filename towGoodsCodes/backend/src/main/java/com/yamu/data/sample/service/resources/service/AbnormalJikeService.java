package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.google.common.collect.Maps;
import com.yamu.data.sample.service.common.entity.PageResult;
import com.yamu.data.sample.service.common.util.BusinessUtils;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.bo.AbnormalJikeParam;
import com.yamu.data.sample.service.resources.entity.vo.AbnormalJikeList;
import com.yamu.data.sample.service.resources.mapper.AbnormalJikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author dys
 * @Date 2022/09/22
 */
@Service
public class AbnormalJikeService {

    @Autowired
    AbnormalJikeMapper abnormalJikeMapper;

    public PageResult<AbnormalJikeList> list(AbnormalJikeParam abnormalJikeParam){
        String tableName = getTableName(abnormalJikeParam.getQueryType());
        Long total = abnormalJikeMapper.getTotal(abnormalJikeParam,tableName);
        List<AbnormalJikeList> list = getList(abnormalJikeParam);
        return new PageResult<>(total, list);
    }

    private List<AbnormalJikeList> getList(AbnormalJikeParam abnormalJikeParam){
        String tableName = getTableName(abnormalJikeParam.getQueryType());
        AbnormalJikeList sumData = abnormalJikeMapper.getSumData(abnormalJikeParam,tableName);
        List<AbnormalJikeList> list = abnormalJikeMapper.getList(abnormalJikeParam,tableName);
        for(AbnormalJikeList data : list){
            data.setParseTotalCntRate(BusinessUtils.convertDoubleToPercent(
                    ReportUtils.buildRatioBase(data.getParseTotalCnt(), sumData.getParseTotalCnt()),2));
            data.setParseTime(abnormalJikeParam.getStartTime() + "~" + abnormalJikeParam.getEndTime());
        }
        return list;
    }

    public void download(AbnormalJikeParam abnormalJikeParam, HttpServletResponse response) throws IOException {
        abnormalJikeParam.setOffset(0L);
        abnormalJikeParam.setLimit(10000L);
        String fileName = "异常集客挖掘" + StrUtil.UNDERLINE + DateUtils.formatDataToString(new Date(),"yyyyMMddHHmm") + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        List<AbnormalJikeList> list = getList(abnormalJikeParam);
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.merge(8, "导出时间段   开始时间:"+abnormalJikeParam.getStartTime().substring(0,abnormalJikeParam.getStartTime().length()-3)
                +",结束时间:"+abnormalJikeParam.getEndTime().substring(0,abnormalJikeParam.getEndTime().length()-3));
        writer.setHeaderAlias(getListHeaderAlias());
        writer.renameSheet("异常集客挖掘");
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
        aliasMapResult.put("answerIp", "IP地址");
        aliasMapResult.put("domainName", "域名");
        aliasMapResult.put("answerProvince", "IP地址所属省份");
        aliasMapResult.put("answerCity", "IP地址所属城市");
        aliasMapResult.put("cdnBusiness", "cdn厂商");
        aliasMapResult.put("cdnIllegalBusiness", "合作违规厂商");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("parseTotalCntRate", "解析占比");
        aliasMapResult.put("netInParseTotalCnt", "本网次数");
        aliasMapResult.put("withinParseTotalCnt", "本省次数");
        return aliasMapResult;
    }

    private String getTableName(String queryType){
        return "rpt_resource_exception_collector_mining_" + queryType;
    }
}
