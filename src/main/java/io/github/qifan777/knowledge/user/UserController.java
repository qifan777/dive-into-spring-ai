package io.github.qifan777.knowledge.user;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RequestMapping("user")
@RestController
@AllArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    @GetMapping
    public User userInfo() {
        return userMapper.selectById(StpUtil.getLoginIdAsString());
    }

    @PostMapping("login")
    public SaTokenInfo login(@RequestBody User input) {
        User databaseUser = userMapper.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getPhone, input.getPhone()));
        if (databaseUser == null) {
            throw new BusinessException("用户名/密码错误");
        }
        if (!BCrypt.checkpw(input.getPassword(), databaseUser.getPassword())) {
            throw new BusinessException("用户名/密码错误");
        }
        StpUtil.login(databaseUser.getId());
        return StpUtil.getTokenInfo();
    }

    @PostMapping("register")
    public SaTokenInfo register(@RequestBody User input) {
        Optional<User> byPhone = Optional.ofNullable(userMapper.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getPhone, input.getPhone())));
        if (byPhone.isPresent()) {
            throw new BusinessException("手机号已存在, 请登录");
        }
        input.setPassword(BCrypt.hashpw(input.getPassword()));
        userMapper.insertOrUpdate(input);
        StpUtil.login(input.getId());
        return StpUtil.getTokenInfo();
    }
}
