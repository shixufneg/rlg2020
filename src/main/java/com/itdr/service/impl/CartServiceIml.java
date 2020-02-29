package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.mapper.ProductMapper;
import com.itdr.mapper.ShoppingCarMapper;
import com.itdr.pojo.Product;
import com.itdr.pojo.ShoppingCar;
import com.itdr.pojo.User;
import com.itdr.pojo.vo.CartProductVo;
import com.itdr.pojo.vo.CartVO;
import com.itdr.service.CartService;
import com.itdr.utils.BigDecimalUtil;
import com.itdr.utils.ObjectToVoUtil;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceIml implements CartService {
    @Autowired
    ShoppingCarMapper shoppingCarMapper;
    @Autowired
    ProductMapper productMapper;
    //获取CartVO对象
    private CartVO getCartVO(List<ShoppingCar> carList){
        //获取购物车中 对应商品信息
        boolean bol = true;
        BigDecimal cartTotalPrice = new BigDecimal("0");
        List<CartProductVo> productList=new ArrayList<CartProductVo>();
        for (ShoppingCar cart : carList) {
            Product product = productMapper.selectByPraentKey(cart.getProductId());
            //把商品和购物车信息数据封装
            if(product != null){
                CartProductVo cartProductVo = ObjectToVoUtil.cartProductVo(cart, product);
                System.out.println();
                productList.add(cartProductVo);
                //ji算购物车总价
                cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
            }
            //判断购物车是否全选
            if(cart.getChecked() == 0){
                bol = false;
            }

        }

        //返回数据
        CartVO cartVO = ObjectToVoUtil.toCartVO(productList, bol, cartTotalPrice);

        return cartVO;
    }
    //获取用户购物车列表
    private ServerResponse<List<ShoppingCar>> getCarList(User user){
        List<ShoppingCar> carList = shoppingCarMapper.selectByUserID(user.getId());
        //用户购物车是否为空
        if(carList.size() == 0){
            return ServerResponse.defeatedRS(
                    ConstCode.CartEnum.EMPTY_CART.getCode(),
                    ConstCode.CartEnum.EMPTY_CART.getDesc());
        }
        return ServerResponse.successRS(carList);
    }
    //要添加的商品是否在售
    private ServerResponse<Product> olnine(Integer productId) {
        //要添加的商品是否在售
        Product p = productMapper.selectByPraentKey(productId);
        if(p == null || p.getStatus() != 1){
            return ServerResponse.defeatedRS(
                    ConstCode.ProductEnum.INEXISTENCE_PRODUCT.getCode(),
                    ConstCode.ProductEnum.INEXISTENCE_PRODUCT.getDesc()
            );
        }
        return ServerResponse.successRS(p);
    }
    //返回成功后封装好的数据
    private ServerResponse getSuccess(User u){
        //返回分装好的cartVo
        //查询用户的购物车信息
        ServerResponse<List<ShoppingCar>> carList = getCarList(u);
        if(!carList.isSuccess()){
            return carList;
        }
        CartVO cartVO = getCartVO(carList.getData());
        return ServerResponse.successRS(cartVO);
    }
    //更新商品数量
    @Override
    public ServerResponse update(Integer productId, Integer count,Integer type, User u) {
        //参数非空
        //参数合法判断
        if(productId == null || productId < 0){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UNLAWFULNESS_PARAM);
        }
        if(count == null || count<= 0){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UNLAWFULNESS_PARAM);
        }
        //要添加的商品是否在售
        //要添加的商品是否在售
        ServerResponse<Product> olnine = olnine(productId);
        if(!olnine.isSuccess()){
            return olnine;
        }
        //查询要增加的数据是否已经存在
        ShoppingCar cart = shoppingCarMapper.selectByUserIDAndProductID(u.getId(),productId);
            //根据type判断要执行的更新方法
            if(type ==ConstCode.CartEnum.CART_TYPE.getCode()){
                cart.setQuantity(count+cart.getQuantity());
            }else if(type ==1){
                cart.setQuantity(count);
            }
            //更新数据库数据
            int i = shoppingCarMapper.updateByPrimaryKey(cart);
            if(i <= 0){
                return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                        ConstCode.CartEnum.FAIL_UPDATEPRODUCT.getDesc());
            }
        return null;
    }
    //选中
    @Override
    public ServerResponse checked(Integer productId, Integer type, User u) {
        if(productId == null || productId < 0){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UNLAWFULNESS_PARAM);
        }
        if(type == null || type < 0){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UNLAWFULNESS_PARAM);
        }
        //选中或者取消选中 针对一个商品
        int i = shoppingCarMapper.updateByUserIDrRProductId(u.getId(),productId,type);
        if(i <= 0){
            ServerResponse.defeatedRS(ConstCode.CartEnum.FAIL_CHECKED.getCode(),
                    ConstCode.CartEnum.FAIL_CHECKED.getDesc());
        }
        return list(u);
    }
    //查询购物车中商品数量
    @Override
    public ServerResponse getCartProductCoun(User u) {
        List<ShoppingCar> carList = shoppingCarMapper.selectByUserID(u.getId());
        Integer i = 0;
        for (ShoppingCar shoppingCar : carList) {
            i += shoppingCar.getQuantity();
        }
        return ServerResponse.successRS(i);
    }

    //删除所有
    @Override
    public ServerResponse delectAll(User u) {
        int i = shoppingCarMapper.deleteByUserIdAndChecked(u.getId());
        if(i <= 0 ){
            return ServerResponse.defeatedRS(
                    ConstCode.CartEnum.FAIL_DELECTALL.getCode(),
                    ConstCode.CartEnum.FAIL_DELECTALL.getDesc()
                    );
        }
        return getSuccess(u);
    }

    //删除商品
    @Override
    public ServerResponse delect(Integer productId, User u) {
        //参数合法判断
        if(productId == null || productId < 0){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UNLAWFULNESS_PARAM);
        }
        //移除购物车中对应商品
        int i = shoppingCarMapper.deleteByUserIDAndProductID(productId,u.getId());
        if(i <= 0){
            return ServerResponse.defeatedRS(
                    ConstCode.CartEnum.FAIL_DELECT.getCode(),
                    ConstCode.CartEnum.FAIL_DELECT.getDesc());
        }
       return  getSuccess(u);
    }

    //添加商品
    @Override
    public ServerResponse add(Integer productid, Integer count, Integer type,User u) {
        //参数合法判断
        if(productid == null || productid < 0){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UNLAWFULNESS_PARAM);
        }
        if(count == null || count < 0){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UNLAWFULNESS_PARAM);
        }
        //要添加的商品是否在售
        ServerResponse<Product> olnine = olnine(productid);
        if(!olnine.isSuccess()){
            return olnine;
        }

        //添加的商品有没有超出库存
        if(count > olnine.getData().getStock()){
            return ServerResponse.defeatedRS(
                    ConstCode.ProductEnum.BEYOND_STOCK.getCode(),
                    ConstCode.ProductEnum.BEYOND_STOCK.getDesc()
            );
        }
        //参数合法时，向购物车增加一条数据
        ShoppingCar c = new ShoppingCar();
        c.setUserId(u.getId());
        c.setProductId(productid);
        c.setQuantity(count);
        //查询要增加的数据是否已经存在
        ShoppingCar cart = shoppingCarMapper.selectByUserIDAndProductID(u.getId(),productid);
        if(cart == null){
            int insert = shoppingCarMapper.insert(c);
            if(insert <= 0){
                return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                        ConstCode.CartEnum.FAIL_ADDPRODUCY.getDesc());
            }
        }else{
            //根据type判断要执行的更新方法
            if(type ==ConstCode.CartEnum.CART_TYPE.getCode()){
                cart.setQuantity(count+cart.getQuantity());
            }else if(type ==1){
                cart.setQuantity(count);
            }
            //更新数据库数据
            int i = shoppingCarMapper.updateByPrimaryKey(cart);
            if(i <= 0){
                return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                        ConstCode.CartEnum.FAIL_UPDATEPRODUCT.getDesc());
            }
        }

        //返回分装好的cartVo
        //查询用户的购物车信息
        ServerResponse<List<ShoppingCar>> carList = getCarList(u);
        if(!carList.isSuccess()){
            return carList;
        }
        CartVO cartVO = getCartVO(carList.getData());
        return ServerResponse.successRS(cartVO);
    }
    //购物车展示
    @Override
    public ServerResponse list(User user) {
        ServerResponse<List<ShoppingCar>> carList = getCarList(user);
        if(!carList.isSuccess()){
            return carList;
        }
        CartVO cartVO = getCartVO(carList.getData());
        return ServerResponse.successRS(cartVO);
    }
}
