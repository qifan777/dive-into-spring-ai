package io.github.qifan777.knowledge.feedback.repository;

import io.github.qifan777.knowledge.feedback.entity.Feedback;
import io.github.qifan777.knowledge.feedback.entity.FeedbackFetcher;
import io.github.qifan777.knowledge.feedback.entity.FeedbackTable;
import io.github.qifan777.knowledge.feedback.entity.dto.FeedbackSpec;
import io.github.qifan777.knowledge.infrastructure.model.QueryRequest;
import io.github.qifan777.knowledge.user.UserFetcher;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.spring.repository.SpringOrders;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedbackRepository extends JRepository<Feedback, String> {
    FeedbackTable feedbackTable = FeedbackTable.$;
    FeedbackFetcher COMPLEX_FETCHER_FOR_ADMIN = FeedbackFetcher.$.allScalarFields()
            .creator(UserFetcher.$.phone().nickname())
            .editor(UserFetcher.$.phone().nickname());
    FeedbackFetcher COMPLEX_FETCHER_FOR_FRONT = FeedbackFetcher.$.allScalarFields()
            .creator(true);

    default Page<Feedback> findPage(QueryRequest<FeedbackSpec> queryRequest,
                                    Fetcher<Feedback> fetcher) {
        FeedbackSpec query = queryRequest.getQuery();
        Pageable pageable = queryRequest.toPageable();
        return sql().createQuery(feedbackTable)
                .where(query)
                .orderBy(SpringOrders.toOrders(feedbackTable, pageable.getSort()))
                .select(feedbackTable.fetch(fetcher))
                .fetchPage(queryRequest.getPageNum() - 1, queryRequest.getPageSize(),
                        SpringPageFactory.getInstance());
    }
}