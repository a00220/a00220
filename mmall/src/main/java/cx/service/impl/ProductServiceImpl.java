package cx.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import cx.common.Const;
import cx.common.ResponseCode;
import cx.common.ServerResponse;
import cx.dao.CategoryMapper;
import cx.dao.ProductMapper;
import cx.pojo.Category;
import cx.pojo.Product;
import cx.service.ProductService;
import cx.utils.DateTimeUtil;
import cx.utils.PropertiesUtil;
import cx.vo.ProductDetailVo;
import cx.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductMapper productMapper;

	@Autowired
	private CategoryMapper categoryMapper;

	@Override
	public ServerResponse saveOrUpdateProduct(Product product) {
		if (product != null) {
			if (StringUtils.isNoneBlank(product.getSubImages())) {
//				split分隔符划分元素
				String[] suImageArray = product.getSubImages().split(",");
				if (suImageArray.length > 0) {
					product.setMainImage(suImageArray[0]);
				}
			}

			if (product.getId() != null) {
				int result = productMapper.updateByPrimaryKeySelective(product);
				if (result == 0) {
					return ServerResponse.ErrorMessage("更新失败");
				}
				return ServerResponse.SuccessMessage("更新产品成功");
			} else {
				int result = productMapper.insert(product);
				if (result == 0) {
					return ServerResponse.ErrorMessage("新增失败");
				}
				return ServerResponse.SuccessMessage("新增产品成功");
			}

		}
		return ServerResponse.ErrorMessage("新增或更新产品参数不正确");
	}

	@Override
	public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
		if (productId == null || status == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = new Product();
		product.setId(productId);

		product.setStatus(status);
		int result = productMapper.updateByPrimaryKeySelective(product);
		if (result == 0) {
			return ServerResponse.ErrorMessage("更新失败");
		}
		return ServerResponse.SuccessMessage("更新成功");
	}

	public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
		if (productId == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if (product == null) {
			return ServerResponse.ErrorMessage("产品已下架或者删除");
		}

		ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		return ServerResponse.Success(productDetailVo);

	}

	private ProductDetailVo assembleProductDetailVo(Product product) {
		ProductDetailVo productDetailVo = new ProductDetailVo();
		productDetailVo.setId(product.getId());
		productDetailVo.setSubtitle(product.getSubtitle());
		productDetailVo.setPrice(product.getPrice());
		productDetailVo.setMainImage(product.getMainImage());
		productDetailVo.setSubImages(product.getSubImages());
		productDetailVo.setCategoryId(product.getCategoryId());
		productDetailVo.setDetail(product.getDetail());
		productDetailVo.setName(product.getName());
		productDetailVo.setStatus(product.getStatus());
		productDetailVo.setStock(product.getStock());

		productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));

		Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
		if (category == null) {
			productDetailVo.setParentCategoryId(0);//默认根节点
		} else {
			productDetailVo.setParentCategoryId(category.getParentId());
		}

		productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
		productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
		return productDetailVo;
	}

	@Override
	public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Product> productList = productMapper.selectList();
		List<ProductListVo> productListVoList = Lists.newArrayList();
		for (Product product : productList) {
			ProductListVo productListVo = assembleProductListVo(product);
			productListVoList.add(productListVo);
		}
		PageInfo pageInfo = new PageInfo(productList);
		pageInfo.setList(productListVoList);
		return ServerResponse.Success(pageInfo);
	}

	private ProductListVo assembleProductListVo(Product product) {
		ProductListVo productListVo = new ProductListVo();
		productListVo.setId(product.getId());
		productListVo.setName(product.getName());
		productListVo.setCategoryId(product.getCategoryId());
		productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
		productListVo.setMainImage(product.getMainImage());
		productListVo.setPrice(product.getPrice());
		productListVo.setSubtitle(product.getSubtitle());
		productListVo.setStatus(product.getStatus());
		return productListVo;
	}

	@Override
	public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
		if (StringUtils.isNoneBlank(productName)) {
			productName = new StringBuilder().append("%").append(productName).append("%").toString();
		}
		List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
		List<ProductListVo> productListVoList = Lists.newArrayList();
		for (Product product : productList) {
			ProductListVo productListVo = assembleProductListVo(product);
			productListVoList.add(productListVo);
		}
		PageInfo pageInfo = new PageInfo(productList);
		pageInfo.setList(productListVoList);
		return ServerResponse.Success(pageInfo);
	}

	@Override
	public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
		if (productId == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if (product == null) {
			return ServerResponse.ErrorMessage("产品已下架或者删除");
		}
		if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
			return ServerResponse.ErrorMessage("产品已下架或者删除");
		}
		ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		return ServerResponse.Success(productDetailVo);
	}







}
