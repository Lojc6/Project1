package com.example.emos.wx.db.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * leave_request
 * @author 
 */
@Data
public class LeaveRequest implements Serializable {
    private Integer id;

    private Integer employeeId;

    private Date startDate;

    private Date endDate;

    private String leaveType;

    private String reason;

    private Byte status;

    private Integer approverId;

    private String approverComment;

    private static final long serialVersionUID = 1L;
}