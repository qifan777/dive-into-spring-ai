package io.github.qifan777.knowledge.infrastructure.jimmer;

import lombok.AllArgsConstructor;
import org.babyfish.jimmer.sql.DraftInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class BaseDateTimeDraftInterceptor implements
    DraftInterceptor<BaseDateTime, BaseDateTimeDraft> {


  @Override
  public void beforeSave(@NotNull BaseDateTimeDraft draft, BaseDateTime baseDateTime) {
    draft.setEditedTime(LocalDateTime.now());
    if (baseDateTime == null) {
      draft.setCreatedTime(LocalDateTime.now());
    }
  }
}
