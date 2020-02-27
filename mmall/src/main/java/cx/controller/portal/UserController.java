package cx.controller.portal;


import cx.common.Const;
import cx.common.ResponseCode;
import cx.common.ServerResponse;
import cx.pojo.User;
import cx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpSession;

@RequestMapping("/user/")
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "login.do",method = RequestMethod.GET)
	public ServerResponse<User> login(HttpSession session, String username, String password) {
		ServerResponse serverResponse = userService.login(username, password);
		if (serverResponse.isSuccess()) {
			session.setAttribute(Const.CURRENT_USER, serverResponse.getData());
		}
		return serverResponse;
	}

	@RequestMapping("logout.do")
	public ServerResponse<String> logout(HttpSession session) {
		session.removeAttribute(Const.CURRENT_USER);
		return ServerResponse.Success("登出成功");
	}

	@RequestMapping("register.do")
	public ServerResponse<String> register(User user) {
		return userService.register(user);
	}

	@RequestMapping(value = "check_valid.do")
	@ResponseBody
	public ServerResponse<String> checkValid(String str,String type){
		return userService.checkValid(str,type);
	}

	@RequestMapping("get_user_info.do")
	public ServerResponse<User> getUserInfo(HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user != null) {
			return ServerResponse.Success(user);
		}
		return ServerResponse.ErrorMessage("用户未登录,无法获取当前用户信息");
	}

	@RequestMapping(value = "get_information.do")
	public ServerResponse<User> get_information(HttpSession session){
		User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
		if(currentUser == null){
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
		}
		return userService.getInformation(currentUser.getId());
	}


	@RequestMapping("forget_get_question.do")
	public ServerResponse selectQuestion(String username) {
		return userService.selectQuestion(username);
	}

	@RequestMapping("forget_check_answer.do")
	public ServerResponse selectAnswer(String username, String question, String answer) {
		return userService.selectAnswer(username, question, answer);
	}


	@RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
	public ServerResponse<String> forgetRestPassword(String username, String passwordNew, String forget) {
		return userService.forgetResetPassword(username, passwordNew, forget);
	}



	@RequestMapping(value = "reset_password.do")
	public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.ErrorMessage("用户未登录");
		}
		return userService.resetPassword(passwordOld,passwordNew,user);
	}


	@RequestMapping(value = "update_information.do")
	public ServerResponse<User> update_information(HttpSession session,User user){
		User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
		if(currentUser == null){
			return ServerResponse.ErrorMessage("用户未登录");
		}
		user.setId(currentUser.getId());
		user.setUsername(currentUser.getUsername());
		ServerResponse<User> response = userService.updateInformation(user);
		if(response.isSuccess()){
			response.getData().setUsername(currentUser.getUsername());
			session.setAttribute(Const.CURRENT_USER,response.getData());
		}
		return response;
	}

}
