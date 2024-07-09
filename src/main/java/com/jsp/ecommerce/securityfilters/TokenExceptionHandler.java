package com.jsp.ecommerce.securityfilters;

import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsp.ecommerce.utility.ErrorStructure;

import jakarta.servlet.http.HttpServletResponse;

public class TokenExceptionHandler {
 public static	void tokenExceptionHandler(int status,String rootcause,HttpServletResponse httpServletResponse) throws StreamWriteException, DatabindException, IOException{
	httpServletResponse.setStatus(status);
	ErrorStructure<String> error=new ErrorStructure<String>()
			.setStatus(status)
			.setMessage("faield to authenticate")
			.setRootCause(rootcause);
	ObjectMapper  objectMapper=new ObjectMapper();
	objectMapper.writeValue(httpServletResponse.getOutputStream(), error);
	}
}
