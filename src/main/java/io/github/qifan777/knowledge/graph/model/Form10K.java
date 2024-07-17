package io.github.qifan777.knowledge.graph.model;

import lombok.Data;

import java.util.List;

/**
 * Form 10-K是美国证券交易委员会（SEC）要求上市公司必须每年提交的有关其财务表现与公司运营的综合性报告，
 * 具体来说包括公司历史，组织架构，财务状况，每股收益，分支机构，高管薪酬等信息。
 */
@Data
public class Form10K {
    /**
     * 业务
     */
    private String item1;
    /**
     * 危险因素
     */
    private String item1a;
    /**
     * 管理层对财务状况及经营成果的探讨与分析。
     */
    private String item7;
    /**
     * 市场风险的定量和定性披露
     */
    private String item7a;
    /**
     * 中央索引键(CIK)用于证券交易委员会的计算机系统，用于识别已向证券交易委员会提交披露文件的公司和个人。
     */
    private String cik;

    /**
     * 。CUSIP的创立是为了给北美的每一个证券一个唯一的代码，这样在清算的时候就不会因为名字相似而出错。
     * 注意它是为了给每一个证券一个唯一的代码，这个证券包括股票，期权，期货，政府债券，企业债券等所有的证券
     */
    private List<String> cusip;
    /**
     * CUSIP的前六位是企业的代码
     */
    private String cusip6;
    /**
     * 公司的名称（包含别名，所以有多个）列表
     */
    private List<String> names;
    /**
     * 该Form 10-K报告的原文
     */
    private String source;
}
