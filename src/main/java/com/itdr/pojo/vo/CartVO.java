package com.itdr.pojo.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
public class CartVO {
    private List<CartProductVo> cartProductVoList;
    private Boolean allCheck;
    private BigDecimal cartTotalPrice;
}
