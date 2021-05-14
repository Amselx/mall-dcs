package com.luban.portal.domain;

import com.luban.model.PmsComment;
import com.luban.model.PmsCommentReplay;
import lombok.Data;

import java.util.List;


@Data
public class PmsCommentParam extends PmsComment {
    private List<PmsCommentReplay> pmsCommentReplayList;
}
