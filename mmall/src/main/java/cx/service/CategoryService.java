package cx.service;

import cx.common.ServerResponse;
import cx.pojo.Category;

import java.util.List;

public interface CategoryService {
	ServerResponse addCategory(String categoryName, Integer parentId);

	ServerResponse updateCategoryName(Integer categoryId, String categoryName);

	ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

	ServerResponse selectCategoryAndChildrenById(Integer categoryId);
}
