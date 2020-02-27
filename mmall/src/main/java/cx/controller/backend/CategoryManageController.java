package cx.controller.backend;

import cx.common.Const;
import cx.common.ServerResponse;
import cx.pojo.User;
import cx.service.CategoryService;
import cx.utils.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;


@RestController
@RequestMapping("/manage/category/")
public class CategoryManageController {

	@Autowired
	private CategoryService categoryService;


	@RequestMapping("add_category.do")
	public ServerResponse adminAddCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
		String role = UUIDUtil.getKey("aop");
		if (Const.SUCCESS.equals(role)) {
			return categoryService.addCategory(categoryName, parentId);
		}
		return ServerResponse.ErrorMessage(role);
	}


	@RequestMapping("set_category_name.do")
	public ServerResponse adminSetCategoryName(Integer categoryId, String categoryName) {
		String role = UUIDUtil.getKey("aop");
		if (Const.SUCCESS.equals(role)) {
			return categoryService.updateCategoryName(categoryId, categoryName);
		}
		return ServerResponse.ErrorMessage(role);
	}


	@RequestMapping("get_category.do")
	public ServerResponse adminGetChildrenParallelCategory(@RequestParam(value = "categoryId", defaultValue ="0") Integer categoryId) {
		String role = UUIDUtil.getKey("aop");
		if (Const.SUCCESS.equals(role)) {
			//查询子节点的category,并且不递归,保持平级
			return categoryService.getChildrenParallelCategory(categoryId);
		}
		return ServerResponse.ErrorMessage(role);
	}

	@RequestMapping("get_deep_category.do")
	public ServerResponse adminGetCategoryAndDeepChildrenCategory(@RequestParam(value = "categoryId", defaultValue ="0") Integer categoryId) {
		String role = UUIDUtil.getKey("aop");
		if (Const.SUCCESS.equals(role)) {
			//查询子节点的id和递归子节点的Id
			return categoryService.selectCategoryAndChildrenById(categoryId);
		}
		return ServerResponse.ErrorMessage(role);
	}


}
