package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;
import java.util.Date;

/**
 * @author getiejun
 * @date 2021/7/25
 * @DESC 组合的X轴
 */
@Data
public class CombinedXAxisBO {

    private Date parseTime;

    private String combinedField;

    public CombinedXAxisBO() {}

    public CombinedXAxisBO(Date parseTime, String combinedField) {
        this.parseTime = parseTime;
        this.combinedField = combinedField;
    }
}
