package com.itdr.config.pay;

import com.alipay.api.domain.GoodsDetail;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PGoodsDetail extends GoodsDetail {
    private static final long serialVersionUID = 7677251125598872494L;

    private String alipay_goods_id;

    private String body;

    private String categories_tree;

    private String goods_category;

    private String goods_id;

    private String goods_name;

    private String price;

    private Long quantity;

    private String show_url;
}
