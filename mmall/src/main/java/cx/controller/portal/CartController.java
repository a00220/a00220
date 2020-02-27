package cx.controller.portal;

import cx.common.Const;
import cx.common.ResponseCode;
import cx.common.ServerResponse;
import cx.pojo.User;
import cx.service.CartService;
import cx.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart/")
public class CartController {

	@Autowired
	private CartService cartService;



	@RequestMapping("list.do")
	public ServerResponse<CartVo> list(HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return cartService.list(user.getId());
	}

	@RequestMapping("add.do")
	public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return cartService.addCart(user.getId(),productId,count);
	}



	@RequestMapping("update.do")
	public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return cartService.update(user.getId(),productId,count);
	}

	@RequestMapping("delete_product.do")
	public ServerResponse<CartVo> deleteProduct(HttpSession session,String productIds){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return cartService.deleteProduct(user.getId(),productIds);
	}


	@RequestMapping("select_all.do")
	public ServerResponse<CartVo> selectAll(HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return cartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
	}

	@RequestMapping("un_select_all.do")
	public ServerResponse<CartVo> unSelectAll(HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return cartService.selectOrUnSelect(user.getId(),null,Const.Cart.UN_CHECKED);
	}



	@RequestMapping("select.do")
	public ServerResponse<CartVo> select(HttpSession session,Integer productId){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return cartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
	}

	@RequestMapping("un_select.do")
	public ServerResponse<CartVo> unSelect(HttpSession session,Integer productId){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return cartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UN_CHECKED);
	}



	@RequestMapping("get_cart_product_count.do")
	public ServerResponse<Integer> getCartProductCount(HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user ==null){
			return ServerResponse.Success(0);
		}
		return cartService.getCartProductCount(user.getId());
	}


}
