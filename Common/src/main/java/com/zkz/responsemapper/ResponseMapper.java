package com.zkz.responsemapper;


import lombok.Data;

@Data
public class ResponseMapper {
    private int code;
    private String msg;
    private Object data;

    public static ResponseMapper setOK(){
        ResponseMapper r=new ResponseMapper();
        r.setCode(1);
        r.setMsg("OK");
        r.setData(null);
        return r;
    }
    public static ResponseMapper setOK(String msg,Object data){
        ResponseMapper r=new ResponseMapper();
        r.setCode(1);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
    public static ResponseMapper setERROR(){
        ResponseMapper r=new ResponseMapper();
        r.setCode(1000);
        r.setMsg("ERROR");
        r.setData(null);
        return r;
    }
    public static ResponseMapper setERROR(String msg){
        ResponseMapper r=new ResponseMapper();
        r.setCode(1000);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }

}
