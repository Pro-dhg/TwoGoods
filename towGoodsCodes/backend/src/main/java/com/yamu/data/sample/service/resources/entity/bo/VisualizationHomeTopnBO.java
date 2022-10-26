package com.yamu.data.sample.service.resources.entity.bo;

import com.yamu.data.sample.service.resources.entity.po.ResourceWebsiteUserSource;
import com.yamu.data.sample.service.resources.entity.vo.VisualizationHomeTopNTrendVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class VisualizationHomeTopnBO {

    @ApiModelProperty(value = "热点topN趋势")
    private List<VisualizationHomeTopNTrendVO> topNTrendList;

    @ApiModelProperty(value = "用户分布")
    private List<ResourceWebsiteUserSource> userList;
}
