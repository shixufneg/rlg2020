package com.itdr.controller;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.pojo.User;
import com.itdr.pojo.vo.UserVo;
import com.itdr.service.UserService;
import com.itdr.utils.ObjectToVoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@ResponseBody
@RequestMapping("/portal/usere/")
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping("login.do")
    public ServerResponse<User> login(String username, String password, HttpSession session){

        ServerResponse sr = userService.login(username,password);
        if(sr.isSuccess()){
            session.setAttribute("user",sr.getData());
        }
        return sr;
    }

    /**
     * 注册
     * @param u
     * @return
     */
    @RequestMapping("register.do")
    public ServerResponse<User> register(User u){
        return userService.register(u);
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @RequestMapping("outlogin.do")
    public ServerResponse outLogin(HttpSession session){
//        // 注销用户，使session失效。
//        session.invalidate();
//        移除session中的某项属性
        session.removeAttribute("user");
//        User u = new User();
//        System.out.println(u.getUsername());
        return ServerResponse.successRS("退出成功");
    }

    /**
     * 获取当前登录的用户详细信息
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("login/getU.do")
    public ServerResponse<User> getU(String username,String password,HttpSession session){
        //判断用户是否登录
        User u = (User) session.getAttribute("user");
        if(u == null){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return userService.getU(username,password);
    }

    /**
     * 获取用户登录信息
     * @param session
     * @return
     */
    @RequestMapping("login/get_user_info.do")
    public ServerResponse<User> getUserInfo(HttpSession session){
        User u = (User) session.getAttribute("user");
        if(u == null){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        UserVo userVo = ObjectToVoUtil.UsertoUserVo(u);
        return ServerResponse.successRS(userVo);
    }

    /**
     * 检查邮箱或者用户名是否存在
     * @param str
     * @param type
     * @return
     */
    @RequestMapping("check_valid.do")
    public ServerResponse<User> checkValid(String str, String type){
        return userService.checkValid(str,type);
    }

    /**
     * 跟新个人信息
     * @param email
     * @param phone
     * @param question
     * @param answer
     * @param session
     * @param user
     * @return
     */
    @RequestMapping("login/update_information.do")
    public ServerResponse<User> updateInformation(String email,String phone,String question,String answer,User user,HttpSession session){
        user = (User) session.getAttribute("user");
        if(user == null){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }

        return userService.updateInformation(email,phone,question,answer,user);
    }

    /**
     * 忘记密码
     * @param username
     * @return
     */
    @RequestMapping("forget_get_question.do")
    public ServerResponse<User> forGetQuestion(String username){
        return userService.forGetQuestion(username);
    }

    /**
     * 提交问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping("forget_check_answer.do")
    public ServerResponse<User> forCheckAnswer(String username,String question,String answer){
        return userService.forCheckAnswer(username,question,answer);
    }

    @RequestMapping("forget_reset_password.do")
    public ServerResponse<User> forgetResetPassword(String username,String passwordNew,String forgetToken,HttpSession session){
        ServerResponse<User> userServerResponse = userService.forgetResetPassword(username, passwordNew, forgetToken);
        if(userServerResponse.isSuccess()){
            session.removeAttribute("user");
        }
        return userServerResponse;
    }

    /**
     * 登录改密码
     * @param passwordOld
     * @param passwordNew
     * @param session
     * @return
     */
    @RequestMapping("login/rest_password.do")
    public ServerResponse<User> resetPassword(String passwordOld,String passwordNew,HttpSession session){
//        判断用户是否登录
        User user = (User) session.getAttribute("user");
        if(user == null){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return userService.resetPassword(user,passwordOld,passwordNew);
    }

}
