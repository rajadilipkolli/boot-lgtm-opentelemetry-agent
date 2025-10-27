package dev.tpcoder.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import dev.tpcoder.coupon.config.AbstractIntegrationTest;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class CouponApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
        mockMvcTester
                .get()
                .uri("/api/v1/coupons")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson()
                .convertTo(List.class)
                .satisfies(coupon -> {
                    assertThat(coupon).isNotNull(); 
                });
    }

}
