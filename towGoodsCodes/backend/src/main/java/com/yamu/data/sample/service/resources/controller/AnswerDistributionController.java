package com.yamu.data.sample.service.resources.controller;

import com.yamu.data.sample.service.resources.entity.bo.AnswerDistributionBO;
import com.yamu.data.sample.service.resources.service.AnswerDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
/**
 * @author getiejun
 * Date 2020-07-1
 */
@RestController
@RequestMapping("/service/resource/answerDistribution")
public class AnswerDistributionController {
    @Autowired
    private AnswerDistributionService answerDistributionService;

    /**
     * 查找省市资源分布信息
     * @param answerDistributionBO
     * @return
     */
    @GetMapping("map/v1")
    public ResponseEntity findProvinceMap(AnswerDistributionBO answerDistributionBO) {
        return ResponseEntity.ok(answerDistributionService.findProvinceMapDataList(answerDistributionBO));
    }

    /**
     * 查找省市资源分布信息排名
     * @param answerDistributionBO
     * @return
     */
    @GetMapping("rank/v1")
    public ResponseEntity findProvinceRank(AnswerDistributionBO answerDistributionBO) {
        return ResponseEntity.ok(answerDistributionService.findProvinceRank(answerDistributionBO));
    }

    /**
     * 查找省市资源分布占比
     * @param answerDistributionBO
     * @return
     */
    @GetMapping("rate/v1")
    public ResponseEntity findProvinceRate(AnswerDistributionBO answerDistributionBO) {
        return ResponseEntity.ok(answerDistributionService.findProvinceRate(answerDistributionBO));
    }

    /**
     * 资源分布解析量趋势分析
     * @param answerDistributionBO
     * @return
     */
    @GetMapping("trendReport/v1")
    public ResponseEntity findTrendReport(AnswerDistributionBO answerDistributionBO) {
        return ResponseEntity.ok(answerDistributionService.findTrendReport(answerDistributionBO));
    }

    /**
     * 资源分布明细
     * @param answerDistributionBO
     * @return
     */
    @GetMapping("trendDetail/v1")
    public ResponseEntity findTrendDetail(AnswerDistributionBO answerDistributionBO) {
        return ResponseEntity.ok(answerDistributionService.findTrendDetail(answerDistributionBO));
    }

    @GetMapping("trendDistributeCode/v1")
    public ResponseEntity findTrendDistributeCode(AnswerDistributionBO answerDistributionBO, int distributeNum) {
        return ResponseEntity.ok(answerDistributionService.findTrendProvinceByNum(answerDistributionBO, distributeNum));
    }

    /**
     * 导出文件
     *
     * @return
     */
    @GetMapping("downloadDetail/v1")
    public void downloadDetail(AnswerDistributionBO answerDistributionBO, HttpServletResponse response) throws Exception {
        answerDistributionService.downloadDetail(answerDistributionBO, response);
    }
}
