package com.example.emos.wx.controller.form;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@ApiModel
public class leave_requestForm {



     private int employeeId;

     private Date startdate;

     private Date enddate;

     private String leavetype;

     private String reason;






}
