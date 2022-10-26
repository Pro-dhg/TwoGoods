package com.yamu.data.sample.service.resources.entity.enumerate;

/**
 * @author getiejun
 * @date 2021/7/21
 */
public enum StatisticsWayEnum {
    ALL("all","时间段全部数据"),
    EVERY("every","时间段粒度数据");

    private String type;

    private String description;

    private StatisticsWayEnum(String type, String description){
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
