package com.example.emos.wx.exception;

import lombok.Data;

@Data
public class EmosException extends RuntimeException{
    private String msg;//保存异常信息
    private int code=500; //出现异常状态码500,写死

    //定义构造函数
    public EmosException(String msg){
        super(msg);
        this.msg=msg;

        //继承了父类RuntimeException的构造方法
        //子类构造器中是要调用父类构造器的.
    }

    public EmosException(String msg,Throwable e){
        super(msg,e);
        this.msg=msg;
    }

    public EmosException(String msg,int code){
        super(msg);
        this.msg=msg;
        this.code=code;
    }

    public EmosException(String msg,int code,Throwable throwable){
        super(msg,throwable);
        this.msg=msg;
        this.code=code;
    }

}
