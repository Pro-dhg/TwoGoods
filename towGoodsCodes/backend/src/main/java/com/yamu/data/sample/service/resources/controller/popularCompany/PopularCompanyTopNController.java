package com.yamu.data.sample.service.resources.controller.popularCompany;

import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;

import com.yamu.data.sample.service.resources.service.PopularCompanyTopNService;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.resources.entity.po.PopularCompanyTopN;
import com.yamu.data.sample.service.resources.entity.po.PopularCompanyTopNDetail;
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
@RequestMapping("/service/resource/popularCompanyTopN")
public class PopularCompanyTopNController {

    @Autowired
    private PopularCompanyTopNService popularCompanyTopNService;

    @GetMapping("rankNum/v1")
    public ResponseEntity findRankNumber(PopularCompanyTopN popularCompanyTopN) throws YamuException {
        PageResult pageResult = popularCompanyTopNService.findRankNumber(popularCompanyTopN);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("download/v1")
    public void downloadRankNumber(PopularCompanyTopN popularCompanyTopN, HttpServletResponse response) throws Exception {
//        checkDownloadMethodParam(popularCompanyTopN);
        List<PopularCompanyTopN> dataList = popularCompanyTopNService.downloadByParam(popularCompanyTopN);
        List<String> csvLines = dataList.stream().map(PopularCompanyTopN::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(PopularCompanyTopN.CSV_NAME, popularCompanyTopN.getStartTime(), popularCompanyTopN.getEndTime(), PopularCompanyTopN.CSV_HEAD, csvLines, response);
    }

    @GetMapping("detail/v1")
    public ResponseEntity findRankNumber(PopularCompanyTopNDetail popularCompanyTopNDetail) {
        PageResult pageResult = popularCompanyTopNService.findDomainDetail(popularCompanyTopNDetail);
        return ResponseEntity.ok(pageResult);
    }

//    private void checkDownloadMethodParam(PopularCompanyTopN popularCompanyTopN) throws UnsupportedEncodingException {
//        if(ObjectUtil.isNotEmpty(popularCompanyTopN.getCompanyShortName())) {
//            popularCompanyTopN.setCompanyShortName(URLDecoder.decode(popularCompanyTopN.getCompanyShortName(), "utf-8"));
//        }
//    }
}
