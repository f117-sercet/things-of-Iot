package com.daijia.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daijia.common.constant.RedisConstant;
import com.daijia.common.exeception.BusinessException;
import com.daijia.common.result.ResultCodeEnum;
import com.daijia.coupon.mapper.CouponInfoMapper;
import com.daijia.coupon.mapper.CustomerCouponMapper;
import com.daijia.coupon.service.CouponInfoService;
import com.daijia.model.entity.coupon.CouponInfo;
import com.daijia.model.entity.coupon.CustomerCoupon;
import com.daijia.model.form.coupon.UseCouponForm;
import com.daijia.model.vo.base.PageVo;
import com.daijia.model.vo.coupon.AvailableCouponVo;
import com.daijia.model.vo.coupon.NoReceiveCouponVo;
import com.daijia.model.vo.coupon.NoUseCouponVo;
import com.daijia.model.vo.coupon.UsedCouponVo;
import jakarta.annotation.Resource;
import net.sf.jsqlparser.statement.select.KSQLWindow;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/11/12 9:34
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    @Resource
    private CouponInfoMapper couponInfoMapper;

    @Resource
    private CustomerCouponMapper customerCouponMapper;

    @Resource
    private RedissonClient redissonClient;


    @Override
    public PageVo<NoReceiveCouponVo> findNoReceivePage(Page<CouponInfo> pageParam, Long customerId) {

        IPage<UsedCouponVo> pageInfo = couponInfoMapper.findUsedPage(pageParam, customerId);
        return new PageVo(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getTotal());
    }

    @Override
    public PageVo<NoUseCouponVo> findNoUsePage(Page<CouponInfo> pageParam, Long customerId) {
        IPage<NoUseCouponVo> pageInfo = couponInfoMapper.findNoUsePage(pageParam, customerId);
        return new PageVo(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getTotal());
    }

    @Override
    public Boolean receive(Long customerId, Long couponId) {

        //1.查询优惠卷信息
        // 判断如果优惠券不存在
        CouponInfo couponInfo = couponInfoMapper.selectById(couponId);
        if (couponInfo== null) {
            throw new BusinessException(ResultCodeEnum.DATA_ERROR);
        }
        // 2.判断优惠券是否过期
        if (couponInfo.getExpireTime().before(new Date())) {
            throw new BusinessException(ResultCodeEnum.COUPON_EXPIRE);
        }
        // 3.检查库存,发行数量 和领券数量
        if (couponInfo.getPublishCount()!=0 && couponInfo.getReceiveCount() == couponInfo.getPublishCount()) {

            throw new BusinessException(ResultCodeEnum.COUPON_LESS);
        }
        RLock lock = null;
        try {
            lock = redissonClient.getLock(RedisConstant.COUPON_LOCK + couponId);
          boolean flag = lock.tryLock(RedisConstant.COUPON_LOCK_WAIT_TIME,
                    RedisConstant.COUPON_LOCK_LEASE_TIME, TimeUnit.SECONDS);
        if (flag){
            // 4.检查每个人限制领取数量
            if (couponInfo.getPerLimit()>0) {
                //统计当前客户已经领取优惠卷数量
                LambdaQueryWrapper<CustomerCoupon> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(CustomerCoupon::getCouponId,couponId);
                wrapper.eq(CustomerCoupon::getCustomerId,customerId);
                Long count = customerCouponMapper.selectCount(wrapper);

                // 判断
                if (count >= couponInfo.getPerLimit()){
                    throw new BusinessException(ResultCodeEnum.COUPON_USER_LIMIT);
                }
            }
            //5.领取优惠券
           //5.1 更新领取数量
            int row = couponInfoMapper.updateReceiveCount(couponId);
            // 添加领取记录
            this.saveCustomerCoupon(customerId,couponId,couponInfo.getExpireTime());
            return  true;
        }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (lock !=null){
                lock.unlock();
            }
        }
        return true;
    }

    @Override
    public List<AvailableCouponVo> findAvailableCoupon(Long customerId, BigDecimal orderAmount) {
        return List.of();
    }

    @Override
    public BigDecimal useCoupon(UseCouponForm useCouponForm) {
        return null;
    }

    @Override
    public PageVo<UsedCouponVo> findUsedPage(Page<CouponInfo> pageParam, Long customerId) {
        return null;
    }

    private void saveCustomerCoupon(Long customerId, Long couponId, Date expireTime) {

        CustomerCoupon customerCoupon = new CustomerCoupon();
        customerCoupon.setCouponId(couponId);
        customerCoupon.setCustomerId(customerId);
        customerCoupon.setExpireTime(expireTime);
        customerCoupon.setReceiveTime(new Date());
        customerCoupon.setStatus(1);
        customerCouponMapper.insert(customerCoupon);


    }
}
