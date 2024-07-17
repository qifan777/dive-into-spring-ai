package io.github.qifan777.knowledge.graph;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class Form13 {
    @ExcelProperty("source")
    private String source;
    private String managerCik;
    private String managerName;
    private String managerAddress;
    private String reportCalendarOrQuarter;
    private String cusip6;
    private String cusip;
    private String companyName;
    private Double value;
    private Double shares;
}
