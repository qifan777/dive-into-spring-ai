package io.github.qifan777.knowledge.graph.model;

import lombok.Data;

/**
 * 表单包含投资方公司投资的其他公司、所持股份数量和投资价值的信息。
 */
@Data
public class Form13 {
    /**
     * From13表格的原文链接
     */
    private String source;
    /**
     * 投资方公司的CIK，参考Form10K中的CIK解释
     */
    private String managerCik;
    /**
     * 投资方公司的名称
     */
    private String managerName;
    /**
     * 投资方公司的地址
     */
    private String managerAddress;
    /**
     * Form13报告发布的日期
     */
    private String reportCalendarOrQuarter;
    /**
     * 参考Form10K中的CUSIP6解释
     */
    private String cusip6;
    /**
     * 参考Form10K中的CUSIP解释
     */
    private String cusip;
    /**
     * 被投资公司的名称
     */
    private String companyName;
    /**
     * 投资的金额
     */
    private Double value;
    /**
     * 投资份额
     */
    private Double shares;
}
