package com.sun.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sun.entity.PageResult;
import com.sun.entity.Result;
import com.sun.pojo.TbUser;
import com.sun.user.service.UserService;
import com.sun.util.PhoneFormatCheckUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户表controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference(timeout = 10000)
	private UserService userService;
	

	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String smscode){
		boolean checkSmsCode = userService.checkSmsCode(user.getPhone(), smscode);
		if(checkSmsCode==false){
			return new Result(false,"验证码输入错误");
		}
		try {
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 发送短信验证码
	 * @param phone
	 * @return
	 */
	@RequestMapping("/sendCode")
	public Result sendCode(String phone){
		//判断手机号格式
		if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
			return new Result(false,"手机号码格式不正确");
		}

		try {
			userService.createSmsCode(phone);//生成验证码
			return new Result(true,"验证码发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"验证码发送失败");
		}

	}

	@RequestMapping("/findUserName")
	public Map findUserName(){

		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map map = new HashMap();
		map.put("loginName",name);
		return map;
	}
	
}
