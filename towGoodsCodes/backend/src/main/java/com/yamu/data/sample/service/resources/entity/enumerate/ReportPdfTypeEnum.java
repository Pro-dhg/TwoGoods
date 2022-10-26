package com.yamu.data.sample.service.resources.entity.enumerate;

public enum ReportPdfTypeEnum {

    LINECHART("LINECHART","折线图"),
    AREACHART("AREACHART", "区间图"),
    PIECHART("PIECHART","饼图"),
    HISTOGRAM("HISTOGRAM","柱状图");

    private String type;

    private String description;

    private ReportPdfTypeEnum(String type, String description){
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
