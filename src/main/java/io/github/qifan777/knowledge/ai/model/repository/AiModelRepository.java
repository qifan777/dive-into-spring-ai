package io.github.qifan777.knowledge.ai.model.repository;

import io.github.qifan777.knowledge.ai.factory.entity.AiFactoryFetcher;
import io.github.qifan777.knowledge.ai.model.entity.AiModel;
import io.github.qifan777.knowledge.ai.model.entity.AiModelFetcher;
import io.github.qifan777.knowledge.ai.model.entity.AiModelTable;
import io.github.qifan777.knowledge.ai.model.entity.dto.AiModelSpec;
import io.github.qifan777.knowledge.infrastructure.model.QueryRequest;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.spring.repository.SpringOrders;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AiModelRepository extends JRepository<AiModel, String> {
    AiModelTable t = AiModelTable.$;
    AiModelFetcher FETCHER = AiModelFetcher.$.allScalarFields()
            .factoryId()
            .factory(AiFactoryFetcher.$.name());


    default Page<AiModel> findPage(QueryRequest<AiModelSpec> queryRequest,
                                   Fetcher<AiModel> fetcher) {
        AiModelSpec query = queryRequest.getQuery();
        Pageable pageable = queryRequest.toPageable();
        return sql().createQuery(t)
                .where(query)
                .orderBy(SpringOrders.toOrders(t, pageable.getSort()))
                .select(t.fetch(fetcher))
                .fetchPage(queryRequest.getPageNum() - 1, queryRequest.getPageSize(),
                        SpringPageFactory.getInstance());
    }
}