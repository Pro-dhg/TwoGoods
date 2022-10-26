package com.yamu.data.sample.service.resources.controller.focusCompany;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.result.ErrorResult;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.resources.entity.po.FocusCompanyTrend;
import com.yamu.data.sample.service.resources.service.FocusCompanyTrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangyanping
 * Date 2020-07-1
 */
@RestController
@RequestMapping("/service/resource/focusCompanyTrend")
public class FocusCompanyTrendController {

    @Autowired
    private FocusCompanyTrendService focusCompanyTrendService;

    /**
     * 重点公司访问趋势分析: 访问重点公司趋势明细
     *
     * @param focusCompanyTrend
     * @return
     */
    @RequestMapping("detailTable/v1")
    public ResponseEntity findTrendDetailByPage(FocusCompanyTrend focusCompanyTrend) {
        // 判断公司名称输入：不超过200个字符
        if (ObjectUtil.isNotEmpty(focusCompanyTrend.getCompanyShortName())) {
            if (focusCompanyTrend.getCompanyShortName().length() > 200) {
                return new ResponseEntity(new ErrorResult(ErrorResult.SYSTEM_FAILURE, "公司简称不能超过200个字符"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        PageResult result = focusCompanyTrendService.findTrendDetailByPage(focusCompanyTrend);
        return ResponseEntity.ok(result);
    }

    /**
     * 重点公司访问趋势分析: 重点公司资源分布图表
     *
     * @param focusCompanyTrend
     * @return
     */
    @GetMapping("resourceReport/v1")
    public ResponseEntity findResourceReport(FocusCompanyTrend focusCompanyTrend, boolean isCount) throws ParseException {
        JSONObject finalResult = focusCompanyTrendService.findResourceReport(focusCompanyTrend, isCount);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 重点公司访问趋势分析: 重点公司访问量趋势图表
     *
     * @param focusCompanyTrend
     * @return
     */
    @GetMapping("accessReport/v1")
    public ResponseEntity findAccessReport(FocusCompanyTrend focusCompanyTrend) throws ParseException {
        JSONObject finalResult = focusCompanyTrendService.findAccessReport(focusCompanyTrend);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 重点公司访问趋势分析: 重点公司本网率、本省率趋势图图表
     *
     * @param focusCompanyTrend
     * @return
     */
    @GetMapping("rateReport/v1")
    public ResponseEntity findRateReport(FocusCompanyTrend focusCompanyTrend) throws ParseException {
        JSONObject finalResult = focusCompanyTrendService.findRateReport(focusCompanyTrend);
        return ResponseEntity.ok(finalResult);
    }

    /**
     * 重点公司访问趋势分析: 重点公司CDN、IDC解析调度趋势图表
     *
     * @param focusCompanyTrend
     * @return
     */
    @GetMapping("parseReport/v1")
    public ResponseEntity findParseReport(FocusCompanyTrend focusCompanyTrend) throws ParseException {
        JSONObject finalResult = focusCompanyTrendService.findParseReport(focusCompanyTrend);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("download/v1")
    public void downloadRankNumber(FocusCompanyTrend focusCompanyTrend, HttpServletResponse response) throws Exception {
//        checkDownloadMethodParam(focusCompanyTrend);
        // 判断公司名称输入：不超过200个字符
//        if (ObjectUtil.isNotEmpty(focusCompanyTrend.getCompanyShortName())) {
//            if (focusCompanyTrend.getCompanyShortName().length() > 200) {
//                return new ResponseEntity(new ErrorResult(ErrorResult.SYSTEM_FAILURE, "公司简称不能超过200个字符"), HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
        List<FocusCompanyTrend> dataList = focusCompanyTrendService.downloadByParam(focusCompanyTrend);
        dataList.stream().forEach(FocusCompanyTrend::buildRate);
        List<String> csvLines = dataList.stream().map(FocusCompanyTrend::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(FocusCompanyTrend.CSV_NAME, focusCompanyTrend.getStartTime(), focusCompanyTrend.getEndTime(), FocusCompanyTrend.CSV_HEAD, csvLines, response);
//        return ResponseEntity.ok(null);
    }

//    private void checkDownloadMethodParam(FocusCompanyTrend focusCompanyTrend) throws UnsupportedEncodingException {
//        if (ObjectUtil.isNotEmpty(focusCompanyTrend.getCompanyShortName())) {
//            focusCompanyTrend.setCompanyShortName(URLDecoder.decode(focusCompanyTrend.getCompanyShortName(), "utf-8"));
//        }
//    }
}
