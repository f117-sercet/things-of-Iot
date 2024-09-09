package com.daijia.driver.client;

import com.daijia.common.result.Result;
import com.daijia.model.vo.driver.CosUploadVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/9 9:48
 */
@FeignClient(value="service-driver")
public interface CosFeignClient {

    //文件上传
    @PostMapping(value = "/cos/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<CosUploadVo> upload(@RequestPart("file") MultipartFile file, @RequestParam("path") String path);
}
