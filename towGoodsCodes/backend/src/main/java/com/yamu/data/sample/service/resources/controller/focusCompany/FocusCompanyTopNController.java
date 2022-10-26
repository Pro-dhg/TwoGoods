package com.yamu.data.sample.service.resources.controller.focusCompany;

import cn.hutool.core.util.ObjectUtil;
import com.yamu.data.sample.common.result.ErrorResult;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.resources.entity.po.FocusCompanyDetail;
import com.yamu.data.sample.service.resources.entity.po.FocusCompanyTopN;
import com.yamu.data.sample.service.resources.service.FocusCompanyTopNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @author zhangyanping
 * Date 2020-07-1
 */
@RestController
@RequestMapping("/service/resource/focusCompanyTopN")
public class FocusCompanyTopNController {

    @Autowired
    private FocusCompanyTopNService focusCompanyTopNService;

    /**
     * 重点公司排名TopN报表
     * @param focusCompanyTopN
     * @return
     */
    @GetMapping("rankNum/v1")
    public ResponseEntity findRankNumber(FocusCompanyTopN focusCompanyTopN) {
        // 判断公司名称输入：不超过200个字符
        if (ObjectUtil.isNotEmpty(focusCompanyTopN.getCompanyShortName())) {
            if (focusCompanyTopN.getCompanyShortName().length() > 200) {
                return new ResponseEntity(new ErrorResult(ErrorResult.SYSTEM_FAILURE, "公司简称不能超过200个字符"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        PageResult pageResult = focusCompanyTopNService.findRankNumber(focusCompanyTopN);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 重点公司排名数据导出
     * @param focusCompanyTopN
     * @param response
     * @throws Exception
     */
    @GetMapping("download/v1")
    public void downloadRankNumber(FocusCompanyTopN focusCompanyTopN, HttpServletResponse response) throws Exception {
 //       checkDownloadMethodParam(focusCompanyTopN);
        List<FocusCompanyTopN> dataList = focusCompanyTopNService.downloadByParam(focusCompanyTopN);
        List<String> csvLines = dataList.stream().map(FocusCompanyTopN::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(FocusCompanyTopN.CSV_NAME, focusCompanyTopN.getStartTime(), focusCompanyTopN.getEndTime(), FocusCompanyTopN.CSV_HEAD, csvLines, response);
    }

    /**
     * 重点公司排名公司详细信息
     * @param focusCompanyDetail
     * @return
     */
    @GetMapping("detail/v1")
    public ResponseEntity findDetail(FocusCompanyDetail focusCompanyDetail) {
        PageResult pageResult = focusCompanyTopNService.findDetail(focusCompanyDetail);
        return ResponseEntity.ok(pageResult);
    }

//    private void checkDownloadMethodParam(FocusCompanyTopN focusCompanyTopN) throws UnsupportedEncodingException {
//        if(ObjectUtil.isNotEmpty(focusCompanyTopN.getCompanyShortName())) {
//            focusCompanyTopN.setCompanyShortName(URLDecoder.decode(focusCompanyTopN.getCompanyShortName(), "utf-8"));
//        }
//    }

}
