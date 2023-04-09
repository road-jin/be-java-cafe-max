package kr.codesqaud.cafe.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.List;
import kr.codesqaud.cafe.dto.post.PostResponse;
import kr.codesqaud.cafe.dto.post.PostWriteRequest;
import kr.codesqaud.cafe.dto.post.WriterResponse;
import kr.codesqaud.cafe.exception.post.PostNotFoundException;
import kr.codesqaud.cafe.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @DisplayName("게시물 목록 조회")
    @Test
    void posts() throws Exception {
        // given
        List<PostResponse> postResponses = List.of(createPostResponseDummy(),
            createPostResponseDummy2());
        given(postService.findAll()).willReturn(postResponses);

        // when

        // then
        mockMvc.perform(get("/posts"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("post/posts"))
            .andExpect(model().attribute("postResponses", postResponses))
            .andDo(print());
    }

    @DisplayName("게시글 작성 성공")
    @Test
    void write() throws Exception {
        // given
        PostWriteRequest postWriteRequest = new PostWriteRequest("게시글 제목", "게시글 내용", 1L);
        given(postService.save(any())).willReturn(1L);

        // when

        // then
        mockMvc.perform(post("/posts")
                .param("title", postWriteRequest.getTitle())
                .param("content", postWriteRequest.getContent())
                .param("writerId", String.valueOf(postWriteRequest.getWriterId()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/posts"))
            .andDo(print());
    }

    @DisplayName("게시글 작성시 게시글 제목이 빈값이거나 2글자 미만 또는 50글자 초과하는 경우 실패")
    @ParameterizedTest
    @CsvSource(value = {",NotBlank", "호,Length", "게시글 제목 최대 길이는 50글자 인데 언제 이걸 다 써야 하는지 모르겠네요. 제목이 얼마나 길어야 50글자를 채울까요?,Length"})
    void writeFalse(String title, String error) throws Exception {
        // given
        PostWriteRequest postWriteRequest = new PostWriteRequest(title, "게시글 내용", 1L);

        // when

        // then
        mockMvc.perform(post("/posts")
                .param("title", postWriteRequest.getTitle())
                .param("content", postWriteRequest.getContent())
                .param("writerId", String.valueOf(postWriteRequest.getWriterId()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk())
            .andExpect(view().name("post/write"))
            .andExpect(model().attributeHasFieldErrorCode("postWriteRequest", "title", error))
            .andDo(print());
    }

    @DisplayName("게시글 작성시 게시글 내용이 빈값이거나 2글자 미만인 경우 실패")
    @ParameterizedTest
    @CsvSource(value = {",NotBlank", "호,Length"})
    void writeFalse2(String content, String error) throws Exception {
        // given
        PostWriteRequest postWriteRequest = new PostWriteRequest("게시글 제목", content, 1L);

        // when

        // then
        mockMvc.perform(post("/posts")
                .param("title", postWriteRequest.getTitle())
                .param("content", postWriteRequest.getContent())
                .param("writerId", String.valueOf(postWriteRequest.getWriterId()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk())
            .andExpect(view().name("post/write"))
            .andExpect(model().attributeHasFieldErrorCode("postWriteRequest", "content", error))
            .andDo(print());
    }

    @DisplayName("게시글 단건 조회 성공")
    @Test
    void detailPost() throws Exception {
        // given
        PostResponse postResponse = createPostResponseDummy();
        given(postService.findById(any())).willReturn(postResponse);

        // when

        // then
        mockMvc.perform(get("/posts/{id}", postResponse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("post/post"))
            .andExpect(model().attribute("postResponse", postResponse))
            .andDo(print());
    }

    @DisplayName("게시글 단건 조회시 게시글 아이디가 없는 경우 실패")
    @Test
    void detailPostFalse() throws Exception {
        // given
        given(postService.findById(any())).willThrow(PostNotFoundException.class);

        // when

        // then
        mockMvc.perform(get("/posts/{id}", "1"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("error/404"))
            .andDo(print());
    }

    @DisplayName("글쓰기 폼")
    @Test
    void writeForm() throws Exception {
        // given

        // when

        // then
        mockMvc.perform(get("/posts/write"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("post/write"))
            .andDo(print());
    }

    private PostResponse createPostResponseDummy() {
        return new PostResponse(1L, "제목", "내용", new WriterResponse(1L, "만두"), LocalDateTime.now(),
            0L);
    }

    private PostResponse createPostResponseDummy2() {
        return new PostResponse(2L, "제목2", "내용2", new WriterResponse(2L, "만두2"), LocalDateTime.now(),
            0L);
    }
}
