package com.yamu.data.sample.service.resources.controller.popularDomain;

import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainDetail;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainTopN;
import com.yamu.data.sample.service.resources.service.PopularDomainTopNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @author getiejun
 * Date 2020-07-1
 */
@RestController
@RequestMapping("/service/resource/popularDomainTopN")
public class PopularDomainTopNController {

    @Autowired
    private PopularDomainTopNService popularDomainTopNService;

    @GetMapping("rankNum/v1")
    public ResponseEntity findRankNumber(PopularDomainTopN popularDomainTopN) throws Exception {
        PageResult pageResult = popularDomainTopNService.findRankNumber(popularDomainTopN);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("download/v1")
    public void downloadRankNumber(PopularDomainTopN popularDomainTopN, HttpServletResponse response) throws Exception {
        List<PopularDomainTopN> dataList = popularDomainTopNService.downloadByParam(popularDomainTopN);
        List<String> csvLines = dataList.stream().map(PopularDomainTopN::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(PopularDomainTopN.CSV_NAME, popularDomainTopN.getStartTime(), popularDomainTopN.getEndTime(), PopularDomainTopN.CSV_HEAD, csvLines, response);
    }

    @GetMapping("detail/v1")
    public ResponseEntity findRankNumber(PopularDomainDetail popularDomainDetail) throws Exception {
        PageResult pageResult = popularDomainTopNService.findDomainDetail(popularDomainDetail);
        return ResponseEntity.ok(pageResult);
    }
}
