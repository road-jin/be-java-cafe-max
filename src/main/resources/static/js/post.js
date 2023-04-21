function addComment(data) {
    return `<div class="comment">
                <strong class="comment-writer">${data.writer.nickname}</strong>
                <pre class="comment-content">${data.content}</pre>
                <span class="comment-date">${data.writeDate}</span>
                <form class="comment-delete" action="/api/posts/${data.postId}/comments/${data.id}">
                     <button class="btn btn-primary btn-sm" type="submit">삭제</button>
                </form>
            </div>`;
}

function updateCommentsSize(plus) {
    const text = $("#comments-size").text();
    const contentsSize = Number(text.replace(/[^0-9]/g, "")) + plus;
    $("#comments-size").text(`댓글 ${contentsSize}개`);
}

function errorAlert(request) {
    let messages = JSON.stringify(request.responseJSON.message, null, 2);

    if (request.responseJSON.message.errorMessage != null) {
        messages = JSON.stringify(request.responseJSON.message.errorMessage[0]);
    }

    alert(`에러 상태 코드 : ${request.responseJSON.status}\n에러 메시지 : ${messages}`);
}

function writeAjax(e) {
    e.preventDefault();
    e.stopPropagation();

    const formSerializeArray = $('.comment-submit').serializeArray();
    const object = {};

    for (let i = 0; i < formSerializeArray.length; i++){
        object[formSerializeArray[i]['name']] = formSerializeArray[i]['value'];
    }

    $.ajax({
        type : 'post',
        url : $(".comment-submit").attr("action"),
        data : JSON.stringify(object),
        dataType : 'json',
        contentType : 'application/json; charset=UTF-8',
        error: function (request, status, error) {
            errorAlert(request);
        },
        success : function(data, status) {
            updateCommentsSize(+1);
            $(".comments").append(addComment(data));
            $("#content").val('');
        },
    });
}

function deleteAjax(e) {
    e.preventDefault();
    e.stopPropagation();

    const form = $(this).closest(".comment-delete");

    $.ajax({
        type: 'delete',
        url: form.attr("action"),
        dataType: 'json',
        contentType: 'application/json; charset=UTF-8',
        error: function (request, status, error) {
            errorAlert(request);
        },
        success: function (data, status) {
            if (data.valid) {
                updateCommentsSize(-1);
                form.closest('.comment').remove();
                return;
            }

            alert("에러가 발생하였습니다.");
        },
    });
}


document.addEventListener("DOMContentLoaded", function () {
    $(".comment-submit button[type='submit']").on("click", writeAjax);
    $(document).on("click", ".comment-delete button[type='submit']", deleteAjax);
});