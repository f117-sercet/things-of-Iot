package com.daijia.driver.servce.impl;

import com.daijia.common.result.Result;
import com.daijia.driver.client.CosFeignClient;
import com.daijia.driver.servce.CosService;
import com.daijia.model.vo.driver.CosUploadVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/9/25 9:31
 */
@Log4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CosServiceImpl implements CosService {

    @Resource
    private CosFeignClient cosFeignClient;

    @Override
    public CosUploadVo uploadFile(MultipartFile file, String path) {

        // 文件上传接
        Result<CosUploadVo> cosUploadVoResult = cosFeignClient.upload(file, path);
        CosUploadVo cosUploadVo = cosUploadVoResult.getData();
        return cosUploadVo;
    }
}
