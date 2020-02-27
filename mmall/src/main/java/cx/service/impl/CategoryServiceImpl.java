package cx.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cx.common.ServerResponse;
import cx.dao.CategoryMapper;
import cx.pojo.Category;
import cx.service.CategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements CategoryService {


	private static Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

	@Autowired
	private CategoryMapper categoryMapper;


	@Override
	public ServerResponse addCategory(String categoryName, Integer parentId) {

		if (parentId == null || StringUtils.isBlank(categoryName)) {
			return ServerResponse.ErrorMessage("添加品类参数错误");
		}

		Category category = new Category();
		category.setName(categoryName);
		category.setParentId(parentId);
		category.setStatus(true);

		int rowCount = categoryMapper.insert(category);
		if (rowCount == 0) {
			return ServerResponse.ErrorMessage("添加分类失败");
		}
		return ServerResponse.SuccessMessage("添加成功");
	}

	@Override
	public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
		if (categoryId == null || StringUtils.isBlank(categoryName)) {
			return ServerResponse.ErrorMessage("更新品类参数错误");
		}
		Category category = new Category();
		category.setId(categoryId);
		category.setName(categoryName);
		int result = categoryMapper.updateByPrimaryKeySelective(category);
		if (result == 0) {
			return ServerResponse.ErrorMessage("更新品类失败");
		}
		return ServerResponse.SuccessMessage("更新品类成功");
	}

	@Override
	public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		if (CollectionUtils.isEmpty(categoryList)) {
			log.info("未找到当前分类的子类");
		}
		return ServerResponse.Success(categoryList);
	}

	@Override
	public ServerResponse selectCategoryAndChildrenById(Integer categoryId) {
		Set<Category> categorySet = Sets.newHashSet();
		findChildCategory(categorySet, categoryId);

		List<Integer> categoryIdList = Lists.newArrayList();
		if (categoryId != null) {
			for (Category categoryItem : categorySet) {
				categoryIdList.add(categoryItem.getId());
			}
		}
		return ServerResponse.Success(categoryIdList);
	}



	private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId) {
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		if (category != null) {
			categorySet.add(category);
		}
		//查找子节点,递归算法一定要有一个退出的条件
		//mybatis返回集合不会返回一个null
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		for (Category categoryItem : categoryList) {
			findChildCategory(categorySet, categoryItem.getId());
		}
		return categorySet;
	}

}
