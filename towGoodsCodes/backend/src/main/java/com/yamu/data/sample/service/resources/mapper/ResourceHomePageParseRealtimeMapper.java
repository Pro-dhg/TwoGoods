package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.ResourceHomePageParseRealtime;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author getiejun
 * @date 2021/10/19
 */

@Repository
public interface ResourceHomePageParseRealtimeMapper {

    List<ResourceHomePageParseRealtime> findAll();
}
