package cx.service.impl;


import cx.common.Const;
import cx.common.ServerResponse;
import cx.dao.UserMapper;
import cx.pojo.User;
import cx.service.UserService;
import cx.utils.MD5Util;
import cx.utils.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public ServerResponse<User> login(String username, String password) {
		int resultCount = userMapper.checkUsername(username);
		if (resultCount == 0) {
			return ServerResponse.ErrorMessage("用户名不存在");
		}

		String md5Password = MD5Util.MD5EncodeUtf8(password);
		User user = userMapper.selectLogin(username, md5Password);
		if (user == null) {
			return ServerResponse.ErrorMessage("密码错误");
		}

		user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
		return ServerResponse.Success("登录成功", user);
	}


	@Override
	public ServerResponse<String> register(User user) {
		ServerResponse result = this.checkValid(user.getEmail(), Const.EMAIL);
		if (!result.isSuccess()) {
			return result;
		}

		result = this.checkValid(user.getUsername(), Const.USERNAME);
		if (!result.isSuccess()) {
			return result;
		}

		user.setRole(Const.Role.ROLE_CUSTOMER);
		user.setId(Integer.valueOf(UUIDUtil.getUUID()));
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		int count = userMapper.insert(user);
		if (count == 0) {
			ServerResponse.ErrorMessage("注册失败");
		}
		return ServerResponse.Success("注册成功");
	}

	@Override
	public ServerResponse<String> checkValid(String str, String type) {
		if (StringUtils.isNoneBlank(type)) {
			if (Const.USERNAME.equals(type)) {
				int result = userMapper.checkUsername(str);
				if (result > 0) {
					return ServerResponse.ErrorMessage("用户名已存在");
				}
			}
			if (Const.EMAIL.equals(type)) {
				int result = userMapper.checkEmail(str);
				if (result > 0) {
					return ServerResponse.ErrorMessage("邮箱已存在");
				}
			}
			return ServerResponse.SuccessMessage("校验成功");
		}
		return ServerResponse.ErrorMessage("参数错误");
	}


	@Override
	public ServerResponse selectQuestion(String username) {
		ServerResponse serverResponse = this.checkValid(username, Const.USERNAME);
		if (serverResponse.isSuccess()) {
			return ServerResponse.ErrorMessage("用户不存在");
		}

		String question = userMapper.selectQuestionByUsername(username);
		if (StringUtils.isBlank(question)) {
			return ServerResponse.ErrorMessage("找回密码的问题不存在");
		}
		return ServerResponse.Success(question);
	}

	@Override
	public ServerResponse selectAnswer(String username, String question, String answer) {
		int result = userMapper.checkAnswer(username, question, answer);
		if (result == 0) {
			return ServerResponse.ErrorMessage("问题答案错误");
		}
		String forgetToken = UUID.randomUUID().toString();
		UUIDUtil.setKey("to" + username, forgetToken);
		return ServerResponse.Success(forgetToken);
	}


	@Override
	public ServerResponse forgetResetPassword(String username, String passwordNew, String forgetToken) {
		ServerResponse serverResponse = this.checkValid(username, Const.USERNAME);
		if (serverResponse.isSuccess()) {
			return ServerResponse.ErrorMessage("用户不存在");
		}
		if (StringUtils.isBlank(forgetToken)) {
			return ServerResponse.ErrorMessage("参数错误,token需要传递");
		}

		String token = UUIDUtil.getKey(UUIDUtil.TOKEN_PREFIX + username);
		if (StringUtils.isBlank(token)) {
			return ServerResponse.ErrorMessage("forget无效或过期");
		}

		//使用StringUtils.equals防止null值空指针异常
		if (StringUtils.equals(token, forgetToken)) {
			String md5password = MD5Util.MD5EncodeUtf8(passwordNew);
			int result = userMapper.updatePasswordByUsername(username, md5password);
			if (result == 0) {
				return ServerResponse.ErrorMessage("更新密码失败");
			}
			return ServerResponse.SuccessMessage("更新密码成功");
		}
		return ServerResponse.ErrorMessage("参数错误");
	}

	@Override
	public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
		//防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
		int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
		if (resultCount == 0) {
			return ServerResponse.ErrorMessage("旧密码错误");
		}

		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
		int updateCount = userMapper.updateByPrimaryKeySelective(user);
		if (updateCount > 0) {
			return ServerResponse.SuccessMessage("密码更新成功");
		}
		return ServerResponse.ErrorMessage("密码更新失败");
	}

	@Override
	public ServerResponse<User> updateInformation(User user) {
		//username是不能被更新的
		//email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的.
		int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
		if (resultCount > 0) {
			return ServerResponse.ErrorMessage("email已存在,请更换email再尝试更新");
		}
		User updateUser = new User();
		updateUser.setId(user.getId());
		updateUser.setEmail(user.getEmail());
		updateUser.setPhone(user.getPhone());
		updateUser.setQuestion(user.getQuestion());
		updateUser.setAnswer(user.getAnswer());

		int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
		if (updateCount > 0) {
			return ServerResponse.Success("更新个人信息成功", updateUser);
		}
		return ServerResponse.ErrorMessage("更新个人信息失败");
	}

	@Override
	public ServerResponse<User> getInformation(Integer userId) {
		User user = userMapper.selectByPrimaryKey(userId);
		if (user == null) {
			return ServerResponse.ErrorMessage("找不到当前用户");
		}
		user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
		return ServerResponse.Success(user);

	}

	//backend

	/**
	 * 校验是否是管理员
	 *
	 * @param user
	 * @return
	 */
	@Override
	public ServerResponse checkAdminRole(User user) {
		if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
			return ServerResponse.Success();
		}
		return ServerResponse.Error();
	}

}
