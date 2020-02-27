package cx.service;

import com.github.pagehelper.PageInfo;
import cx.common.ServerResponse;
import cx.vo.OrderVo;

import java.util.Map;

public interface OrderService {
	ServerResponse createOrder(Integer userId, Integer shippingId);

	ServerResponse<String> cancel(Integer userId, Long orderNo);

	ServerResponse getOrderCartProduct(Integer userId);

	ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

	ServerResponse pay(Long orderNo, Integer userId, String path);

	ServerResponse aliCallback(Map<String, String> params);

	ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

	ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

	ServerResponse<PageInfo> manageList(int pageNum, int pageSize);

	ServerResponse<OrderVo> manageDetail(Long orderNo);

	ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize);

	ServerResponse<String> manageSendGoods(Long orderNo);
}
