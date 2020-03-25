package cx.controller.backend;

import com.google.common.collect.Maps;
import cx.common.Const;
import cx.common.ResponseCode;
import cx.common.ServerResponse;
import cx.pojo.Product;
import cx.pojo.User;
import cx.service.FileService;
import cx.service.ProductService;
import cx.service.UserService;
import cx.utils.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/manage/product/")
public class ProductManageController {

	@Autowired
	private UserService userService;
	@Autowired
	private ProductService productService;
	@Autowired
	private FileService fileService;

	@RequestMapping("save.do")
	public ServerResponse productSave(HttpSession session, Product product) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充我们增加产品的业务逻辑
			return productService.saveOrUpdateProduct(product);
		} else {
			return ServerResponse.ErrorMessage("无权限操作");
		}
	}

	@RequestMapping("set_sale_status.do")
	public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			return productService.setSaleStatus(productId, status);
		} else {
			return ServerResponse.ErrorMessage("无权限操作");
		}
	}

	@RequestMapping("detail.do")
	public ServerResponse getDetail(HttpSession session, Integer productId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充业务
			return productService.getProductDetail(productId);
		} else {
			return ServerResponse.ErrorMessage("无权限操作");
		}
	}

	@RequestMapping("list.do")
	public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充业务
			return productService.getProductList(pageNum, pageSize);
		} else {
			return ServerResponse.ErrorMessage("无权限操作");
		}
	}

	@RequestMapping("search.do")
	public ServerResponse productSearch(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");

		}
		if (userService.checkAdminRole(user).isSuccess()) {
			//填充业务
			return productService.searchProduct(productName, productId, pageNum, pageSize);
		} else {
			return ServerResponse.ErrorMessage("无权限操作");
		}
	}

	@RequestMapping("upload.do")
	public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.ErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");
		}
		if (userService.checkAdminRole(user).isSuccess()) {
			String path = request.getSession().getServletContext().getRealPath("upload");
			String targetFileName = fileService.upload(file, path);
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

			Map fileMap = Maps.newHashMap();
			fileMap.put("uri", targetFileName);
			fileMap.put("url", url);
			return ServerResponse.Success(fileMap);
		} else {
			return ServerResponse.ErrorMessage("无权限操作");
		}
	}


	@RequestMapping("richtext_img_upload.do")
	public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
		Map resultMap = Maps.newHashMap();
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			resultMap.put("success", false);
			resultMap.put("msg", "请登录管理员");
			return resultMap;
		}
		//富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
		if (userService.checkAdminRole(user).isSuccess()) {
			String path = request.getSession().getServletContext().getRealPath("upload");
			String targetFileName = fileService.upload(file, path);
			if (StringUtils.isBlank(targetFileName)) {
				resultMap.put("success", false);
				resultMap.put("msg", "上传失败");
				return resultMap;
			}
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
			resultMap.put("success", true);
			resultMap.put("msg", "上传成功");
			resultMap.put("file_path", url);
			response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
			return resultMap;
		} else {
			resultMap.put("success", false);
			resultMap.put("msg", "无权限操作");
			return resultMap;
		}
	}


}
