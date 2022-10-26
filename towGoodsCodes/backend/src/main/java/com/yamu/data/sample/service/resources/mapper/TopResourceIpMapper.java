package com.yamu.data.sample.service.resources.mapper;


import com.yamu.data.sample.service.resources.entity.po.TopResourceIp;
import com.yamu.data.sample.service.resources.entity.po.TopResourceIpExcelData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author yuyuan.Dong
 * @Date 2022/2/16
 * @DESC
 */
@Repository
public interface TopResourceIpMapper {

    Long countTopIp(@Param("queryParam") TopResourceIp topUserIp);

    List<TopResourceIp> findTopIpTrend(@Param("queryParam") TopResourceIp topUserIp);

    List<TopResourceIp> findTopIpTableList(@Param("queryParam") TopResourceIp topUserIp);

    List<TopResourceIpExcelData> findTopIpTableExcelList(@Param("queryParam") TopResourceIp topUserIp);
}
