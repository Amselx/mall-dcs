package com.luban.domain;

import com.luban.model.UmsMember;
import com.luban.model.UmsMemberLevel;
import lombok.Data;

@Data
public class PortalMemberInfo extends UmsMember {
    private UmsMemberLevel umsMemberLevel;
}
