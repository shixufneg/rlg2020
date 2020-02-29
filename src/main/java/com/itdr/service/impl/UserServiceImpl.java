package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.config.TokenCache;
import com.itdr.mapper.UserMapper;
import com.itdr.pojo.User;
import com.itdr.service.UserService;
import com.itdr.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    static String md5Code;
    @Autowired
    UserMapper userMapper;
    @Override
    public ServerResponse login(String username, String password) {
        if(StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc()
            );
    }
        if(StringUtils.isEmpty(password)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc()
            );
        }
//        MD5加密
        String md5Code = MD5Util.getMD5Code(password);
        //查询用户
    User u = userMapper.selectByUserNameAndPassword(username,md5Code);
        if(u == null){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.FAIL_LOGIN.getCode(),
                    ConstCode.UserEnum.FAIL_LOGIN.getDesc()
            );
        }
//        返回成功数据
        return ServerResponse.successRS(u);
    }

    @Override
    public ServerResponse<User> register(User u) {
//        参数非空判断
        if(StringUtils.isEmpty(u.getQuestion())){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.EMPTY_QUESTION.getCode(),
                    ConstCode.UserEnum.EMPTY_QUESTION.getDesc());
        }
        if(StringUtils.isEmpty(u.getEmail())){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,
                    "邮箱不能为空");
        }
        if(StringUtils.isEmpty(u.getUsername())){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
        if(StringUtils.isEmpty(u.getAnswer())){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.EMPTY_ANSWER.getCode(),
                    ConstCode.UserEnum.EMPTY_ANSWER.getDesc());
        }
        if(StringUtils.isEmpty(u.getPassword())){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }
        if(StringUtils.isEmpty(u.getPhone())){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    "手机号不能为空");
        }
//        查找用户是否已经注册
        int selectByUserName =userMapper.selectByUserName(u.getUsername());
        if(selectByUserName>0){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EXIST_USER.getCode(),
                    ConstCode.UserEnum.EXIST_USER.getDesc());
        }
//        检查用户名是否有效
        if(u.getUsername().length() > 12 || u.getUsername().length() < 3){
            return ServerResponse.defeatedRS(
              ConstCode.DEFAULT_FAIL,
              "用户名应在三到十二个字节"
            );
        }

//        MD5加密
        u.setPassword(MD5Util.getMD5Code(u.getPassword()));
//       注册信息
        int i = userMapper.insert(u);
        if(i<=0){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.FAIL_REGIST.getDesc());
        }
        return ServerResponse.successRS(ConstCode.UserEnum.SUCCESS_USER.getDesc());
    }

    @Override
    public ServerResponse<User> getU(String username,String password) {
        User u = userMapper.selectByUserNameAndPassword(username,password);
        return ServerResponse.successRS(u);
    }

    @Override
    public ServerResponse<User> checkValid(String str, String type) {
        //参数非空判断
        if(StringUtils.isEmpty(str)){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.FAIL_ISEMPTY.getDesc());
        }
        if(StringUtils.isEmpty(type)){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.FAIL_ISEMPTY.getDesc());
        }
        //查找用户名或者邮箱是否存在
        int i = userMapper.selectBYUSerNameOrEmail(str,type);
        if(i>0){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EXIST_USEROREMAIL.getCode(),
                    ConstCode.UserEnum.EXIST_USEROREMAIL.getDesc());
        }
        return ServerResponse.successRS(ConstCode.UserEnum.SUCCESS_MSG.getDesc());
    }

    @Override
    public ServerResponse<User> updateInformation(String email, String phone, String question, String answer,User user) {
        User u = new User();
        u.setId(user.getId());
        System.out.println(u.getId());
        u.setEmail(email);
        u.setPhone(phone);
        u.setQuestion(question);
        u.setAnswer(answer);
        int i = userMapper.updateByPrimaryKeySelective(u);
        if(i<0){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.FAIL_UPDATEiNFORMATION.getCode(),
                    ConstCode.UserEnum.FAIL_UPDATEiNFORMATION.getDesc());
        }
        return ServerResponse.successRS(ConstCode.UserEnum.SUCCESS_USERMSG.getDesc());
    }
//忘记密码
    @Override
    public ServerResponse<User> forGetQuestion(String username) {
        //参数非空判断
        if(StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
        //该用户是否存在
        User u = userMapper.selectByUserName2(username);
        if(u == null){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.INEXISTENCE_USER.getCode(),
                    ConstCode.UserEnum.INEXISTENCE_USER.getDesc()
            );
        }
        //获取用户密保问题
        String question = u.getQuestion();
        if(StringUtils.isEmpty(question)){
            return ServerResponse.defeatedRS(
              ConstCode.UserEnum.NO_QUESTION.getCode(),
              ConstCode.UserEnum.NO_QUESTION.getDesc()
            );
        }

        return ServerResponse.successRS(ConstCode.DEFAULT_SUCCESS,question);
    }

    @Override
    public ServerResponse<User> forCheckAnswer(String username, String question, String answer) {
        if(StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc()
            );
        }
        if(StringUtils.isEmpty(question)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_QUESTION.getCode(),
                    ConstCode.UserEnum.EMPTY_QUESTION.getDesc()
            );
        }
        if(StringUtils.isEmpty(answer)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_ANSWER.getCode(),
                    ConstCode.UserEnum.EMPTY_ANSWER.getDesc()
            );
        }
        //判断答案是否正确
        int i = userMapper.selectByUsernameAndQuestionAndAnswer(username,question,answer);
        if(i<=0){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.ERROR_ANSWER.getCode(),
                    ConstCode.UserEnum.ERROR_ANSWER.getDesc()
            );
        }
//        返回随机令牌
        String token = UUID.randomUUID().toString();
        //把令牌放入缓存中，这里使用的是Google的guava缓存，后期会使用redis替代
        TokenCache.set("token_" + username, token);
        return ServerResponse.successRS(ConstCode.DEFAULT_SUCCESS,token);
    }

    @Override
    public ServerResponse<User> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc()
            );
        }
        if(StringUtils.isEmpty(passwordNew)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc()
            );
        }
        if(StringUtils.isEmpty(forgetToken)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getCode(),
                    ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getDesc()
            );
        }
        //判断缓存中token是否过期，是否正确
        String s = TokenCache.get("token_" + username);
        if(s ==null || s.equals("")){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.LOSE_EFFICACY.getCode(),
                    ConstCode.UserEnum.LOSE_EFFICACY.getDesc()
                    );
        }
        if(!s.equals(forgetToken)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getCode(),
                    ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getDesc()
            );
        }
        //        MD5加密
        String md5Code = MD5Util.getMD5Code(passwordNew);
//        重置密码
        int i = userMapper.updateByUsernameAndPasswordNew(username,passwordNew);
        if(i <= 0){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getCode(),
                    ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getDesc()
            );
        }
        return ServerResponse.defeatedRS(
                ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getCode(),
                ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getDesc()
        );
    }

    @Override
    public ServerResponse<User> resetPassword(User user, String passwordOld, String passwordNew) {
//        非空
        if(StringUtils.isEmpty(passwordOld)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc()
            );
        }
        if(StringUtils.isEmpty(passwordNew)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc()
            );
        }
        //MD5加密
        String md5passwordOld = MD5Util.getMD5Code(passwordOld);
        String md5passwordNew = MD5Util.getMD5Code(passwordNew);
        //更新密码
        int i = userMapper.updateByUsernameAndPasswordNewAndPasswordOld(user.getUsername(),md5passwordOld,md5passwordNew);

        if(i <= 0){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getCode(),
                    ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getDesc()
            );
        }
        return ServerResponse.defeatedRS(
                ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getCode(),
                ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getDesc()
        );
    }
}
