package com.luban.feignapi.ums;

import com.luban.common.api.CommonResult;
import com.luban.domain.CartPromotionItem;
import com.luban.domain.SmsCouponHistoryDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* 会员优惠卷服务
*/
@FeignClient(name = "mall-coupons",path = "/coupon")
public interface UmsCouponFeignApi {

    @RequestMapping(value = "/listCart", method = RequestMethod.POST)
    @ResponseBody
    CommonResult<List<SmsCouponHistoryDetail>> listCart2Feign(@RequestParam("type") Integer type,
                                                              @RequestBody List<CartPromotionItem> cartPromotionItemList);

}
