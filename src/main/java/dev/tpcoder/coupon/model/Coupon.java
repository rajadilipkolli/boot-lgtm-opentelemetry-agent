package dev.tpcoder.coupon.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("coupon")
public record Coupon(
        @Id Long id,
        String title,
        String description,
        @Column("start_date") LocalDateTime startDate,
        @Column("end_date") LocalDateTime endDate,
        Operator operator,
        BigDecimal value
) {
}
