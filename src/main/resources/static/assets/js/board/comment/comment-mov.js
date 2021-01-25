let header;
let token;

let movieNo;
let memNo;


$(document).ready(function () {
    token = $("meta[name='_csrf']").attr("content");
    header = $("meta[name='_csrf_header']").attr("content");

    movieNo = $('#movieCd').val();
    memNo = $('#memNo').val();


    getCommentList(1, movieNo);

    $('#commentSubmitBtn').on("click", function () {
        let commentVal = $('#commentContent').val();
        let rating = $('#ratingSelector').val();

        if (memNo == null) {
            let conf = confirm("로그인 후 가능한 작업입니다. 로그인 화면으로 이동하시겠습니까?");
            if (conf) {
                location.href = "/sign-in";
            }
        } else if (commentVal == null) {
            alert("내용을 작성해주세요.");
        } else if (rating == null) {
            alert("별점을 매겨주세요.");
        } else {
            if (commentVal != null && commentVal.trim().length != 0 && 0 < rating <= 5) {

                $.ajax({
                    url: "/movieInfo/comment/write",
                    data: {
                        'commentContent': commentVal,
                        'memNo': memNo,
                        'movieNo': movieNo,
                        'rating': rating
                    },
                    type: 'post',
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(header, token);
                    },
                    success(data) {
                        if (data === "writeSuccess") {
                            // $('#ratingSelector').val(1); // 별 하나로 표시될까?
                            $('#commentContent').val("");
                            getCommentList(1, movieNo, memNo);
                        } else { // 'data === writerFail'
                            alert("댓글 작성 중 문제가 발생했습니다. 지속될 경우 관리자에 문의바랍니다.");
                        }

                    }, error: function (request, status) {
                        alert("댓글 작성 중 문제가 발생했습니다. 지속될 경우 관리자에 문의바랍니다.");
                        console.warn("code : " + status + "\nmessage : " + request.responseText);
                    }
                });
            } else {
                alert("댓글 내용을 작성해주세요.");
            }
        }
    });
});

function getCommentList(pageNum, movieCd) {
    $.ajax({
        url: "/movieInfo/comment/regDt",
        data: {
            'p': pageNum,
            'movieCd': movieCd
        },
        type: 'get',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            let str = "";
            /* 댓글 수 확인 */
            if (data.totalCommentCnt == 0) {
                str = "<p>작성된 코멘트가 없습니다.</p>";
            } else {

                str = "<table style='padding: 2em; width: 100%;'>"
                    + "<thead><tr>"
                    + "<td></td>" // 댓글 번호
                    + "<td></td>" // 별점
                    + "<td>내용</td>"
                    + "<td>작성자명</td>"
                    + "<td>작성일</td>"
                    + "<td></td>" // 수정, 삭제 버튼 들어갈 자리
                    + "</tr></thead><tbody>";

                for (let i = 0; i < data.list.length; i++) {
                    let item = data.list[i];
                    let rating = "";
                    for (let j = 0; j < item.rating; j++) {
                        rating += "★";
                    }
                    let dateTime = item.regDate.date.year + "년" + item.regDate.date.month + "월" + item.regDate.date.day
                        + "일&nbsp;" + item.regDate.time.hour + "시" + item.regDate.time.minute + "분";

                    str +=
                        "<tr style='background-color:white; margin: 0.6em auto; border:none; border-radius: 1.8em;'>" +
                        "<td><input type='text' value='" + item.id + "' hidden='hidden'></td>" +
                        "<td style='color: goldenrod'>" + rating + "</td>" +
                        "<td>" + item.content + "</td>" +
                        "<td>" + item.writerName + "<input type='text' class='n' value='" + item.writerId + "' hidden='hidden'></td>" +
                        "<td>" + dateTime + "</td>" +
                        "<td></td>" +
                        "</tr>";
                }

                /* 페이지넘버 나열 */
                let pageStr = "";
                for (let i = 1; i <= data.totalPageCnt; i++) {
                    pageStr += "<span style='margin:0.3em' onclick='getCommentList(" + i + ",movieCd) '>" + i + "</span>"; //getCommentList('+i+',movieCd)
                }
                $('#pagenation_commMov').html(pageStr);
            }
            $('#commentMovList').html(str);

            if (memNo != null) {
                CheckMemberId(memNo);
            }

        }, error: function (request, status) {
            alert("현재 내부서버 오류로 인해 댓글 조회 기능이 어렵습니다.");
            console.warn("code : " + status + "\nmessage : " + request.responseText);
        }
    });
}

function CheckMemberId(memNo) {
    let commIdArr = document.getElementsByClassName("n"); // 코멘트를 작성한 아이디가 있는 input 노드를 모두 가져오기

    $.ajax({
        url: "/movieInfo/comment/checkMemNo?memNo=" + memNo,
        type: "get",
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (result) {
            if (result.length > 0) {
                for (let i = 0; i < result.length; i++) {
                    for (let j = 0; j < commIdArr.length; j++) {
                        if (commIdArr[j].value == result[i].writerId) {
                            let td = commIdArr[j].parentNode.parentNode.children[5];
                            td.innerHTML = "<button class='modifyBtn' onclick='openUpdateForm(this)' type='button'>수정</button>" +
                                "<button class='deleteBtn' onclick='deleteComment(this)' type='button'>삭제</button>";
                        }
                    }
                }
            }
        }
    });
}

function deleteComment(btn) {
    let conf = confirm("정말 삭제하시겠습니까?");
    if (conf) {
        commId = btn.parentNode.parentNode.childNodes[0].children[0].value;

        $.ajax({
            url: "/movieInfo/comment/delete",
            type: "post",
            data: {
                "commentId": commId
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (result) {
                if (result === "success") {
                    getCommentList(1, movieNo);
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
        url: "/movieInfo/comment/update",
        type: "post",
        data: {
            'content': content,
            'commentId': commId
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (result) {
            if (result == "success") {
                getCommentList(1, movieNo);
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


