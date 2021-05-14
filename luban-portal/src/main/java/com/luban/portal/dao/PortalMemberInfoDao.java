package com.luban.portal.dao;

import com.luban.portal.domain.PortalMemberInfo;


public interface PortalMemberInfoDao {
    /**
     * 查询会员信息
     * @param memberId
     * @return
     */
    PortalMemberInfo getMemberInfo(Long memberId);
}
