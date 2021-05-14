package com.luban.mall.dao;


import com.luban.mall.domain.PortalMemberInfo;

public interface PortalMemberInfoDao {
    /**
     * 查询会员信息
     * @param memberId
     * @return
     */
    PortalMemberInfo getMemberInfo(Long memberId);
}
