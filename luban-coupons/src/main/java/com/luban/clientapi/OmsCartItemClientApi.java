package com.luban.clientapi;

import com.luban.clientapi.interceptor.config.FeignConfig;
import com.luban.common.api.CommonResult;
import com.luban.domain.CartPromotionItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;



@FeignClient(name = "luban-order",configuration = FeignConfig.class)
public interface OmsCartItemClientApi {

    @RequestMapping(value = "/cart/list/promotion", method = RequestMethod.GET)
    @ResponseBody
    CommonResult<List<CartPromotionItem>> listPromotionByMemberId();

}
