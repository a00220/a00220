package cx.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import cx.common.Const;
import cx.common.ResponseCode;
import cx.common.ServerResponse;
import cx.dao.CartMapper;
import cx.dao.ProductMapper;
import cx.pojo.Cart;
import cx.pojo.Product;
import cx.service.CartService;
import cx.service.ProductService;
import cx.utils.BigDecimalUtil;
import cx.utils.PropertiesUtil;
import cx.vo.CartProductVo;
import cx.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private ProductMapper productMapper;

	@Override
	public ServerResponse addCart(Integer userId, Integer productId, Integer count) {
		if (count == null && productId == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
		if (cart == null) {
//			购物车不存在,需要创建
			Cart cartItem = new Cart();
			cartItem.setQuantity(count);
			cartItem.setUserId(userId);
			cartMapper.insert(cartItem);
		} else {
			count = cart.getQuantity() + count;
			cart.setQuantity(count);
			cartMapper.updateByPrimaryKeySelective(cart);
		}
		return this.list(userId);
	}

	public CartVo getCartVoLimit(Integer userId) {
		CartVo cartVo = new CartVo();
		List<Cart> cartList = cartMapper.selectCartByUserId(userId);

		List<CartProductVo> cartProductVoList = Lists.newArrayList();
		BigDecimal cartTotalPrice = new BigDecimal("0");

		if (CollectionUtils.isNotEmpty(cartList)) {
			for (Cart cartItem : cartList) {
				CartProductVo cartProductVo = new CartProductVo();
				cartProductVo.setId(cartItem.getId());
				cartProductVo.setUserId(cartItem.getUserId());
				cartProductVo.setProductId(cartItem.getProductId());

				Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
				if (product != null) {
					cartProductVo.setProductMainImage(product.getMainImage());
					cartProductVo.setProductName(product.getName());
					cartProductVo.setProductSubtitle(product.getSubtitle());
					cartProductVo.setProductStatus(product.getStatus());
					cartProductVo.setProductPrice(product.getPrice());
					cartProductVo.setProductStock(product.getStock());

					//判断库存
					int buyLimitCount = 0;
					if (product.getStock() >= cartItem.getQuantity()) {
						buyLimitCount = cartItem.getQuantity();
						cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
					} else {
						buyLimitCount = product.getStock();
						cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);

						Cart cartForQuantity = new Cart();
						cartForQuantity.setId(cartItem.getId());
						cartForQuantity.setQuantity(buyLimitCount);
						cartMapper.updateByPrimaryKeySelective(cartForQuantity);
					}
					cartProductVo.setQuantity(buyLimitCount);
					//计算总价
					cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getStock().doubleValue(), product.getPrice().doubleValue()));
					cartProductVo.setProductChecked(cartItem.getChecked());
				}
				if (cartItem.getChecked() == Const.Cart.CHECKED) {
					//如果已经勾选,增加到整个的购物车总价中
					cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
				}
				cartProductVoList.add(cartProductVo);
			}
		}
		cartVo.setCartTotalPrice(cartTotalPrice);
		cartVo.setCartProductVoList(cartProductVoList);
		cartVo.setAllChecked(this.getAllCheckedStatus(userId));
		cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

		return cartVo;


	}

	@Override
	public ServerResponse<CartVo> list(Integer userId) {
		CartVo cartVo = this.getCartVoLimit(userId);
		return ServerResponse.Success(cartVo);
	}

	@Override
	public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
		if (productId == null || count == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
		if (cart != null) {
			cart.setQuantity(count);
		}
		cartMapper.updateByPrimaryKey(cart);
		return this.list(userId);
	}

	@Override
	public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
		List<String> productList = Splitter.on(",").splitToList(productIds);
		if (CollectionUtils.isEmpty(productList)) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		cartMapper.deleteByUserIdProductIds(userId, productList);
		return this.list(userId);
	}

	private boolean getAllCheckedStatus(Integer userId) {
		if (userId == null) {
			return false;
		}
		return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;

	}

	@Override
	public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
		cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
		return this.list(userId);
	}

	@Override
	public ServerResponse<Integer> getCartProductCount(Integer userId) {
		if (userId == null) {
			return ServerResponse.Success(0);
		}
		return ServerResponse.Success(cartMapper.selectCartProductCount(userId));
	}


}
