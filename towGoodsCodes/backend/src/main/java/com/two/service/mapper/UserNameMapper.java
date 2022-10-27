package com.two.service.mapper;

import com.two.service.entity.po.UserNameParam;
import com.two.service.entity.vo.UserNameListVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author dhg
 * @Date 2022/10/19
 */
@Repository
public interface UserNameMapper {

    Long getTotal(@Param("queryParam") UserNameParam uerNameParam , @Param("queryTable")String queryTable);

    List<UserNameListVO> getDataList(@Param("queryParam") UserNameParam uerNameParam , @Param("queryTable")String queryTable);
}
