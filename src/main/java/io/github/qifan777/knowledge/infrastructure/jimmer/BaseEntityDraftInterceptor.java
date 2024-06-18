package io.github.qifan777.knowledge.infrastructure.jimmer;

import cn.dev33.satoken.stp.StpUtil;
import lombok.AllArgsConstructor;
import org.babyfish.jimmer.sql.DraftInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BaseEntityDraftInterceptor implements DraftInterceptor<BaseEntity, BaseEntityDraft> {


  @Override
  public void beforeSave(@NotNull BaseEntityDraft draft, BaseEntity baseEntity) {
    draft.applyEditor(user -> {
      user.setId(StpUtil.getLoginIdAsString());
    });
    if (baseEntity == null) {
      draft.applyCreator(user -> {
        user.setId(StpUtil.getLoginIdAsString());
      });
    }
  }
}