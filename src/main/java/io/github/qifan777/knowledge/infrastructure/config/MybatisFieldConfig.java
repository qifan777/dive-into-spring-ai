package io.github.qifan777.knowledge.infrastructure.config;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MybatisFieldConfig implements MetaObjectHandler {

    /**
     * 使用mp做添加操作时候，这个方法执行
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        //设置属性值
        this.setFieldValByName("editedTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("editorId", StpUtil.getLoginIdAsString(), metaObject);
        this.setFieldValByName("createdTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("creatorId", StpUtil.getLoginIdAsString(), metaObject);
    }

    /**
     * 使用mp做修改操作时候，这个方法执行
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("editedTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("editorId", StpUtil.getLoginIdAsString(), metaObject);
    }
}