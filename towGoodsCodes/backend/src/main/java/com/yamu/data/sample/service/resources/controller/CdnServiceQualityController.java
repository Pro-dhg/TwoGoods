package com.yamu.data.sample.service.resources.controller;

import com.yamu.data.sample.service.resources.entity.vo.*;
import com.yamu.data.sample.service.resources.service.CdnServiceQualityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @author dys
 * @date 2022/07/26
 */
@RestController
@RequestMapping("/service/resource/cdnServiceQuality")
@Api(value = "CDN厂商服务质量分析", tags = "CDN厂商服务质量分析API")
public class CdnServiceQualityController {

    @Autowired
    CdnServiceQualityService cdnServiceQualityService;

    @GetMapping("sumData/v1")
    @ApiOperation("总览数据")
    public CdnServiceQualitySumData sumData(CdnServiceQualityParam cdnServiceQualityParam){
        CdnServiceQualitySumData data = cdnServiceQualityService.sumData(cdnServiceQualityParam);
        return data;
    }

    @GetMapping("map/v1")
    @ApiOperation("全国的CDN覆盖节点")
    public List<CdnServiceQualityMapData> map(CdnServiceQualityParam cdnServiceQualityParam){
        List<CdnServiceQualityMapData> list = cdnServiceQualityService.map(cdnServiceQualityParam);
        return list;
    }

    @GetMapping("cdnCover/v1")
    @ApiOperation("CDN覆盖")
    public CdnServiceQualityCdnCover cdnCover(CdnServiceQualityParam cdnServiceQualityParam){
        CdnServiceQualityCdnCover data = cdnServiceQualityService.cdnCover(cdnServiceQualityParam);
        return data;
    }

    @GetMapping("responseResultsTrend/v1")
    @ApiOperation("应答结果分布")
    public ResponseResultsTrend responseResultsTrend(CdnServiceQualityParam cdnServiceQualityParam) throws ParseException {
        ResponseResultsTrend data = cdnServiceQualityService.responseResultsTrend(cdnServiceQualityParam);
        return data;
    }

    @GetMapping("rateTrend/v1")
    @ApiOperation("本网、本省、本市趋势")
    public List<RateTrend> rateTrend(CdnServiceQualityParam cdnServiceQualityParam) throws ParseException {
        List<RateTrend> list = cdnServiceQualityService.rateTrend(cdnServiceQualityParam);
        return list;
    }

    @GetMapping("dispatchTrend/v1")
    @ApiOperation("节点变化趋势")
    public List<ResourceCdnServerDispatchTrend> dispatchTrend(CdnServiceQualityParam cdnServiceQualityParam) throws ParseException {
        List<ResourceCdnServerDispatchTrend> list = cdnServiceQualityService.dispatchTrend(cdnServiceQualityParam);
        return list;
    }

    @GetMapping("topNCdn/v1")
    @ApiOperation("TopN CDN厂商")
    public List<String> topNCdn(CdnServiceQualityParam cdnServiceQualityParam){
        List<String> list = cdnServiceQualityService.topNCdn(cdnServiceQualityParam);
        return list;
    }

    @GetMapping("download/v1")
    @ApiOperation("导出")
    public void download(CdnServiceQualityParam cdnServiceQualityParam, HttpServletResponse response) throws IOException, ParseException{
        cdnServiceQualityService.download(cdnServiceQualityParam,response);
    }
}
