package com.itdr.mapper;

import com.itdr.pojo.Category;
import com.itdr.pojo.Product;
import com.itdr.pojo.ProductWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductWithBLOBs record);

    int insertSelective(ProductWithBLOBs record);

    ProductWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(ProductWithBLOBs record);

    int updateByPrimaryKey(Product record);

    List<Category> selectByPraentID(Integer pid);

    Product selectByPraentKey(Integer productid);

    List<Product> selectByName(@Param("keyword") String keyword);
}