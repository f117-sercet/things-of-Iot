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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    public PageVo<UsedCouponVo> findUsedPage(Page<CouponInfo> pageParam, Long customerId) {
        IPage<UsedCouponVo> pageInfo = couponInfoMapper.findUsedPage(pageParam, customerId);
        return new PageVo(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getTotal());
    }

    @Override
    public PageVo<NoReceiveCouponVo> findNoReceivePage(Page<CouponInfo> pageParam, Long customerId) {

        IPage<NoReceiveCouponVo> pageInfo = couponInfoMapper.findNoReceivePage(pageParam, customerId);
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

        // 1.创建List 集合,存储最终返回数据
        ArrayList<AvailableCouponVo> availableCouponVoList = new ArrayList<>();

        // 2.根据乘客Id,获取乘客已经领取但是没有使用的优惠券列表
        List<NoUseCouponVo> list = couponInfoMapper.findNoUseList(customerId);

        // 3.遍历乘客未使用的优惠券，得到每个优惠券
        // 3.1判断优惠券类型:现金券和折扣券
        List<NoUseCouponVo> typeList = list.stream().filter(item -> item.getCouponType() == 1).collect(Collectors.toList());
        // 3.2 是现金券
        // 判断现金是否满足条件
        for (NoUseCouponVo noUseCouponVo : typeList) {

            // 判断使用门槛
            // 减免金额
            BigDecimal reduceAmount = noUseCouponVo.getAmount();
            // 1.没有门槛 ==0，订单金额必须大于优惠减免金额
            if (noUseCouponVo.getConditionAmount().doubleValue() ==0
                    && orderAmount.subtract(reduceAmount).doubleValue()>0) {
                availableCouponVoList.add(this.buildBestNoUseCouponVo(noUseCouponVo,reduceAmount));
            }
            // 2.有门槛，，订单金额大于优惠门槛金额

            if(noUseCouponVo.getConditionAmount().doubleValue() > 0
                    && orderAmount.subtract(noUseCouponVo.getConditionAmount()).doubleValue()>0) {
                availableCouponVoList.add(this.buildBestNoUseCouponVo(noUseCouponVo,reduceAmount));
            }
        }
        //3.3折扣券
        // 判断折扣券是否满足条件

        // 4.把满足条件优惠券放到最终list集合

        // 5.如果满足条件，更新两张数据

        return List.of();
    }

    @Override
    public BigDecimal useCoupon(UseCouponForm useCouponForm) {
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
