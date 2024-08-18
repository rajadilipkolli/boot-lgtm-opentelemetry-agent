package dev.tpcoder.coupon;

import dev.tpcoder.coupon.exception.FakeInternalException;
import dev.tpcoder.coupon.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandlerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandlerAdvice.class);

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail handleNotFoundException(NotFoundException e) {
        logger.error("Not found exception: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(FakeInternalException.class)
    ProblemDetail handleFakeInternalException(FakeInternalException e) {
        logger.error("Fake internal exception: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
