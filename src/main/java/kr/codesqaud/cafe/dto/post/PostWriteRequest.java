package kr.codesqaud.cafe.dto.post;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import kr.codesqaud.cafe.domain.Post;
import org.hibernate.validator.constraints.Length;

public class PostWriteRequest {

    @NotBlank
    @Length(min = 1, max = 50)
    private final String title;

    @NotBlank
    @Length(min = 10, max = 3000)
    private final String content;

    private final String writerId;

    private final LocalDateTime writeDate;

    public PostWriteRequest(String title, String content, String writerId) {
        this.title = title;
        this.content = content;
        this.writerId = writerId;
        this.writeDate = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getWriterId() {
        return writerId;
    }

    public LocalDateTime getWriteDate() {
        return writeDate;
    }

    public Post toEntity() {
        return new Post(null, title, content, writerId, writeDate, 0L);
    }
}