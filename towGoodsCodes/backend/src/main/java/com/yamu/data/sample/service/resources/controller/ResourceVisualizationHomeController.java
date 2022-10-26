package com.yamu.data.sample.service.resources.controller;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.service.resources.entity.bo.VisualizationHomeKeyBusinessBO;
import com.yamu.data.sample.service.resources.entity.bo.VisualizationHomeMapBO;
import com.yamu.data.sample.service.resources.entity.bo.VisualizationHomeTopnBO;
import com.yamu.data.sample.service.resources.entity.vo.VisualizationHomeDataVO;
import com.yamu.data.sample.service.resources.service.ResourceVisualizationHomeDownloadService;
import com.yamu.data.sample.service.resources.service.ResourceVisualizationHomeService;
import com.yamu.data.sample.service.resources.visualizationHomeMap.VisualizationHomeSchedulerMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author dys
 * @date 2022/01/06
 */
@RestController
@RequestMapping("/service/resource/visualizationHome")
@Api(value = "可视化首页", tags = "可视化首页API")
public class ResourceVisualizationHomeController {

    @Autowired
    private ResourceVisualizationHomeService resourceVisualizationHomeService;

    @Autowired
    private ResourceVisualizationHomeDownloadService DownloadService;

    @Autowired
    private VisualizationHomeSchedulerMap visualizationHomeSchedulerMap;

    @GetMapping("keyBusiness/v1")
    @ApiOperation("关键业务指标")
    public List<VisualizationHomeKeyBusinessBO> keyBusiness(VisualizationHomeDataVO visualizationHomeDataVO) {
        List<VisualizationHomeKeyBusinessBO> list = new ArrayList<>();
        if(visualizationHomeDataVO.getIsToday()){
            Map<String, List> cacheMap = visualizationHomeSchedulerMap.getAllRegionMap();
            String mapKey = visualizationHomeDataVO.getEndTime()+"-keyBusinessScheduler";
            if(visualizationHomeDataVO.getRankNumber() != null && !"".equals(visualizationHomeDataVO.getRankNumber())){
                mapKey += "-" + visualizationHomeDataVO.getRankNumber();
            }
            if(visualizationHomeDataVO.getUserType() != null && !"".equals(visualizationHomeDataVO.getUserType())){
                mapKey += "-" + visualizationHomeDataVO.getUserType();
            }
            list = cacheMap.get(mapKey);
            if(list == null){
                list = resourceVisualizationHomeService.keyBusiness(visualizationHomeDataVO);
            }
        }else{
            list = resourceVisualizationHomeService.keyBusiness(visualizationHomeDataVO);
        }
        return list;
    }

    @GetMapping("map/v1")
    @ApiOperation("资源分布")
    public List<VisualizationHomeMapBO> map(VisualizationHomeDataVO visualizationHomeDataVO) {
        List<VisualizationHomeMapBO> list = resourceVisualizationHomeService.getMap(visualizationHomeDataVO);
        return list;
    }

    @GetMapping("topNTrend/v1")
    @ApiOperation("热点topn分布")
    public VisualizationHomeTopnBO topNTrend(VisualizationHomeDataVO visualizationHomeDataVO) {
        VisualizationHomeTopnBO visualizationHomeTopnBO = resourceVisualizationHomeService.topNTrend(visualizationHomeDataVO);
        return visualizationHomeTopnBO;
    }

    @GetMapping("todayParseTotal/v1")
    @ApiOperation("今日解析量")
    public List<VisualizationHomeKeyBusinessBO> todayParseTotal(VisualizationHomeDataVO visualizationHomeDataVO) {
        List<VisualizationHomeKeyBusinessBO> list = resourceVisualizationHomeService.todayParseTotal(visualizationHomeDataVO);
        return list;
    }

    @GetMapping("findTopTenWebsite/v1")
    @ApiOperation("top10应用分布图")
    public ResponseEntity topTenWebsiteParseCnt(VisualizationHomeDataVO visualizationHomeDataVO){
        JSONObject dataList = resourceVisualizationHomeService.top10WebsiteParseCnt(visualizationHomeDataVO);
        return ResponseEntity.ok(dataList);
    }

    @GetMapping("findTopTenType/v1")
    @ApiOperation("top10分类分布图")
    public ResponseEntity topTenTypeParseCnt(VisualizationHomeDataVO visualizationHomeDataVO){
        JSONObject dataList = resourceVisualizationHomeService.top10TypeParseCnt(visualizationHomeDataVO);
        return ResponseEntity.ok(dataList);
    }

    @GetMapping("findTopTenDomainName/v1")
    @ApiOperation("top10域名分布图")
    public ResponseEntity topTenDomainNameParseCnt(VisualizationHomeDataVO visualizationHomeDataVO){
        JSONObject dataList = resourceVisualizationHomeService.top10DomainNameParseCnt(visualizationHomeDataVO);
        return ResponseEntity.ok(dataList);
    }

    @GetMapping("ispProportion/v1")
    @ApiOperation("资源分布占比")
    public ResponseEntity answerFirstIspProportion(VisualizationHomeDataVO visualizationHomeDataVO){
        JSONObject dataList = resourceVisualizationHomeService.answerFirstIspProportion(visualizationHomeDataVO);
        return ResponseEntity.ok(dataList);
    }

    @GetMapping("topWebsiteProportion/v1")
    @ApiOperation("top应用占比")
    public ResponseEntity topNWebsiteProportion(VisualizationHomeDataVO visualizationHomeDataVO){
        JSONObject dataList = resourceVisualizationHomeService.topNWebsiteProportion(visualizationHomeDataVO);
        return ResponseEntity.ok(dataList);
    }

    @GetMapping("topTypeProportion/v1")
    @ApiOperation("top分类占比")
    public ResponseEntity topNTypeProportion(VisualizationHomeDataVO visualizationHomeDataVO){
        JSONObject dataList = resourceVisualizationHomeService.topNTypeProportion(visualizationHomeDataVO);
        return ResponseEntity.ok(dataList);
    }

    @GetMapping("topDomainProportion/v1")
    @ApiOperation("top域名占比")
    public ResponseEntity topNDomainNameProportion(VisualizationHomeDataVO visualizationHomeDataVO){
        JSONObject dataList = resourceVisualizationHomeService.topNDomainNameProportion(visualizationHomeDataVO);
        return ResponseEntity.ok(dataList);
    }

    @GetMapping("downloadExcel/v1")
    @ApiOperation("可视化首页导出excel")
    public void downloadExcel(VisualizationHomeDataVO visualizationHomeDataVO, HttpServletResponse response) throws Exception {
        DownloadService.downloadExcel(visualizationHomeDataVO, response);
    }
}
