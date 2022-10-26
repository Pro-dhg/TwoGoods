package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

/**
 * @Author yuyuan.Dong
 * @Date 2022/1/13
 * @DESC
 */
@Data
public class KeyBusinessDownloadBO {
    private String name;

    //"环比"
    private String yoyRate;

    //"同比"
    private String momRate;

    //"占比"
    private String popRate;
}
