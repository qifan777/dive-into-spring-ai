package io.github.qifan777.knowledge.infrastructure.jimmer;

import cn.hutool.core.util.IdUtil;
import org.babyfish.jimmer.sql.meta.UserIdGenerator;

public class UUIDIdGenerator implements UserIdGenerator<String> {

  public UUIDIdGenerator() {
  }

  public String generate(Class<?> entityType) {
    return IdUtil.fastSimpleUUID();
  }
}
