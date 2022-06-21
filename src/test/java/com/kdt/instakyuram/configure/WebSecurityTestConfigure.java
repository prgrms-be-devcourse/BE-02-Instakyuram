
package com.kdt.instakyuram.configure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import com.kdt.instakyuram.security.WebSecurityConfigure;
import com.kdt.instakyuram.security.jwt.JwtConfigure;
import com.kdt.instakyuram.token.service.TokenService;

@Import({WebSecurityConfigure.class, TokenService.class})
@EnableConfigurationProperties(JwtConfigure.class)
public class WebSecurityTestConfigure {
}

