$(document).ready(function () {
    // 검색 글자수 검사
    $('#ImpsearchBtn').on("click", function () {
        let query = $('#searchQuery').val();
        if (query.length < 2) {
            alert("검색어는 2글자 이상 입력해주세요.");
        } else {
            $('#search > form').submit();
        }
    });

    $('#deleteBtn').on("click", function () {
        let conf = confirm("정말 삭제하시겠습니까?");
        if (conf) {
            $('#impDeleteForm').submit();
        }
    });

    // 게시글 작성/수정 페이지 글자수 표시
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
function checkAnonymous() {
    let conf = confirm("로그인이 필요한 작업입니다. 로그인 화면으로 이동하시겠습니까?");
    if (conf) {
        location.href = "/sign-in";
    }
}