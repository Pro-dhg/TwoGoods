package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.bo.AbnormalJikeParam;
import com.yamu.data.sample.service.resources.entity.vo.AbnormalJikeList;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author dys
 * @Date 2022/9/22
 */
@Repository
public interface AbnormalJikeMapper {

    Long getTotal(@Param("queryParam") AbnormalJikeParam abnormalJikeParam, @Param("queryTable")String queryTable);

    AbnormalJikeList getSumData(@Param("queryParam") AbnormalJikeParam abnormalJikeParam, @Param("queryTable")String queryTable);

    List<AbnormalJikeList> getList(@Param("queryParam") AbnormalJikeParam abnormalJikeParam, @Param("queryTable")String queryTable);
}
