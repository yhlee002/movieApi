$(document).ready(function () {

    // 게시글 삭제 확인
    $('#deleteBtn').on("click", function () {
        let conf = confirm("해당 게시글을 삭제하시겠습니까?");
        if (conf) {
            $('#noticeDeleteForm').submit();
        }
    });

    // 검색 글자수 검사
    $('#NtcSearchBtn').on("click", function () {
        let query = $('#searchQuery').val();
        if (query.length < 2) {
            alert("검색어는 2글자 이상 입력해주세요.");
        } else {
            $('#search > form').submit();
        }
    });

    // 게시글 작성/수정시 글자수 검사
    let input = $('.note-editable').text();
    let inputLength = input.length;
    $('#currentInputLength').html(inputLength);

    $('.note-editable').on("keyup", function () {
        input = $(this).text();
        inputLength = input.length;
        $('#currentInputLength').html(inputLength);

        if (inputLength > 2048) {
            $('#inputLengthBox').css("color", "red");
        } else {
            $('#inputLengthBox').css("color", "black");
        }

    });
});

// 게시글 작성 자격 검사
function checkRoleAdmin(role) {
    if (role === "ROLE_ADMIN") {
        location.href = "/notice/write";
    } else if (role === "ROLE_USER") { //
        alert("관리자만 접근가능한 페이지입니다.");
    }
}


