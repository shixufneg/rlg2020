package com.itdr.controller;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.pojo.User;
import com.itdr.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@ResponseBody
@RequestMapping("/portall/car")
public class CartController {
    @Autowired
    CartService cartService;

    /**
     * 查看购物车列表
     * @param session
     * @return
     */
    @RequestMapping("list.do")
    public ServerResponse list(HttpSession session){
        //判断用户是否登录
        User u = (User) session.getAttribute("user");
        if(u == null){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.list(u);
    }

    /**
     * 增加商品
     * @param productId
     * @param count
     * @param session
     * @return
     */
    @RequestMapping("add.do")
    public ServerResponse add(
            Integer productId,
            @RequestParam(value = "count",required = false,defaultValue = "1") Integer count,
            @RequestParam(value = "type",required = false,defaultValue = "0") Integer type,
            HttpSession session){
        //判断用户是否登录
        User u = (User) session.getAttribute("user");
        if(u == null){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.add(productId,count,type,u);
    }

    /**
     * 购物车更新商品数量
     * @param productId
     * @param count
     * @param type
     * @param session
     * @return
     */
    @RequestMapping("update.do")
    public ServerResponse update(
            Integer productId,
            @RequestParam(value = "count",required = false,defaultValue = "1") Integer count,
            @RequestParam(value = "type",required = false,defaultValue = "0") Integer type,
            HttpSession session){
        //判断用户是否登录
        User u = (User) session.getAttribute("user");
        if(u == null){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.update(productId,count,type,u);
    }

    /**
     * 删除一条数据
     * @param productId
     * @param session
     * @return
     */
        @RequestMapping("delect.do")
    public ServerResponse delect(
            Integer productId,HttpSession session){
        //判断用户是否登录
        User u = (User) session.getAttribute("user");
        if(u == null){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.delect(productId,u);
    }

    /**
     * 选出选中数据
     * @param session
     * @return
     */
    @RequestMapping("delectAll.do")
    public ServerResponse delectAll(HttpSession session){
        //判断用户是否登录
        User u = (User) session.getAttribute("user");
        if(u == null){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.delectAll(u);
    }

    /**
     *查询购物车中商品数量
     * @param session
     * @return
     */
    @RequestMapping("get_cart_product_count.do")
    public ServerResponse getCartProductCoun(HttpSession session){
        //判断用户是否登录
        User u = (User) session.getAttribute("user");
        if(u == null){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.getCartProductCoun(u);
    }


    @RequestMapping("checked.do")
    public ServerResponse checked(Integer productId,
                                  @RequestParam(value = "type",required = false,defaultValue = "0")Integer type,
                                  HttpSession session){
        //判断用户是否登录
        User u = (User) session.getAttribute("user");
        if(u == null){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.checked(productId,type,u);
    }
}
