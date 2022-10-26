package com.yamu.data.sample.service.resources.controller;

import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.resources.entity.po.FocusCompanyTrend;
import com.yamu.data.sample.service.resources.entity.po.TopResourceIp;
import com.yamu.data.sample.service.resources.entity.vo.CdnBusinessAssessQueryVo;
import com.yamu.data.sample.service.resources.service.CdnBusinessAssessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author zl.chen
 * @create 2022/10/21 15:38
 */

@RestController
@RequestMapping("/service/resources/cndBusinessAssess")
@Api(value = "CDN厂商调度考核报告", tags = "CDN厂商调度考核报告API")
public class CdnBusinessAssessController {

    @Resource
    private CdnBusinessAssessService service;
    private static final String CSV_NAME = "CDN厂商调度考核报告";
    private static final String CSV_HEAD = "时间,厂商名称,考核得分,对比变化率,解析次数,对比变化率,成功率,对比变化率,ICP调度准确率,对比变化率,本网率,对比变化率,本省率,对比变化率,邻省率,对比变化率\n";

    @PostMapping("/tableDetail")
    @ApiOperation("厂商调度考核明细")
    public ResponseEntity tableDetail(@RequestBody CdnBusinessAssessQueryVo queryVo) {
        PageResult pageResult = service.tableDetail(queryVo);
        return ResponseEntity.ok(pageResult);
    }
    @PostMapping("/scoreTrend")
    @ApiOperation("得分趋势")
    public ResponseEntity scoreTrend(@RequestBody CdnBusinessAssessQueryVo queryVo){
        return service.scoreTrend(queryVo);
    }

    @PostMapping("/scoreCityTrend")
    @ApiOperation("各地市得分对比")
    public ResponseEntity scoreCityTrend(@RequestBody CdnBusinessAssessQueryVo queryVo){
        return service.scoreCityTrend(queryVo);
    }

    @PostMapping("indicatTrend")
    @ApiOperation("指标趋势")
    public ResponseEntity indicatTrend(@RequestBody CdnBusinessAssessQueryVo queryVo){
        return service.indicatTrend(queryVo);
    }

    @PostMapping("radar")
    @ApiOperation("得分雷达图")
    public ResponseEntity radar(@RequestBody CdnBusinessAssessQueryVo queryVo){
        return service.radar(queryVo);
    }

    @PostMapping("download")
    public void download(@RequestBody CdnBusinessAssessQueryVo queryVo, HttpServletResponse response) throws Exception {
        List<String> csvLines = service.getCsving(queryVo);
        CsvUtils.exportCsv(CSV_NAME, queryVo.getStartTime(), queryVo.getEndTime(), CSV_HEAD, csvLines, response);
    }
}
