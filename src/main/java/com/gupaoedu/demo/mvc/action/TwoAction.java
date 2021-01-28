package com.gupaoedu.demo.mvc.action;

import java.io.IOException;

import com.gupaoedu.demo.service.IDemoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TwoAction {
	
	private IDemoService demoService;

	public void edit(HttpServletRequest req, HttpServletResponse resp,
					 String name){
		String result = demoService.get(name);
		try {
			resp.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
