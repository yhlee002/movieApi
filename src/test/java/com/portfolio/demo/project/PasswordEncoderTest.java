package com.portfolio.demo.project;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void 테스트1() {
        String str = "1234";
        String hashed = passwordEncoder.encode(str);

        Assertions.assertNotEquals(passwordEncoder.encode(str), hashed);
        Assertions.assertTrue(passwordEncoder.matches(str, hashed));
    }
}
