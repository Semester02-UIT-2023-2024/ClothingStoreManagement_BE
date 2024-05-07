package com.sa.clothingstore.dto.response.report;

import java.math.BigDecimal;
import java.util.Date;

public class DailyRevenueResponse {
    private Date date;
    private long totalInvoices;
    private long totalProducts;
    private BigDecimal totalExpense;
}