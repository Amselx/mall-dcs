package com.luban.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.luban.common.constant.RedisKeyPrefixConst;
import com.luban.mall.component.LocalCache;
import com.luban.mall.component.zklock.ZKLock;
import com.luban.mall.dao.FlashPromotionProductDao;
import com.luban.mall.dao.PortalProductDao;
import com.luban.mall.domain.*;
import com.luban.mapper.SmsFlashPromotionMapper;
import com.luban.mapper.SmsFlashPromotionSessionMapper;
import com.luban.model.SmsFlashPromotion;
import com.luban.model.SmsFlashPromotionExample;
import com.luban.model.SmsFlashPromotionSession;
import com.luban.model.SmsFlashPromotionSessionExample;
import com.luban.mall.service.PmsProductService;
import com.luban.mall.util.DateUtil;
import com.luban.mall.util.RedisOpsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PmsProductServiceImpl implements PmsProductService {

    @Autowired
    private PortalProductDao portalProductDao;

    @Autowired
    private FlashPromotionProductDao flashPromotionProductDao;

    @Autowired
    private SmsFlashPromotionMapper flashPromotionMapper;

    @Autowired
    private SmsFlashPromotionSessionMapper promotionSessionMapper;

    @Autowired
    private RedisOpsUtil redisOpsUtil;

    private Map<String, PmsProductParam> cacheMap = new ConcurrentHashMap<>();

    @Autowired
    private LocalCache cache;

    /*
     * zk分布式锁
     */
    @Autowired
    private ZKLock zkLock;
    private String lockPath = "/load_db";

    /**
     * 获取商品详情信息
     *
     * @param id 产品ID
     */
    public PmsProductParam getProductInfo0(Long id) {

        PmsProductParam productInfo = redisOpsUtil.get(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id, PmsProductParam.class);
        if (productInfo != null)
            return productInfo;

        productInfo = portalProductDao.getProductInfo(id);
        if (productInfo == null) {
            return null;
        }
        /*
         * 查询是否是秒杀商品
         */
        FlashPromotionParam promotion = flashPromotionProductDao.getFlashPromotion(id);
        if (!ObjectUtils.isEmpty(promotion)) {
            productInfo.setFlashPromotionCount(promotion.getRelation().get(0).getFlashPromotionCount());
            productInfo.setFlashPromotionLimit(promotion.getRelation().get(0).getFlashPromotionLimit());
            productInfo.setFlashPromotionPrice(promotion.getRelation().get(0).getFlashPromotionPrice());
            productInfo.setFlashPromotionRelationId(promotion.getRelation().get(0).getId());
            productInfo.setFlashPromotionEndDate(promotion.getEndDate());
            productInfo.setFlashPromotionStartDate(promotion.getStartDate());
            productInfo.setFlashPromotionStatus(promotion.getStatus());
        }

        log.info("start query product info {}", id);
        redisOpsUtil.set(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE+id,productInfo,3600,TimeUnit.SECONDS);

        return productInfo;
    }


    public PmsProductParam getProductInfo_0(Long id) {

        PmsProductParam productInfo = portalProductDao.getProductInfo(id);
        if (productInfo == null) {
            return null;
        }
        /*
         * 查询是否是秒杀商品
         */
        FlashPromotionParam promotion = flashPromotionProductDao.getFlashPromotion(id);
        if (!ObjectUtils.isEmpty(promotion)) {
            productInfo.setFlashPromotionCount(promotion.getRelation().get(0).getFlashPromotionCount());
            productInfo.setFlashPromotionLimit(promotion.getRelation().get(0).getFlashPromotionLimit());
            productInfo.setFlashPromotionPrice(promotion.getRelation().get(0).getFlashPromotionPrice());
            productInfo.setFlashPromotionRelationId(promotion.getRelation().get(0).getId());
            productInfo.setFlashPromotionEndDate(promotion.getEndDate());
            productInfo.setFlashPromotionStartDate(promotion.getStartDate());
            productInfo.setFlashPromotionStatus(promotion.getStatus());
        }

        log.info("start query product info {}", id);
        return productInfo;
    }

    public PmsProductParam getProductInfo1(Long id) {
        log.info("start query product info {}", id);
        //  PmsProductParam productInfo=cacheMap.get(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE+id)
        PmsProductParam productInfo = cache.get(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id);

        if (productInfo != null)
            return productInfo;

        productInfo = redisOpsUtil.get(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id, PmsProductParam.class);
        if (productInfo != null) {
            // cacheMap.put(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE+id,productInfo);
            cache.setLocalCache(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id, productInfo);
            return productInfo;
        }

        productInfo = portalProductDao.getProductInfo(id);
        if (productInfo == null) {
            return null;
        }
        /*
         * 查询是否是秒杀商品
         */
        FlashPromotionParam promotion = flashPromotionProductDao.getFlashPromotion(id);
        if (!ObjectUtils.isEmpty(promotion)) {
            productInfo.setFlashPromotionCount(promotion.getRelation().get(0).getFlashPromotionCount());
            productInfo.setFlashPromotionLimit(promotion.getRelation().get(0).getFlashPromotionLimit());
            productInfo.setFlashPromotionPrice(promotion.getRelation().get(0).getFlashPromotionPrice());
            productInfo.setFlashPromotionRelationId(promotion.getRelation().get(0).getId());
            productInfo.setFlashPromotionEndDate(promotion.getEndDate());
            productInfo.setFlashPromotionStartDate(promotion.getStartDate());
            productInfo.setFlashPromotionStatus(promotion.getStatus());
        }
        //本地缓存
        // cacheMap.put(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE+id,productInfo);
        // Google Guava Cache
        cache.setLocalCache(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id, productInfo);
        //redis缓存
        redisOpsUtil.set(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id, productInfo, 3600, TimeUnit.SECONDS);
        return productInfo;
    }

    /**
     * 获取商品详情信息
     *
     * @param id 产品ID
     */
    public PmsProductParam getProductInfo(Long id) {
        PmsProductParam productInfo = null;
        productInfo = cache.get(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id);
        //一级缓存
        if (productInfo != null) {
            return productInfo;
        }

        //从二级缓存Redis里找
        productInfo = redisOpsUtil.get(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id, PmsProductParam.class);

        if (productInfo != null) {
            cache.setLocalCache(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id, productInfo);
            return productInfo;
        }

        if (zkLock.lock(lockPath + "_" + id)) {
            //todo 查询商品详情信息
            productInfo = portalProductDao.getProductInfo(id);
            if (productInfo == null) {
                return null;
            }

            /*
             * 查询是否是秒杀商品
             */
            FlashPromotionParam promotion = flashPromotionProductDao.getFlashPromotion(id);
            if (!ObjectUtils.isEmpty(promotion)) {
                productInfo.setFlashPromotionCount(promotion.getRelation().get(0).getFlashPromotionCount());
                productInfo.setFlashPromotionLimit(promotion.getRelation().get(0).getFlashPromotionLimit());
                productInfo.setFlashPromotionPrice(promotion.getRelation().get(0).getFlashPromotionPrice());
                productInfo.setFlashPromotionRelationId(promotion.getRelation().get(0).getId());
                productInfo.setFlashPromotionEndDate(promotion.getEndDate());
                productInfo.setFlashPromotionStartDate(promotion.getStartDate());
                productInfo.setFlashPromotionStatus(promotion.getStatus());
            }

            //todo 缓存到一级缓存
            cache.setLocalCache(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id, productInfo);
            //todo 商品信息缓存到reids当中，缓存被动更新
            redisOpsUtil.set(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id, productInfo, 3600, TimeUnit.SECONDS);
            zkLock.unlock(lockPath + "_" + id);
        } else {
            productInfo = redisOpsUtil.get(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id, PmsProductParam.class);
            if (productInfo != null) {
                cache.setLocalCache(RedisKeyPrefixConst.PRODUCT_DETAIL_CACHE + id, productInfo);
            }
        }
        return productInfo;
    }

    /**
     * 获取秒杀商品列表
     *
     * @param flashPromotionId 秒杀活动ID，关联秒杀活动设置
     * @param sessionId        场次活动ID，for example：13:00-14:00场等
     */
    public List<FlashPromotionProduct> getFlashProductList(Integer pageSize, Integer pageNum, Long flashPromotionId, Long sessionId) {
        PageHelper.startPage(pageNum, pageSize, "sort desc");
        return flashPromotionProductDao.getFlashProductList(flashPromotionId, sessionId);
    }

    /**
     * 获取当前日期秒杀活动所有场次
     *
     * @return
     */
    public List<FlashPromotionSessionExt> getFlashPromotionSessionList() {
        Date now = new Date();
        SmsFlashPromotion promotion = getFlashPromotion(now);
        if (promotion != null) {
            SmsFlashPromotionSessionExample sessionExample = new SmsFlashPromotionSessionExample();
            //获取时间段内的秒杀场次
            sessionExample.createCriteria().andStatusEqualTo(1);//启用状态
            sessionExample.setOrderByClause("start_time asc");
            List<SmsFlashPromotionSession> promotionSessionList = promotionSessionMapper.selectByExample(sessionExample);
            List<FlashPromotionSessionExt> extList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(promotionSessionList)) {
                promotionSessionList.stream().forEach((item) -> {
                    FlashPromotionSessionExt ext = new FlashPromotionSessionExt();
                    BeanUtils.copyProperties(item, ext);
                    ext.setFlashPromotionId(promotion.getId());
                    if (DateUtil.getTime(now).after(DateUtil.getTime(ext.getStartTime()))
                            && DateUtil.getTime(now).before(DateUtil.getTime(ext.getEndTime()))) {
                        //活动进行中
                        ext.setSessionStatus(0);
                    } else if (DateUtil.getTime(now).after(DateUtil.getTime(ext.getEndTime()))) {
                        //活动即将开始
                        ext.setSessionStatus(1);
                    } else if (DateUtil.getTime(now).before(DateUtil.getTime(ext.getStartTime()))) {
                        //活动已结束
                        ext.setSessionStatus(2);
                    }
                    extList.add(ext);
                });
                return extList;
            }
        }
        return null;
    }

    //根据时间获取秒杀活动
    public SmsFlashPromotion getFlashPromotion(Date date) {
        Date currDate = DateUtil.getDate(date);
        SmsFlashPromotionExample example = new SmsFlashPromotionExample();
        example.createCriteria()
                .andStatusEqualTo(1)
                .andStartDateLessThanOrEqualTo(currDate)
                .andEndDateGreaterThanOrEqualTo(currDate);
        List<SmsFlashPromotion> flashPromotionList = flashPromotionMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(flashPromotionList)) {
            return flashPromotionList.get(0);
        }
        return null;
    }

    /**
     * 获取首页的秒杀商品列表
     *
     * @return
     */
    public List<FlashPromotionProduct> getHomeSecKillProductList() {
        PageHelper.startPage(1, 8, "sort desc");
        FlashPromotionParam flashPromotionParam = flashPromotionProductDao.getFlashPromotion(null);
        if (flashPromotionParam == null || CollectionUtils.isEmpty(flashPromotionParam.getRelation())) {
            return null;
        }
        List<Long> promotionIds = new ArrayList<>();
        flashPromotionParam.getRelation().stream().forEach(item -> {
            promotionIds.add(item.getId());
        });
        PageHelper.clearPage();
        return flashPromotionProductDao.getHomePromotionProductList(promotionIds);
    }

    @Override
    public CartProduct getCartProduct(Long productId) {
        return portalProductDao.getCartProduct(productId);
    }

    @Override
    public List<PromotionProduct> getPromotionProductList(List<Long> ids) {
        return portalProductDao.getPromotionProductList(ids);
    }

    /**
     * 查找出所有的产品ID
     *
     * @return
     */
    public List<Long> getAllProductId() {
        return portalProductDao.getAllProductId();
    }
}
