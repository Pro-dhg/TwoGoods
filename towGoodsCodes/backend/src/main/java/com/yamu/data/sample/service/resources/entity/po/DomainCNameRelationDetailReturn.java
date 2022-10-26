package com.yamu.data.sample.service.resources.entity.po;

import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import lombok.Data;

/**
 * @author Zhang Yanping
 * @Date 2021/8/27
 * @DESC
 */

@Data
public class DomainCNameRelationDetailReturn extends BaseRptEntity {

    /**
     * 查询时间
     */
    private String queryTime;
    /**
     * 域名
     */
    private String domainName;
    /**
     * 别名: 当前、上一时间
     */
    private String curCname;
    private String lstCname;
    /**
     * A记录: 当前、上一时间
     */
    private String curARecord;
    private String lstARecord;
    /**
     * 4A记录: 当前、上一时间
     */
    private String curAAAARecord;
    private String lstAAAARecord;

}
