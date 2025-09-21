package dev.tpcoder.coupon.config;

import org.springframework.boot.test.context.SpringBootTest;
import dev.tpcoder.coupon.CouponApplication;

@SpringBootTest(classes = {CouponApplication.class, TestContainerConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractIntegrationTest {
}

