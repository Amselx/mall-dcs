package com.luban.domain;

import lombok.Data;

@Data
public class MqCancelOrder {

    private Long orderId;

    private Long memberId;
}
