package com.yamu.data.sample.service.resources.entity.enumerate;

import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;

public enum DomainTypeEnum {

    CENTRAL_DOMAIN("重点域名","重点域名"),
    CENTRAL_OFFICE_DOMAIN("重点机关域名","重点机关域名"),
    CENTRAL_COMPANY_DOMAIN("重点企业域名","重点企业域名"),
    CENTRAL_BUSINESS_DOMAIN("重点行业域名","重点行业域名"),
    CENTRAL_COLLEGES_DOMAIN("重点高校域名","重点高校域名"),
    COUNTRY_CENTRAL_DOMAIN("全国重点域名","全国重点域名"),
    PROVINCIAL_GOVERNMENT("省政府类","省政府类"),
    E_COMMERCE("电子商务类","电子商务类"),
    ENTERPRISES_ABOVE_DESIGNATED_SIZE("规模以上企业类","规模以上企业类"),
    OTHER("其他","其他");

    private String type;

    private String description;

    private DomainTypeEnum(String type, String description){
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

    /**
     * 获取所有域名类型
     * @return
     */
    public static List<String> getAllDomainType() {
        DomainTypeEnum[] allType = DomainTypeEnum.values();
        List<String> domainTypeList = Lists.newArrayList();
        Arrays.stream(allType).forEach(domainType -> {
            domainTypeList.add(domainType.getType());
        });
        return domainTypeList;
    }

    public static List<String> getDomainTypeNotContainsOther() {
        DomainTypeEnum[] allType = DomainTypeEnum.values();
        List<String> domainTypeList = Lists.newArrayList();
        Arrays.stream(allType).forEach(domainType -> {
            if(!domainType.getType().equals(OTHER.getType())) {
                domainTypeList.add(domainType.getType());
            }
        });
        return domainTypeList;
    }
}
