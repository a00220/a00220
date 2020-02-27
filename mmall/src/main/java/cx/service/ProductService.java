package cx.service;

import com.github.pagehelper.PageInfo;
import cx.common.ServerResponse;
import cx.pojo.Product;
import cx.vo.ProductDetailVo;

public interface ProductService {
	ServerResponse saveOrUpdateProduct(Product product);

	ServerResponse<String> setSaleStatus(Integer productId, Integer status);

	ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

	ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);

	ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
}
