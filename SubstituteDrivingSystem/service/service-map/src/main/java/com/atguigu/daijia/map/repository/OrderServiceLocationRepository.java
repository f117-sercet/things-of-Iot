package com.atguigu.daijia.map.repository;

import com.atguigu.daijia.model.entity.map.OrderServiceLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/12/11 9:26
 */
@Repository
public interface OrderServiceLocationRepository
        extends MongoRepository<OrderServiceLocation,String> {

    //  根据订单id获取代驾订单位置信息,根据创建时间排序(升序)
    List<OrderServiceLocation> findByOrderIdOrderByCreateTimeAsc(Long orderId);
}
