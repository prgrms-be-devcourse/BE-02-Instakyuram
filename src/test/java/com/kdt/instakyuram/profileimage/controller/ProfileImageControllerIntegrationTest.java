/*
package com.kdt.instakyuram.profileimage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.profileimage.domain.ProfileImage;
import com.kdt.instakyuram.profileimage.domain.ProfileImageRepository;

@WithMockUser("MEMBER")
@AutoConfigureMockMvc
@SpringBootTest
class ProfileImageControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private ProfileImageRepository profileImageRepository;

	@Test
	@Transactional
	@DisplayName("사용자 프로필 이미지를 가져온다")
	void testGetProfileImage() throws Exception {
		//given
		setMockAnonymousAuthenticationToken();
		String name = "programmer";
		Member demoMember = Member.builder()
			.name(name)
			.username(name + 123)
			.email(name + "@programmers.co.kr")
			.password("Programmer123!")
			.phoneNumber("01012341234")
			.introduction("hi velopert")
			.build();

		final String DEFAULT_PATH = System.getProperty("user.dir") + "/picture/";

		entityManager.persist(demoMember);

		ProfileImage profileImage = profileImageRepository.save(
			ProfileImage.builder()
				.originalFileName("kkyu.png")
				.path(DEFAULT_PATH)
				.serverFileName("a504da44-61b7-4e79-96f1-26567768ddab.png")
				.size(3704L)
				.member(demoMember)
				.build()
		);

		//when
		String uri = "/api/profileImages/" + demoMember.getId() + "/image/" + profileImage.getServerFileName();
		ResultActions perform = mockMvc.perform(
			get(uri)
		).andDo(print());

		//then
		perform.andExpect(status().isOk());
	}

	*/
/**
	 * note : 필요한 데이터를 저장할 때, jpaAudit이 동작하게 된다 (통합테스트에서만)
	 *   @WithMockUser("MEMBER") 사용시 src 내부에 jpaAudit 부분에 castException이 난다.
	 *   만약 임시 데이터를 사용하기 위해서는 해당 메소드를 한번 호출하면 해결된다.
	 *//*

	private void setMockAnonymousAuthenticationToken() {
		SimpleGrantedAuthority role_anonymous = new SimpleGrantedAuthority("ROLE_MEMBER");
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(role_anonymous);
		Authentication authentication = new AnonymousAuthenticationToken("anonymous", "anonymous", authorities);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

}*/
