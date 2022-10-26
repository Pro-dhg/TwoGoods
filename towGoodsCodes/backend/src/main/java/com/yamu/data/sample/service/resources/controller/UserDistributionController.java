package com.yamu.data.sample.service.resources.controller;

import com.yamu.data.sample.service.resources.entity.bo.UserDistributionBO;
import com.yamu.data.sample.service.resources.service.UserDistributionService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping("/service/resource/userDistribution")
public class UserDistributionController {

    @Autowired
    private UserDistributionService userDistributionService;

    /**
     * 查找省市用户分布信息
     * @param userDistributionBO
     * @return
     */
    @GetMapping("map/v1")
    public ResponseEntity findProvinceMap(UserDistributionBO userDistributionBO) {
        return ResponseEntity.ok(userDistributionService.findProvinceMapDataList(userDistributionBO));
    }

    /**
     * 查找省市用户信息排名
     * @param userDistributionBO
     * @return
     */
    @GetMapping("rank/v1")
    public ResponseEntity findProvinceRank(UserDistributionBO userDistributionBO) {
        return ResponseEntity.ok(userDistributionService.findProvinceRank(userDistributionBO));
    }

    /**
     * 查找省市用户占比
     * @param userDistributionBO
     * @return
     */
    @GetMapping("rate/v1")
    public ResponseEntity findProvinceRate(UserDistributionBO userDistributionBO) {
        return ResponseEntity.ok(userDistributionService.findProvinceRate(userDistributionBO));
    }

    /**
     * 用户解析量趋势分析
     * @param userDistributionBO
     * @return
     */
    @GetMapping("trendReport/v1")
    public ResponseEntity findTrendReport(UserDistributionBO userDistributionBO) {
        return ResponseEntity.ok(userDistributionService.findTrendReport(userDistributionBO));
    }

    /**
     * 用户分布明细
     * @param userDistributionBO
     * @return
     */
    @GetMapping("trendDetail/v1")
    public ResponseEntity findTrendDetail(UserDistributionBO userDistributionBO) {
        return ResponseEntity.ok(userDistributionService.findTrendDetail(userDistributionBO));
    }

    @GetMapping("trendDistribute/v1")
    public ResponseEntity findTrendDistribute(UserDistributionBO userDistributionBO, int distributeNum) {
        return ResponseEntity.ok(userDistributionService.findTrendDistributeByNum(userDistributionBO, distributeNum));
    }

    /**
     * 导出文件
     *
     * @return
     */
    @GetMapping("downloadDetail/v1")
    public void downloadDetail(UserDistributionBO userDistributionBO, HttpServletResponse response) throws Exception {
        userDistributionService.downloadDetail(userDistributionBO, response);
    }


}
