package cx.aop;

import cx.common.Const;
import cx.common.ResponseCode;
import cx.common.ServerResponse;
import cx.pojo.User;
import cx.utils.UUIDUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

@Aspect
@Component
public class Aspectj {


//	@Before(value = "execution(* cx.controller.backend.*Manage*.admin*(..))")
//	public void admin() {
//		System.out.println("进入了aop");
//		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//		HttpSession session=attr.getRequest().getSession(true);
//		User user = (User) session.getAttribute(Const.CURRENT_USER);
//		if (user == null) {
//			System.out.println("空的");
//			UUIDUtil.setKey("aop","用户未登录null");
//		} else {
//			if (user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
//				UUIDUtil.setKey("aop", "success");
//			} else {
//				UUIDUtil.setKey("aop","用户不是管理员");
//			}
//		}
//	}
}
