package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.google.common.collect.Maps;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.RecordDomainNameDetailList;
import com.yamu.data.sample.service.resources.entity.po.RecordDomainNameList;
import com.yamu.data.sample.service.resources.entity.po.RecordDomainNameListData;
import com.yamu.data.sample.service.resources.entity.po.RecordDomainNameParam;
import com.yamu.data.sample.service.resources.mapper.RecordDomainNameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author dys
 * @Date 2022/05/13
 */
@Service
public class RecordDomainNameService {

    @Autowired
    RecordDomainNameMapper recordDomainNameMapper;

    public RecordDomainNameListData dataList(RecordDomainNameParam recordDomainNameParam){
        String tableName = getTableName(recordDomainNameParam.getQueryType());
        RecordDomainNameListData recordDomainNameListData = new RecordDomainNameListData();
        Long total = recordDomainNameMapper.getTotal(recordDomainNameParam,tableName);
        List<RecordDomainNameList> list = recordDomainNameMapper.getList(recordDomainNameParam,tableName);
        RecordDomainNameList sumData = recordDomainNameMapper.getSumCnt(recordDomainNameParam,tableName);
        for (RecordDomainNameList data : list) {
            data.setParseTime(recordDomainNameParam.getStartTime() + "~" + recordDomainNameParam.getEndTime());
            data.setSuccessRate(convertDoubleToPercent(
                    ReportUtils.buildRatioBase(data.getParseSuccessCnt(), data.getParseTotalCnt()),2));
            data.setNetOutRate(convertDoubleToPercent(
                    ReportUtils.buildRatioBase(data.getNetOutParseTotalCnt(), data.getParseTotalCnt()),2));
            data.setNetInRate(convertDoubleToPercent(
                    ReportUtils.buildRatioBase(data.getNetInParseTotalCnt(), data.getParseTotalCnt()),2));
            data.setRate(convertDoubleToPercent(
                    ReportUtils.buildRatioBase(data.getParseTotalCnt(), sumData.getParseTotalCnt()),2));
        }
        recordDomainNameListData.setTotal(total);
        recordDomainNameListData.setData(list);
        return recordDomainNameListData;
    }

    public List<RecordDomainNameDetailList> dataDetailList(RecordDomainNameParam recordDomainNameParam){
        String tableName = getTableName(recordDomainNameParam.getQueryType());
        List<RecordDomainNameDetailList> list = recordDomainNameMapper.getDetailList(recordDomainNameParam,tableName);
        return list;
    }

    public void download(RecordDomainNameParam recordDomainNameParam, HttpServletResponse response) throws IOException {
        recordDomainNameParam.setOffset(0L);
        recordDomainNameParam.setLimit(10000L);
        String fileName = "备案域分析" + StrUtil.UNDERLINE + DateUtils.formatDataToString(new Date(),"yyyyMMddHHmm") + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        List<RecordDomainNameList> list = dataList(recordDomainNameParam).getData();
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.merge(8, "导出时间段   开始时间:"+recordDomainNameParam.getStartTime().substring(0,recordDomainNameParam.getStartTime().length()-3)
                +",结束时间:"+recordDomainNameParam.getEndTime().substring(0,recordDomainNameParam.getEndTime().length()-3));
        writer.setHeaderAlias(getListHeaderAlias());
        writer.renameSheet("备案域分析");
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
        aliasMapResult.put("domainName", "域");
        aliasMapResult.put("parseTotalCnt", "DNS查询次数");
        aliasMapResult.put("parseSuccessCnt", "成功次数");
        aliasMapResult.put("successRate", "成功率");
        aliasMapResult.put("netOutParseTotalCnt", "出网次数");
        aliasMapResult.put("netOutRate", "出网率");
        aliasMapResult.put("netInParseTotalCnt", "网内次数");
        aliasMapResult.put("netInRate", "本网率");
        aliasMapResult.put("parseFailCnt", "错误次数");
        aliasMapResult.put("withinParseTotalCnt", "本省次数");
        aliasMapResult.put("withoutParseTotalCnt", "外省次数");
        aliasMapResult.put("cdnParseTotalCnt", "CDN");
        aliasMapResult.put("idcParseTotalCnt", "IDC");
        aliasMapResult.put("rate", "请求占比");
        aliasMapResult.put("domainNameCnt", "域名数");
        aliasMapResult.put("icpCode", "备案");
        aliasMapResult.put("companyShortName", "公司名");
        aliasMapResult.put("websiteName", "所属网站");
        aliasMapResult.put("zgdxCnt", "中国电信");
        aliasMapResult.put("zgltCnt", "中国联通");
        aliasMapResult.put("gatCnt", "港澳台");
        aliasMapResult.put("outCountryCnt", "境外");
        aliasMapResult.put("unknownCnt", "未知");
        return aliasMapResult;
    }

    private String getTableName(String queryType){
        return "rpt_resource_icp_domain_analyze_" + queryType;
    }

    private String convertDoubleToPercent(double value, int scale) {
        if(value == 0) {
            return "0%";
        }
        String valStr = new BigDecimal(value).multiply(new BigDecimal(100)).setScale(scale, RoundingMode.HALF_UP).toString();
        if(valStr.equals("100.00")) {
            return "100%";
        }
        return valStr + "%";
    }
}
