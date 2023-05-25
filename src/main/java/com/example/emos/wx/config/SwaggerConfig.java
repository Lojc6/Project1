package com.example.emos.wx.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi(){
        Docket docket=new Docket(DocumentationType.SWAGGER_2);
        ApiInfoBuilder builder=new ApiInfoBuilder();
        builder.title("EMOS在线办公系统");
        ApiInfo info=builder.build();
        docket.apiInfo(info);

        ApiSelectorBuilder selectorBuilder=docket.select();
        selectorBuilder.paths(PathSelectors.any());//所有包下的所有类都加载swagger页面
        selectorBuilder.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));//只有添加了ApiOperation的注解才可以添加到Swagger页面
        docket = selectorBuilder.build();//更新Swagger页面

        ApiKey apiKey=new ApiKey("token","token","header");
        //客户端发起JWT请求,需要上传令牌  规定在请求头header接收客户端上传的令牌,请求头里的参数name:token在里面提取
        //字符串,keyname:token 这个是描述性信息
        List<ApiKey> apiKeyList=new ArrayList<>();
        apiKeyList.add(apiKey);
        docket.securitySchemes(apiKeyList);

        //JWT在Swagger的作用域    别导错包 是这个springfox.documentation.service.AuthorizationScope;
        AuthorizationScope scope=new AuthorizationScope("global","accessEverything");
        //把认证对象再次封装
        AuthorizationScope[] scopes={scope};
        SecurityReference reference=new SecurityReference("token",scopes);
        List refList=new ArrayList();
        refList.add(reference);
        SecurityContext context=SecurityContext.builder().securityReferences(refList).build();
        List ctxList=new ArrayList();
        ctxList.add(context);
        docket.securityContexts(ctxList);

        return docket;
    }
}
