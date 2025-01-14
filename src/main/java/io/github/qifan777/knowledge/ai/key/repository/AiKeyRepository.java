package io.github.qifan777.knowledge.ai.key.repository;

import io.github.qifan777.knowledge.ai.factory.entity.AiFactoryFetcher;
import io.github.qifan777.knowledge.ai.key.entity.AiKey;
import io.github.qifan777.knowledge.ai.key.entity.AiKeyFetcher;
import io.github.qifan777.knowledge.ai.key.entity.AiKeyTable;
import io.github.qifan777.knowledge.ai.key.entity.dto.AiKeySpec;
import io.github.qifan777.knowledge.infrastructure.model.QueryRequest;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.spring.repository.SpringOrders;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AiKeyRepository extends JRepository<AiKey, String> {
    AiKeyTable t = AiKeyTable.$;
    AiKeyFetcher FETCHER = AiKeyFetcher.$.allScalarFields()
            .factory(AiFactoryFetcher.$.name())
            .factoryId();

    default Page<AiKey> findPage(QueryRequest<AiKeySpec> queryRequest,
                                 Fetcher<AiKey> fetcher) {
        AiKeySpec query = queryRequest.getQuery();
        Pageable pageable = queryRequest.toPageable();
        return sql().createQuery(t)
                .where(query)
                .orderBy(SpringOrders.toOrders(t, pageable.getSort()))
                .select(t.fetch(fetcher))
                .fetchPage(queryRequest.getPageNum() - 1, queryRequest.getPageSize(),
                        SpringPageFactory.getInstance());
    }
}