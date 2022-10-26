package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.utils.ValidationResult;
import com.yamu.data.sample.common.utils.ValidationUtils;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import com.yamu.data.sample.service.resources.entity.po.PopularCnameDomainList;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.PopularCnameDomainDowmloadList;
import com.yamu.data.sample.service.resources.entity.vo.PopularCnameDomainFlowTrend;
import com.yamu.data.sample.service.resources.entity.vo.PopularCnameDomainNetInTrend;
import com.yamu.data.sample.service.resources.mapper.PopularCnameDomainQueryDomainMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author dhg
 * @Date 2022/10/19
 */
@Service
@Slf4j
public class PopularCnameDomainQueryDomainService {

    @Autowired
    PopularCnameDomainQueryDomainMapper popularCnameDomainQueryDomainMapper;

    public PopularCnameDomainListData dataList(PopularCnameDomainQueryParam popularCnameDomainQueryParam) throws YamuException {
        checkFindTrendListParam(popularCnameDomainQueryParam);
        String tableName = getTableName(popularCnameDomainQueryParam.getQueryType());
        String orderByStr = getOrderByStr(popularCnameDomainQueryParam);
        PopularCnameDomainListData popularCnameDomainListData = new PopularCnameDomainListData();

        Long total = popularCnameDomainQueryDomainMapper.getTotal(popularCnameDomainQueryParam, tableName);
        List<PopularCnameDomainList> dataList = popularCnameDomainQueryDomainMapper.getDataList(popularCnameDomainQueryParam, tableName, orderByStr);
        cNameRate(dataList,popularCnameDomainQueryParam);

        popularCnameDomainListData.setTotal(total);
        popularCnameDomainListData.setData(dataList);

        return popularCnameDomainListData;
    }

    public List<PopularCnameDomainFlowTrend> flowTrend(PopularCnameDomainFlowTrendParam popularCnameDomainFlowTrendParam){

        String tableName = getTableName(popularCnameDomainFlowTrendParam.getQueryType());
        List<PopularCnameDomainFlowTrend> list = popularCnameDomainQueryDomainMapper.getFlowTrend(popularCnameDomainFlowTrendParam, tableName);

        return list;
    }
    public List<PopularCnameDomainNetInTrend> netInTrend(PopularCnameDomainFlowTrendParam popularCnameDomainFlowTrendParam){

        String tableName = getTableName(popularCnameDomainFlowTrendParam.getQueryType());
        List<PopularCnameDomainList> dataList = popularCnameDomainQueryDomainMapper.getNetInTrend(popularCnameDomainFlowTrendParam, tableName);
        List<PopularCnameDomainNetInTrend> list = getNetInRate(dataList) ;

        return list;
    }


    public void download(PopularCnameDomainParam popularCnameDomainParam, HttpServletResponse response) throws IOException {
        popularCnameDomainParam.setOffset(0L);
        popularCnameDomainParam.setLimit(10000L);
        String fileName = "CNAME反查域名分析" + StrUtil.UNDERLINE + DateUtils.formatDataToString(new Date(),"yyyyMMddHHmm") + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        String tableName = getDownloadTableName(popularCnameDomainParam.getQueryType());
        List<PopularCnameDomainDowmloadList> list = popularCnameDomainQueryDomainMapper.getDownload(popularCnameDomainParam, tableName);
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.merge(8, "导出时间段   开始时间:"+popularCnameDomainParam.getStartTime().substring(0,popularCnameDomainParam.getStartTime().length()-3)
                +",结束时间:"+popularCnameDomainParam.getEndTime().substring(0,popularCnameDomainParam.getEndTime().length()-3));
        writer.setHeaderAlias(getListHeaderAlias());
        writer.renameSheet("CNAME反查域名分析");
        writer.setOnlyAlias(true);
        writer.write(list, true);

        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);

    }
    public void allDownload(PopularCnameDomainQueryParam popularCnameDomainQueryParam, HttpServletResponse response) throws IOException {
        popularCnameDomainQueryParam.setOffset(0L);
        popularCnameDomainQueryParam.setLimit(10000L);
        String fileName = "CNAME反查域名分析" + StrUtil.UNDERLINE + DateUtils.formatDataToString(new Date(),"yyyyMMddHHmm") + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));

        String tableName = getTableName(popularCnameDomainQueryParam.getQueryType());
        String orderByStr = getOrderByStr(popularCnameDomainQueryParam);
        List<PopularCnameDomainList> dataList = popularCnameDomainQueryDomainMapper.getDataList(popularCnameDomainQueryParam, tableName, orderByStr);
        cNameRate(dataList,popularCnameDomainQueryParam);

        ExcelWriter writer = ExcelUtil.getWriter();
        writer.merge(8, "导出时间段   开始时间:"+popularCnameDomainQueryParam.getStartTime().substring(0,popularCnameDomainQueryParam.getStartTime().length()-3)
                +",结束时间:"+popularCnameDomainQueryParam.getEndTime().substring(0,popularCnameDomainQueryParam.getEndTime().length()-3));
        writer.setHeaderAlias(getAllListHeaderAlias());
        writer.renameSheet("CNAME反查域名分析");
        writer.setOnlyAlias(true);
        writer.write(dataList, true);

        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);

    }

    private Map<String, String> getListHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("cname", "CNAME");
        aliasMapResult.put("domainName", "域名");
        aliasMapResult.put("answerFirst", "目标IP");
        aliasMapResult.put("province", "IP所属省份");
        aliasMapResult.put("isp", "IP所属运营商");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        return aliasMapResult;
    }
    private Map<String, String> getAllListHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("parseTime", "时间段");
        aliasMapResult.put("cname", "别名");
        aliasMapResult.put("websiteAppName", "网站");
        aliasMapResult.put("companyShortName", "公司");
        aliasMapResult.put("domainName", "域名");
        aliasMapResult.put("domainWebsiteAppName", "域名所属网站");
        aliasMapResult.put("domainCompanyShortName", "域名所属公司");
        aliasMapResult.put("netOutParseFlowTotal", "出网流量");
        aliasMapResult.put("netOutParseTotalCnt", "出网次数");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("parseSuccessCnt", "成功次数");
        aliasMapResult.put("successRate", "成功率");
        aliasMapResult.put("parseFlowTotal", "总流量");
        aliasMapResult.put("outNetFlowRate", "出网流量占比");
        aliasMapResult.put("outNetRate", "出网率");
        aliasMapResult.put("netInParseFlowTotal", "网内流量");
        aliasMapResult.put("netInParseTotalCnt", "本网次数");
        aliasMapResult.put("netInRate", "本网率");
        aliasMapResult.put("withinParseTotalCnt", "本省次数");
        aliasMapResult.put("withinParseRate", "本省率");
        aliasMapResult.put("withoutParseTotalCnt", "外省次数");
        aliasMapResult.put("withoutParseRate", "出省率");
        return aliasMapResult;
    }

    private void checkFindTrendListParam(PopularCnameDomainQueryParam popularCnameDomainQueryParam) throws YamuException {
        String cname = popularCnameDomainQueryParam.getCname();
        String domain = popularCnameDomainQueryParam.getDomainName();
        popularCnameDomainQueryParam.setCname(ReportUtils.escapeChar(cname));
        popularCnameDomainQueryParam.setDomainName(ReportUtils.escapeChar(domain));

        ValidationResult validationResult = ValidationUtils.validateEntity(popularCnameDomainQueryParam);
        if(validationResult.isHasErrors()) {
            log.error(">>PopularCnameDomainQueryDomainService checkFindTrendListParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
    }
    private String getTableName(String queryType){
        return "rpt_resource_cname_counter_check_domain_" + queryType;
    }
    private String getDownloadTableName(String queryType){
        return "rpt_resource_cname_counter_check_domain_answer_first_" + queryType;
    }

    private String getOrderByStr(PopularCnameDomainQueryParam popularCnameDomainQueryParam){ //netOutParseFlowTotal netOutParseTotalCnt  parseTotalCnt
        String orderByStr = "";
        if(popularCnameDomainQueryParam.getSortKey() != null && !popularCnameDomainQueryParam.getSortKey().equals("")){
            orderByStr = (String) popularCnameDomainQueryParam.getSortKey() + " " + popularCnameDomainQueryParam.getSortOrder();
        }else{
            orderByStr = "parseTotalCnt desc";
        }
        return orderByStr;
    }

    private void cNameRate(List<PopularCnameDomainList> dataList, PopularCnameDomainQueryParam popularCnameDomainQueryParam){
        for (PopularCnameDomainList popularCnameDomainList : dataList) {
            BigInteger parseSuccessCnt = popularCnameDomainList.getParseSuccessCnt();
            BigInteger parseTotalCnt = popularCnameDomainList.getParseTotalCnt();
            String successRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt), 2);

            BigInteger netOutParseFlowTotal = popularCnameDomainList.getNetOutParseFlowTotal();
            BigInteger parseFlowTotal = popularCnameDomainList.getParseFlowTotal();
            String outNetFlowRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(netOutParseFlowTotal, parseFlowTotal), 2);

            BigInteger netOutParseTotalCnt = popularCnameDomainList.getNetOutParseTotalCnt();
            String outNetRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(netOutParseTotalCnt, parseTotalCnt), 2);

            BigInteger netInParseTotalCnt = popularCnameDomainList.getNetInParseTotalCnt();
            String netInRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(netInParseTotalCnt, parseTotalCnt), 2);

            BigInteger withinParseTotalCnt = popularCnameDomainList.getWithinParseTotalCnt();
            String withinParseRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(withinParseTotalCnt, parseTotalCnt), 2);

            BigInteger withoutParseTotalCnt = popularCnameDomainList.getWithoutParseTotalCnt();
            String withoutParseRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(withoutParseTotalCnt, parseTotalCnt), 2);


            popularCnameDomainList.setSuccessRate(successRate);
            popularCnameDomainList.setOutNetFlowRate(outNetFlowRate);
            popularCnameDomainList.setOutNetRate(outNetRate);
            popularCnameDomainList.setNetInRate(netInRate);
            popularCnameDomainList.setWithinParseRate(withinParseRate);
            popularCnameDomainList.setWithoutParseRate(withoutParseRate);

            popularCnameDomainList.setParseTime(
                    popularCnameDomainQueryParam.getStartTime()
                            +"~"+popularCnameDomainQueryParam.getEndTime());

        }
    }
    private List<PopularCnameDomainNetInTrend> getNetInRate(List<PopularCnameDomainList> dataList) {
        List<PopularCnameDomainNetInTrend> list = new ArrayList<>();
        PopularCnameDomainNetInTrend popularCnameDomainNetInTrend = new PopularCnameDomainNetInTrend();
        for (PopularCnameDomainList popularCnameDomainList : dataList) {
            BigInteger parseSuccessCnt = popularCnameDomainList.getParseSuccessCnt();
            BigInteger parseTotalCnt = popularCnameDomainList.getParseTotalCnt();
            String successRate = df(ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt),4);

            BigInteger withoutParseTotalCnt = popularCnameDomainList.getWithoutParseTotalCnt();
            String withoutParseRate = df(ReportUtils.buildRatioBase(withoutParseTotalCnt, parseTotalCnt),4);

            BigInteger withinParseTotalCnt = popularCnameDomainList.getWithinParseTotalCnt();
            String withinParseRate = df(ReportUtils.buildRatioBase(withinParseTotalCnt, parseTotalCnt),4);

            BigInteger netOutParseTotalCnt = popularCnameDomainList.getNetOutParseTotalCnt();
            String outNetRate = df(ReportUtils.buildRatioBase(netOutParseTotalCnt, parseTotalCnt),4);

            BigInteger netInParseTotalCnt = popularCnameDomainList.getNetInParseTotalCnt();
            String netInRate = df(ReportUtils.buildRatioBase(netInParseTotalCnt, parseTotalCnt),4);

            popularCnameDomainNetInTrend.setSuccessRate(successRate);
            popularCnameDomainNetInTrend.setWithoutParseRate(withoutParseRate);
            popularCnameDomainNetInTrend.setWithinParseRate(withinParseRate);
            popularCnameDomainNetInTrend.setOutNetRate(outNetRate);
            popularCnameDomainNetInTrend.setNetInRate(netInRate);
            popularCnameDomainNetInTrend.setParseTime(popularCnameDomainList.getParseTime());
            list.add(popularCnameDomainNetInTrend) ;
        }

        return list ;

    }
    private String df (Double d , int a){

        String format = String.format("%."+a+"f", d);

        if (format.equals("1.0000")) format="1";
        return format ;

    }

}
