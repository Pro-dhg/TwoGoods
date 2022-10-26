package com.yamu.data.sample.service.resources.entity.po;

import com.yamu.data.sample.service.resources.entity.enumerate.ReportPdfTypeEnum;
import lombok.Data;
import org.jfree.data.general.Dataset;

@Data
public class ResourceReportPdf {

    private String titleName;
    private ReportPdfTypeEnum reportPdfType;
    private Dataset data;
}
