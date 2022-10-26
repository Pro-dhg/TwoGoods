package com.yamu.data.sample.service.resources.controller;

import com.yamu.data.sample.service.resources.entity.bo.OperatorProportionBO;
import com.yamu.data.sample.service.resources.entity.bo.OwnershipOperatorDataBO;
import com.yamu.data.sample.service.resources.entity.po.ResourceDistributionProvinceData;
import com.yamu.data.sample.service.resources.entity.vo.ResourceOwnershipOperatorVO;
import com.yamu.data.sample.service.resources.service.ResourceOwnershipOperatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author dys
 * @date 2022/03/15
 */
@RestController
@RequestMapping("/service/resource/ownershipOperator")
@Api(value = "归属地&运营商分布分析", tags = "归属地&运营商分布分析API")
public class ResourceOwnershipOperatorController {

    @Autowired
    ResourceOwnershipOperatorService resourceOwnershipOperatorService;

    @GetMapping("dataList/v1")
    @ApiOperation("列表")
    public OwnershipOperatorDataBO dataList(ResourceOwnershipOperatorVO resourceOwnershipOperatorVO){
        OwnershipOperatorDataBO ownershipOperatorDataBO = resourceOwnershipOperatorService.dataList(resourceOwnershipOperatorVO);
        return ownershipOperatorDataBO;
    }

    @GetMapping("operatorProportion/v1")
    @ApiOperation("运营商占比")
    public List<OperatorProportionBO> operatorProportion(ResourceOwnershipOperatorVO resourceOwnershipOperatorVO){
        List<OperatorProportionBO> list = resourceOwnershipOperatorService.operatorProportion(resourceOwnershipOperatorVO);
        return list;
    }

    @GetMapping("ownershipDistribution/v1")
    @ApiOperation("归属分布")
    public List<ResourceDistributionProvinceData> ownershipDistribution(ResourceOwnershipOperatorVO resourceOwnershipOperatorVO){
        List<ResourceDistributionProvinceData> list = resourceOwnershipOperatorService.ownershipDistribution(resourceOwnershipOperatorVO);
        return list;
    }

    @GetMapping("download/v1")
    @ApiOperation("列表")
    public void download(ResourceOwnershipOperatorVO resourceOwnershipOperatorVO, HttpServletResponse response) throws IOException {
        resourceOwnershipOperatorService.download(resourceOwnershipOperatorVO,response);
    }

}
