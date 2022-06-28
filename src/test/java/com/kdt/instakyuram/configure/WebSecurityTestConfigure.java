
package com.kdt.instakyuram.configure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import com.kdt.instakyuram.security.SecurityConfigProperties;
import com.kdt.instakyuram.security.WebSecurityConfig;
import com.kdt.instakyuram.auth.service.TokenService;

@Import({WebSecurityConfig.class, TokenService.class})
@EnableConfigurationProperties(SecurityConfigProperties.class)
public class WebSecurityTestConfigure {
}

