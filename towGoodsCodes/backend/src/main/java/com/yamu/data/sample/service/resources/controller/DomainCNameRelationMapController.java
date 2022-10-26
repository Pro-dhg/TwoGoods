package com.yamu.data.sample.service.resources.controller;

import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.DomainCNameRelationDetailSelect;
import com.yamu.data.sample.service.resources.entity.po.DomainCNameRelationMap;
import com.yamu.data.sample.service.resources.service.DomainCNameRelationMapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhang Yanping
 * @date 2021/8/26
 * @desc
 */

@RestController
@RequestMapping("/service/resource/domainCNameRelationMap")
@Slf4j
public class DomainCNameRelationMapController {

    @Autowired
    private DomainCNameRelationMapService service;

    @GetMapping("/v1")
    public ResponseEntity find(DomainCNameRelationMap param) throws YamuException, ParseException {
        checkSelectParam(param);
        PageResult result = service.find(param);
        return ResponseEntity.ok(result);
    }

    @GetMapping("download/v1")
    public void download(DomainCNameRelationMap param, HttpServletResponse response) throws Exception {
        List<DomainCNameRelationMap> dataList = service.download(param);
        List<String> csvLines = dataList.stream().map(DomainCNameRelationMap::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(DomainCNameRelationMap.CSV_NAME, DomainCNameRelationMap.CSV_HEAD, csvLines, response);
    }

    @GetMapping("/detail/v1")
    public PageResult findDetail(DomainCNameRelationDetailSelect param) throws Exception {
        return service.findDetail(param);
    }

    // check select
    private void checkSelectParam(DomainCNameRelationMap param) {
        param.setDomainName(ReportUtils.escapeChar(param.getDomainName()));
        param.setCname(ReportUtils.escapeChar(param.getCname()));
    }
}
