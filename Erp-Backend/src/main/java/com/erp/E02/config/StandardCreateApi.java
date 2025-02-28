package com.erp.E02.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "创建资源", description = "创建新的资源并初始化")
@ApiResponses({
        @ApiResponse(responseCode = "201", description = "创建成功"),
        @ApiResponse(responseCode = "409", description = "名称重复"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
})
//swagger-ui自动化生成接口信息
public @interface StandardCreateApi {
    String summary() default "创建资源";
    String description() default "创建新的资源并初始化";
}