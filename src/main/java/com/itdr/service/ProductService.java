package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.Category;
import com.itdr.pojo.Product;
import com.itdr.pojo.vo.ProductVo;

public interface ProductService {

    ServerResponse<Category> baseCategory(Integer pid);

    ServerResponse<ProductVo> detail(Integer productid);

    ServerResponse<ProductVo> list(String keyword,Integer pageNum,Integer pageSize,String orderBy);
}
