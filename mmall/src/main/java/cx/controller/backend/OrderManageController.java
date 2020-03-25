package cx.controller.backend;


import com.github.pagehelper.PageInfo;
import cx.common.Const;
import cx.common.ResponseCode;
import cx.common.ServerResponse;
import cx.pojo.User;
import cx.service.OrderService;
import cx.service.UserService;
import cx.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/order/")
public class OrderManageController {

	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;


	@RequestMapping("list.do")
	public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
											  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");
		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充我们增加产品的业务逻辑
			return orderService.manageList(pageNum, pageSize);
		} else {
			return ServerResponse.ErrorMessage("无权限操作");
		}
	}

	@RequestMapping("detail.do")
	public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo) {

		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充我们增加产品的业务逻辑

			return orderService.manageDetail(orderNo);
		} else {
			return ServerResponse.ErrorMessage("无权限操作");
		}
	}


	@RequestMapping("search.do")
	public ServerResponse<PageInfo> orderSearch(HttpSession session, Long orderNo, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
												@RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充我们增加产品的业务逻辑
			return orderService.manageSearch(orderNo, pageNum, pageSize);
		} else {
			return ServerResponse.ErrorMessage("无权限操作");
		}
	}


	@RequestMapping("send_goods.do")
	public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo) {

		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充我们增加产品的业务逻辑
			return orderService.manageSendGoods(orderNo);
		} else {
			return ServerResponse.ErrorMessage("无权限操作");
		}
	}


}
