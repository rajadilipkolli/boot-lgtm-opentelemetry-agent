package dev.tpcoder.coupon.controller;

import dev.tpcoder.coupon.exception.FakeInternalException;
import dev.tpcoder.coupon.model.Coupon;
import dev.tpcoder.coupon.service.CouponService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {
    private final CouponService couponService;
    private final Random random;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
        random = new Random(0);
    }

    @GetMapping
    public Iterable<Coupon> getAllCoupons() throws InterruptedException {
        // Simulate latency (Long running process)
        TimeUnit.of(ChronoUnit.SECONDS).sleep(random.nextInt(5));
        return couponService.findAll();
    }

    @GetMapping("/{id}")
    public Coupon getCouponById(@PathVariable Long id) {
        return couponService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Coupon createCoupon(@RequestBody Coupon coupon) {
        return couponService.saveCoupon(coupon);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
    }

    @GetMapping("/active")
    public List<Coupon> getActiveCoupons(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        if (random.nextInt(3) > 1) {
            throw new FakeInternalException("Failed to fetch active coupons");
        }
        return couponService.findActiveCoupons(startDate, endDate);
    }
}
