package com.itdr.mapper;

import com.itdr.pojo.ShoppingCar;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShoppingCarMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShoppingCar record);

    int insertSelective(ShoppingCar record);

    ShoppingCar selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ShoppingCar record);

    int updateByPrimaryKey(ShoppingCar record);

    List<ShoppingCar> selectByUserID(Integer id);

    ShoppingCar selectByUserIDAndProductID(@Param("userId") Integer id,
                                           @Param("productId") Integer productid);

    int deleteByUserIDAndProductID(
                                    @Param("productId")Integer productId,
                                    @Param("id")Integer id);

    int deleteByUserIdAndChecked(Integer id);

    int updateByUserIDrRProductId(@Param("id")Integer id,
                                  @Param("productId")Integer productId,
                                  @Param("type")Integer type
    );
}