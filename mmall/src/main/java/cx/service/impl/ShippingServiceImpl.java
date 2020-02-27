package cx.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import cx.common.ServerResponse;
import cx.dao.ShippingMapper;
import cx.pojo.Shipping;
import cx.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShippingServiceImpl implements ShippingService {
	
	@Autowired
	private ShippingMapper shippingMapper;

	@Override
	public ServerResponse add(Integer userId, Shipping shipping){
		shipping.setUserId(userId);
		int rowCount = shippingMapper.insert(shipping);
		if(rowCount > 0){
			Map result = Maps.newHashMap();
			result.put("shippingId",shipping.getId());
			return ServerResponse.Success("新建地址成功",result);
		}
		return ServerResponse.ErrorMessage("新建地址失败");
	}
	@Override
	public ServerResponse<String> del(Integer userId,Integer shippingId){
		int resultCount = shippingMapper.deleteByShippingIdUserId(userId,shippingId);
		if(resultCount > 0){
			return ServerResponse.Success("删除地址成功");
		}
		return ServerResponse.ErrorMessage("删除地址失败");
	}

	@Override
	public ServerResponse update(Integer userId, Shipping shipping){
		shipping.setUserId(userId);
		int rowCount = shippingMapper.updateByShipping(shipping);
		if(rowCount > 0){
			return ServerResponse.Success("更新地址成功");
		}
		return ServerResponse.ErrorMessage("更新地址失败");
	}
	@Override
	public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
		Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
		if(shipping == null){
			return ServerResponse.ErrorMessage("无法查询到该地址");
		}
		return ServerResponse.Success("更新地址成功",shipping);
	}

	@Override
	public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize){
		PageHelper.startPage(pageNum,pageSize);
		List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
		PageInfo pageInfo = new PageInfo(shippingList);
		return ServerResponse.Success(pageInfo);
	}




	
}
