package com.safnect.wallet.mpc.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.util.JsonUtil;
import com.safnect.wallet.mpc.util.TextUtil;

@Component
public class TokenAuthInterceptor implements HandlerInterceptor {
	
	static final String KEY = "s2fnect";
	
	static final String ALLOW_TOKEN = "8bdf70515e98bd98e4532aa55778b88791cff4f08a61a2998930619aaeef70a8";

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String token = request.getHeader("token");
		if (StringUtils.isAnyBlank(token)) {
			this.writeMessage(response, ResponseModel.fail601());
			return false;
		}
		
		if (StringUtils.equals(token, ALLOW_TOKEN)) { // allow this token
			return true;
		}
		
		String encryptText = this.getEncryptText(request);
		if (!StringUtils.equals(token, encryptText)) {
			try{
				this.writeMessage(response, ResponseModel.fail(603, "Invalid token"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
    }
	
	private String getEncryptText(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		List<String> list = new ArrayList<>();
		while (names.hasMoreElements()) {
			list.add(names.nextElement());
		}
		Collections.sort(list);
		StringBuilder sb = new StringBuilder();
		list.forEach((item) -> {
			String value = request.getParameter(item);
			sb.append(value);
		});
		sb.append(KEY);
		String text = sb.toString();
		String encryptText = TextUtil.sha1(TextUtil.base64(text));
		return encryptText;
	}

	private void writeMessage(HttpServletResponse response, ResponseModel rm) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		PrintWriter out = response.getWriter();
		out.write(JsonUtil.toJson(rm));
		out.flush();
		out.close();
	}
}
