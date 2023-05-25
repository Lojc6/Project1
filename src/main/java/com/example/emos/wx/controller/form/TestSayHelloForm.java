package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@ApiModel //在实体类上边使用,标记类时swagger的解析类  在swagger描述里面的model就会有

//@ApiModelProperty  使用在被ApiModel注解的模型类的属性上
@Data
public class TestSayHelloForm {
    @NotBlank
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,15}$")
    @ApiModelProperty("姓名")//描述属性
    public String name;
}
