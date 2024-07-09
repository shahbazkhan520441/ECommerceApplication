package com.jsp.ecommerce.utility;

public class ResponseStructure<T> {
	
    private int status;
    
	private String message;
	
	private T data;
	
	public int getStatus() {
		return status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public T getData() {
		return data;
	}
	
	public ResponseStructure<T> setStatus(int status) {
		this.status = status;
		return this;
	}
	
	public ResponseStructure<T> setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public ResponseStructure<T> setData(T data) {
		this.data = data;
		return this;
	}

}
