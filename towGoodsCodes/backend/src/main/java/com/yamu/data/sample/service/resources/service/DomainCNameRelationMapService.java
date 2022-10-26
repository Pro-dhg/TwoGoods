package com.yamu.data.sample.service.resources.service;

import com.google.common.collect.Lists;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.DomainCNameRelationDetailReturn;
import com.yamu.data.sample.service.resources.entity.po.DomainCNameRelationDetailSelect;
import com.yamu.data.sample.service.resources.entity.po.DomainCNameRelationMap;
import com.yamu.data.sample.service.resources.mapper.DomainCNameRelationMapMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author ZhangYanping
 * @Date 2021/8/26
 * @DESC
 */

@Service
@Slf4j
public class DomainCNameRelationMapService {

    @Autowired
    private DomainCNameRelationMapMapper mapper;

    public PageResult find(DomainCNameRelationMap cNameRelationMap) throws YamuException, ParseException {
        cNameRelationMap.setLstQueryTime(setLstQueryTime(cNameRelationMap.getQueryTime()));
//        checkSelectParam(cNameRelationMap);
        Long total = mapper.countFind(cNameRelationMap);
        List<DomainCNameRelationMap> dataList = mapper.find(cNameRelationMap);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        dataList.forEach(x -> {
            String format = fmt.format(x.getParseTime());
            x.setQueryTime(format);
        });
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    public List<DomainCNameRelationMap> download(DomainCNameRelationMap param) throws ParseException {
        param.setLstQueryTime(setLstQueryTime(param.getQueryTime()));
//        checkSelectParam(param);
        param.setOffset(0L);
        param.setLimit(10000L);
        return mapper.find(param);
    }

    public PageResult findDetail(DomainCNameRelationDetailSelect param) throws YamuException, ParseException {
        param.setLstQueryTime(setLstQueryTime(param.getQueryTime()));

        Long count1 = mapper.countFindDetail(param);
        List<DomainCNameRelationDetailSelect> list1 = mapper.findDetail(param);

        param.setQueryTime(param.getLstQueryTime());

        Long count2 = mapper.countFindDetail(param);
        List<DomainCNameRelationDetailSelect> list2 = mapper.findDetail(param);

        List<DomainCNameRelationDetailReturn> listAll = Lists.newArrayList();
        Long total = 0L;
        // for循环次数
        Long num = 0L;

        if (count1 != 0 || count2 != 0) {

            if (count1 >= count2) {
                total = count1;
                num = (count1 > (param.getOffset() + param.getLimit())) ? param.getLimit() : (count1 - param.getOffset());

                for (int i = 0; i <= num - 1; i++) {
                    DomainCNameRelationDetailReturn subDetail = new DomainCNameRelationDetailReturn();
                    // current
                    subDetail.setParseTime(list1.get(i).getParseTime());
                    subDetail.setDomainName(list1.get(i).getDomainName());
                    subDetail.setCurCname(list1.get(i).getCname());
                    subDetail.setCurARecord(list1.get(i).getARecord());
                    subDetail.setCurAAAARecord(list1.get(i).getAaaaRecord());
                    // last
                    if (i <= count2 - 1) {
                        subDetail.setLstCname(list2.get(i).getCname());
                        subDetail.setLstARecord(list2.get(i).getARecord());
                        subDetail.setLstAAAARecord(list2.get(i).getAaaaRecord());
                    }
                    listAll.add(subDetail);
                }

            } else {
                total = count2;
                num = (count2 > param.getLimit()) ? param.getLimit() : count2;

                for (int i = 0; i <= num - 1; i++) {
                    DomainCNameRelationDetailReturn subDetail = new DomainCNameRelationDetailReturn();
                    // last
                    subDetail.setParseTime(list2.get(i).getParseTime());
                    subDetail.setDomainName(list2.get(i).getDomainName());
                    subDetail.setLstCname(list2.get(i).getCname());
                    subDetail.setLstARecord(list2.get(i).getARecord());
                    subDetail.setLstAAAARecord(list2.get(i).getAaaaRecord());
                    // current
                    if (i <= count1 - 1) {
                        subDetail.setCurCname(list1.get(i).getCname());
                        subDetail.setCurARecord(list1.get(i).getARecord());
                        subDetail.setCurAAAARecord(list1.get(i).getAaaaRecord());
                    }
                    listAll.add(subDetail);
                }
            }
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        listAll.forEach(x -> {
            String format = fmt.format(x.getParseTime());
            x.setQueryTime(format);
        });

        PageResult pageResult = new PageResult(total, listAll);
        return pageResult;
    }

    private String setLstQueryTime(String queryTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(DateUtils.DEFAULT_FMT);
        Long dif = df.parse(queryTime).getTime() - 86400 * 1000;
        Date date = new Date();
        date.setTime(dif);
        String format = df.format(date);
        return format;
    }

    private void checkSelectParam(DomainCNameRelationMap param) {
        param.setDomainName(ReportUtils.escapeChar(param.getDomainName()));
        param.setCname(ReportUtils.escapeChar(param.getCname()));
    }
}
