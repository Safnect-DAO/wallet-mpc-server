package com.safnect.wallet.mpc.dto;

public class ResponseModel {
	
	int code = 200;
	
	String msg;
	
	Object data;

	private ResponseModel() {}
	
	private ResponseModel(String msg, Object data) {
		super();
		this.msg = msg;
		this.data = data;
	}
	
	private ResponseModel(int code, String msg, Object data) {
		super();
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	
	public static ResponseModel sucess() {
		return new ResponseModel(null, null);
	}
	
	public static ResponseModel sucess(String msg) {
		return new ResponseModel(msg, null);
	}
	
	public static ResponseModel sucessData(Object data) {
		return new ResponseModel(null, data);
	}
	
	public static ResponseModel fail(String msg) {
		return new ResponseModel(500, msg, null);
	}
	
	public static ResponseModel fail(int code, String msg) {
		return new ResponseModel(code, msg, null);
	}
	
	public static ResponseModel fail601() {
		return new ResponseModel(601, "Missing parameters", null);
	}
	
	public static ResponseModel fail602() {
		return new ResponseModel(602, "Invalid parameters", null);
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
