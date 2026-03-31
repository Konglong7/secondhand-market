package com.secondhand.market.controller;

import com.secondhand.market.common.Result;
import com.secondhand.market.service.PaymentService;
import com.secondhand.market.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create/{orderId}")
    public Result<String> createPayment(@PathVariable Long orderId,
                                         HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        String paymentNo = paymentService.createPayment(orderId, userId);
        return Result.success(paymentNo);
    }

    @PostMapping("/simulate/{paymentNo}")
    public Result<Void> simulatePayment(@PathVariable String paymentNo) {
        paymentService.simulatePayment(paymentNo);
        return Result.success();
    }

    @PostMapping("/callback")
    public Result<Void> paymentCallback(@RequestParam String paymentNo,
                                         @RequestParam String transactionId) {
        paymentService.paymentCallback(paymentNo, transactionId);
        return Result.success();
    }
}
