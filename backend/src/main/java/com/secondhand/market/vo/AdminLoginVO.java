package com.secondhand.market.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginVO {
    private String token;
    private Long adminId;
    private String username;
    private String nickname;
}
