let token;
let header;

let memNo;
let boardId;

let commId;
let originContent;

$(document).ready(function () {
    token = $("meta[name='_csrf']").attr("content");
    header = $("meta[name='_csrf_header']").attr("content");

    memNo = $('#memNo').val();
    boardId = $('#boardId').val();

    getCommentList(boardId);

    // 댓글 작성시
    $('#comWriteBtn').on("click", function () {
        let content = $('#comContent').val();
        if (content === "" || content.length == 0) {
            alert("내용을 입력해주세요.");
        } else {
            writeComment(content);
        }
    });
});

function deleteComment(btn) {
    let conf = confirm("정말 삭제하시겠습니까?");
    if (conf) {
        commId = btn.parentNode.parentNode.childNodes[0].children[0].value;

        $.ajax({
            url: "/imp/comment",
            type: "delete",
            data: {
                "commentId": commId
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (result) {
                if (result === "success") {
                    getCommentList(boardId);
                } else {
                    alert("문제가 발생했습니다.");

                }
            }
        });
    }
}

function openUpdateForm(btn) {
    commId = btn.parentNode.parentNode.children[0].children[0].value;
    let originContentBox = btn.parentNode.parentNode.children[2];
    let originContent = btn.parentNode.parentNode.children[2].innerText;
    originContentBox.innerHTML =
        "<input type='text' id='newContent' name='content' value='" + originContent + "'>" +
        "<button type='button' id='modifySbmBtn' onclick='updateComment()'>완료</button>";
}

function updateComment() {
    let content = $('#newContent').val();
    $.ajax({
        url: "/imp/comment",
        type: "patch",
        data: {
            'content': content,
            'commentId': commId
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (result) {
            if (result == "success") {
                getCommentList(boardId);
            } else {
                alert("문제가 발생했습니다.");
            }
        },
        error: function (request, status) {
            alert("내부 서버의 문제로 댓글 수정에 실패했습니다.");
            console.warn("code : " + status + "\nmessage : " + request.responseText);
        }
    });
}

function getCommentList(boardId) {

    $.ajax({
        url: "/imp/comments",
        type: "get",
        data: {
            'boardId': boardId
        },
        dataType: "json",
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (result) {
            let commList = $('#comList');
            if (result.length > 0) {
                let str = "<table style='padding: 2em; width: 100%;'>";
                str += "<thead><tr>"
                    + "<td></td>" // 댓글 작성자 넘버가 들어감
                    + "<td>작성자명</td>"
                    + "<td>내용</td>"
                    + "<td>작성일</td>"
                    + "<td></td>"
                    + "</tr></thead><tbody>";

                for (let i = 0; i < result.length; i++) {
                    let item = result[i];
                    let dateTime = item.regDate.date.year + "년" + item.regDate.date.month + "월" + item.regDate.date.day
                        + "일&nbsp;" + item.regDate.time.hour + "시" + item.regDate.time.minute + "분";

                    str +=
                        "<tr style='background-color:white; margin: 0.6em auto; border:none; border-radius: 1.8em;'>" +
                        "<td><input type='text' hidden='hidden' value='" + item.id + "'</td>" +
                        "<td>" + item.writerName + "<input type='text' class='n' value='" + item.writerId + "' hidden='hidden'></td>" +
                        "<td>" + item.content + "</td>" +
                        "<td>" + dateTime + "</td>" +
                        "<td></td>" +
                        "</tr>";
                }
                str += "</tbody>";
                commList.html(str);

                if (memNo != null) {
                    CheckMemberId(memNo);
                }
            } else {
                commList.html("<p>작성된 댓글이 없습니다.</p>");
            }

        }, error: function (request, status) {
            console.warn("code : " + status + ", message : " + request.responseText);
        }

    });
}

function writeComment(content) {
    $.ajax({
        url: "/imp/comment",
        type: "post",
        data: {
            'memNo': memNo,
            'boardId': boardId,
            'content': content
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function () {
            $('#comContent').val("");
            getCommentList(boardId);
        }
    });
}

function CheckMemberId(memNo) {
    let idInputArr = document.getElementsByClassName('n');
    $.ajax({
        url: "/imp/comment/checkMemNo?memNo=" + memNo,
        type: "get",
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (result) {
            for (let i = 0; i < result.length; i++) {
                for (let j = 0; j < idInputArr.length; j++) {
                    if (result[i].writerId == idInputArr[j].value) {
                        let td = idInputArr[j].parentNode.parentNode.children[4]; // 0부터 센다.
                        let inner =
                            "<button class='modifyBtn' onclick='openUpdateForm(this)' type='button'>수정</button>" +
                            "<button class='deleteBtn' onclick='deleteComment(this)'  type='button'>삭제</button>";
                        td.innerHTML = inner;
                    }
                }
            }
        }, error: function (request, status) {
            console.warn("code : " + status + ", message : " + request.responseText);
        }
    });
}



