package com.itdr.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.mapper.CategoryMapper;
import com.itdr.mapper.ProductMapper;
import com.itdr.pojo.Category;
import com.itdr.pojo.Product;
import com.itdr.pojo.vo.ProductVo;
import com.itdr.service.ProductService;
import com.itdr.utils.ObjectToVoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    //获取商品直接分类
    @Override
    public ServerResponse<Category> baseCategory(Integer pid) {
        //参数合法行
        if(pid ==null || pid <0){
            return ServerResponse.defeatedRS(ConstCode.ProductEnum.UNLAWFULNESS_PARAM.getCode(),
                    ConstCode.ProductEnum.UNLAWFULNESS_PARAM.getDesc());
        }
        //g根据父id查找直接子类
        List<Category> li = categoryMapper.selectByPraentID(pid);
        //返回成功数据

        return ServerResponse.successRS(li);
    }

    /**
     * 模糊查询
     * @param word
     * @return
     */
    @Override
    public ServerResponse<ProductVo> list(String word,Integer pageNum,Integer pageSize,String orderBy) {
        if(StringUtils.isEmpty(word)){
            return ServerResponse.defeatedRS(
                    ConstCode.ProductEnum.ISEMPTY_PARAMETER.getCode(),
                    ConstCode.ProductEnum.UNLAWFULNESS_PARAM.getDesc()
            );
        }
        //排序参数处理
        String[] split = new String[2];
        if(!StringUtils.isEmpty(orderBy)){
            split = orderBy.split("_");
        }
        //模糊查询数据
        String keyword = "%"+word+"%";
        //开启分页
        PageHelper.startPage(pageNum,pageSize,split[0]+" "+split[1]);
        List<Product> list = productMapper.selectByName(keyword);
        PageInfo pageInfo = new PageInfo(list);
        //封装VO
        List<ProductVo> listNew = new ArrayList<ProductVo>();
        for(Product product : list){
            ProductVo productVo = ObjectToVoUtil.ProductToUserVo(product);
            listNew.add(productVo);
        }
        pageInfo.setList(listNew);
        //返回成功数据
        return ServerResponse.successRS(pageInfo);
    }

    /**
     * 商品查询详情
     * @param productid
     * @return
     */
    @Override
    public ServerResponse<ProductVo> detail(Integer productid) {
        //参数合法行
        if(productid ==null || productid <0){
            return ServerResponse.defeatedRS(ConstCode.ProductEnum.UNLAWFULNESS_PARAM.getCode(),
                    ConstCode.ProductEnum.UNLAWFULNESS_PARAM.getDesc());
        }
        //根据id查找直接子类
        Product p  = productMapper.selectByPraentKey(productid);
        if(p == null ||p.getStatus() != 1){
            return ServerResponse.defeatedRS(
                    ConstCode.ProductEnum.INEXISTENCE_PRODUCT.getCode(),
                    ConstCode.ProductEnum.INEXISTENCE_PRODUCT.getDesc()
            );
        }
        //分装VO
        ProductVo productVo = ObjectToVoUtil.ProductToUserVo(p);
        return ServerResponse.successRS(productVo);
    }
}
