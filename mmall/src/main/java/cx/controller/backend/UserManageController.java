package cx.controller.backend;


import cx.common.Const;
import cx.common.ServerResponse;
import cx.pojo.User;
import cx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/user/")
public class UserManageController {


	@Autowired
	private UserService userService;


	@RequestMapping(value = "login.do")
	public ServerResponse<User> login(String username, String password, HttpSession session){
		ServerResponse<User> response = userService.login(username,password);
		if(response.isSuccess()){
			User user = response.getData();
			if(user.getRole() == Const.Role.ROLE_ADMIN){
				//说明登录的是管理员
				session.setAttribute(Const.CURRENT_USER,user);
				return response;
			}else{
				return ServerResponse.ErrorMessage("不是管理员,无法登录");
			}
		}
		return response;
	}



}
