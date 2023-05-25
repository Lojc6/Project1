package com.example.emos.wx.controller.form;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class leave_requestForm {

     private int employee_id;

     private Date start_date;

     private Date end_date;

     private String leave_type;

     private String reason;

     private Byte status;




}
