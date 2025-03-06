package io.github.qifan777.knowledge.ai.session;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("session")
@RestController
@AllArgsConstructor
public class AiSessionController {
    private final AiSessionMapper sessionMapper;

    /**
     * 根据id查询会话
     *
     * @param id 会话id
     * @return 会话信息
     */
    @GetMapping("{id}")
    public AiSession findById(@PathVariable String id) {
        return sessionMapper.selectById(id);
    }

    /**
     * 保存会话
     *
     * @param input 会话dto参考src/main/dto/AiSession.dto
     * @return 创建后的id
     */
    @PostMapping("save")
    public String save(@RequestBody AiSession input) {
        sessionMapper.insertOrUpdate(input);
        return input.getId();
    }

    /**
     * 查询当前登录用户的会话
     *
     * @return 会话列表
     */
    @GetMapping("user")
    public List<AiSession> findByUser() {
        return sessionMapper.selectList(Wrappers.lambdaQuery(AiSession.class).eq(AiSession::getCreatorId, StpUtil.getLoginIdAsString()));
    }

    /**
     * 批量删除会话
     *
     * @param ids 会话id列表
     */
    @DeleteMapping
    public void delete(@RequestBody List<String> ids) {
        sessionMapper.deleteByIds(ids);
    }
}
