package com.daijia.driver.client;

import com.daijia.common.result.Result;
import com.daijia.model.vo.order.TextAuditingVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/9 9:40
 */
@FeignClient(value = "service-driver")
public interface CiFeignClient {
    /**
     * 文本审核
     * @return
     */
    @PostMapping("/ci/textAuditing")
    Result<TextAuditingVo> textAuditing(@RequestBody String content);
}
