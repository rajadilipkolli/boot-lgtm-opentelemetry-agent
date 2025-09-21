package dev.tpcoder.coupon.model;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Coupon(@Id Long id, String title, String description, LocalDateTime startDate, LocalDateTime endDate,
                     Operator operator, BigDecimal value) {
}
