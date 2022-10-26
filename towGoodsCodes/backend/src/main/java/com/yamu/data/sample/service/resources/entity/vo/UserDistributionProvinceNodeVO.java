package com.yamu.data.sample.service.resources.entity.vo;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class UserDistributionProvinceNodeVO {

    private String name;

    private List<BigInteger> value;

    private String districtsCode;
}
