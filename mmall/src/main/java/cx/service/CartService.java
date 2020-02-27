package cx.service;

import cx.common.ServerResponse;
import cx.vo.CartVo;

public interface CartService {
	ServerResponse addCart(Integer userId, Integer productId, Integer count);

	ServerResponse<CartVo> list(Integer userId);

	ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

	ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);

	ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

	ServerResponse<Integer> getCartProductCount(Integer userId);
}
