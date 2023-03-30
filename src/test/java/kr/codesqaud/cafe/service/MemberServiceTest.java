package kr.codesqaud.cafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import kr.codesqaud.cafe.domain.Member;
import kr.codesqaud.cafe.dto.SignUpRequest;
import kr.codesqaud.cafe.exception.DuplicateMemberEmailException;
import kr.codesqaud.cafe.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        memberRepository.deleteAll();
    }

    @DisplayName("회원 저장 성공")
    @Test
    void create() {
        // given
        SignUpRequest signUpRequest = createRequestDummy();

        // when
        String savedId = memberService.signUp(signUpRequest);

        // then
        Member findMember = memberRepository.findById(savedId).orElseThrow();
        assertAll(
            () -> assertEquals(savedId, findMember.getId()),
            () -> assertEquals(signUpRequest.getEmail(), findMember.getEmail()),
            () -> assertEquals(signUpRequest.getPassword(), findMember.getPassword()),
            () -> assertEquals(signUpRequest.getNickName(), findMember.getNickName()));
    }

    @DisplayName("회원 저장시 이메일이 중복인 경우 실패")
    @Test
    void createFalse2() {
        // given
        SignUpRequest signUpRequest = createRequestDummy();
        memberService.signUp(signUpRequest);

        // when

        // then
        assertThrows(DuplicateMemberEmailException.class,
            () -> memberService.signUp(new SignUpRequest(signUpRequest.getEmail()
                , "test1111", "test")));
    }

    private SignUpRequest createRequestDummy() {
        String email = "test@naver.com";
        String password = "test1234";
        String nickName = "test";
        return new SignUpRequest(email, password, nickName);
    }

    private SignUpRequest createRequestDummy2() {
        String email = "mandu@gmail.com";
        String password = "mandu1234";
        String nickName = "mandu";
        return new SignUpRequest(email, password, nickName);
    }
}
