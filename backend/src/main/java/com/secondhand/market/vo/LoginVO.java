package com.secondhand.market.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {
    private String token;
    private UserInfoVO userInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoVO {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
        private String phone;
        private String email;
        private Integer creditScore;
        private Integer status;
    }
}
