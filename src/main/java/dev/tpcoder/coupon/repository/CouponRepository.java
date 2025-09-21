package dev.tpcoder.coupon.repository;

import dev.tpcoder.coupon.model.Coupon;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponRepository extends CrudRepository<Coupon, Long> {

    @Query("SELECT * FROM coupon WHERE start_date <= :startDate AND end_date >= :endDate")
    List<Coupon> findActiveCoupons(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
}
