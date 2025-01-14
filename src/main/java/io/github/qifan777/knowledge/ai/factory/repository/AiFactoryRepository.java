package io.github.qifan777.knowledge.ai.factory.repository;

import io.github.qifan777.knowledge.ai.factory.entity.AiFactory;
import io.github.qifan777.knowledge.ai.factory.entity.AiFactoryFetcher;
import io.github.qifan777.knowledge.ai.factory.entity.AiFactoryTable;
import io.github.qifan777.knowledge.ai.factory.entity.dto.AiFactorySpec;
import io.github.qifan777.knowledge.infrastructure.model.QueryRequest;
import io.github.qifan777.knowledge.user.UserFetcher;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.spring.repository.SpringOrders;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AiFactoryRepository extends JRepository<AiFactory, String> {
    AiFactoryTable t = AiFactoryTable.$;
    AiFactoryFetcher FETCHER = AiFactoryFetcher.$.allScalarFields();

    default Page<AiFactory> findPage(QueryRequest<AiFactorySpec> queryRequest,
                                     Fetcher<AiFactory> fetcher) {
        AiFactorySpec query = queryRequest.getQuery();
        Pageable pageable = queryRequest.toPageable();
        return sql().createQuery(t)
                .where(query)
                .orderBy(SpringOrders.toOrders(t, pageable.getSort()))
                .select(t.fetch(fetcher))
                .fetchPage(queryRequest.getPageNum() - 1, queryRequest.getPageSize(),
                        SpringPageFactory.getInstance());
    }
}