package com.yamu.data.sample.service.resources.entity.bo;

import com.yamu.data.sample.service.resources.entity.vo.SecondDomainTableListVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author yuyuan.Dong
 * @Date 2022/3/17
 * @DESC
 */
@Data
@ApiModel
public class SecondDomainTableListBO {

    @ApiModelProperty(value = "总条数")
    private Long total;

    @ApiModelProperty(value = "数据")
    private List<SecondDomainTableListVO> data;

}
