package com.daijia.common.exeception;

import com.daijia.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * Description： 自定义全局异常
 *
 * @author: 段世超
 * @aate: Created in 2024/9/3 10:47
 */
@Data
public class BusinessException extends RuntimeException {

    private Integer code;

    private String message;

    public BusinessException(Integer code,String message){
        super(message);
        this.code = code;
        this.message = message;
    }
    public BusinessException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }

   public String toString(){
       return "BusinessException{" +
               "code=" + code +
               ", message=" + this.getMessage() +
               '}';
   }

}
