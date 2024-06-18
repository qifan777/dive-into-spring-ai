package io.github.qifan777.knowledge.infrastructure.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.ast.LikeMode;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
public class QueryRequest<T> {

    private T query;

    private Integer pageSize = 10;

    private Integer pageNum = 1;
    @Nullable
    private LikeMode likeMode;
    @Nullable
    private List<QuerySort> sorts;

    public Pageable toPageable() {
        if (!CollectionUtils.isEmpty(sorts)) {
            return PageRequest.of(getPageNum() - 1, getPageSize(),
                    Sort.by(sorts.stream()
                            .map(QuerySort::toOrder)
                            .collect(Collectors.toList())));
        }
        return PageRequest.of(getPageNum() - 1, getPageSize(),
                Sort.by(Sort.Order.desc("createdTime")));
    }

    public Pageable toPageable(Sort.Order... orders) {
        return PageRequest.of(this.getPageNum() - 1,
                this.getPageSize(),
                Sort.by(orders));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuerySort {

        String property;
        Sort.Direction direction;

        public Sort.Order toOrder() {
            return new Sort.Order(direction, property);
        }
    }
}
