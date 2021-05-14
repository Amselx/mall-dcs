package com.luban.mall.service;


import com.luban.mall.domain.PortalMemberInfo;

public interface UmsMemberCenterService {

    /**
     * 查询会员信息
     * @param memberId
     * @return
     */
    PortalMemberInfo getMemberInfo(Long memberId);
}
