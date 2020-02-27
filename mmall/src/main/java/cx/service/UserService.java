package cx.service;

import cx.common.ServerResponse;
import cx.pojo.User;

import javax.servlet.http.HttpSession;

public interface UserService {
	ServerResponse<User> login( String username, String password);

	ServerResponse<String> register(User user);

	ServerResponse<String> checkValid(String str, String type);

	ServerResponse selectQuestion(String username);

	ServerResponse selectAnswer(String username, String question, String answer);

	ServerResponse forgetResetPassword(String username, String passwordNew, String forgetToken);

	ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

	ServerResponse<User> updateInformation(User user);

	ServerResponse<User> getInformation(Integer userId);

	ServerResponse checkAdminRole(User user);
}
