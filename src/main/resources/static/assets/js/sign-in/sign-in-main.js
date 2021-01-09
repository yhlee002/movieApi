$(function () {

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    $('input[name=sign-in-submit]').on("click", function () {
        let email = $('#email').val();
        let pwd = $('#password').val();

        /* ID의 유효성 검사 */
        if (email == "" || email.length == 0) { // 빈칸 검사
            alert("이메일을 입력해주세요.");
        } else {// 이메일 형식 검사
            let emailCheck = RegExp(/^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/);
            if (emailCheck.test(email)) {
                /* password의 유효성 검사(빈칸 검사만) */
                if (pwd == "" || pwd.length == 0) {
                    alert("비밀번호를 입력해주세요.");
                } else {
                    /* 아이디, 비밀번호 일치 여부를 판단한 뒤, submit하거나 틀린 아이디/비밀번호 임을 알려주기 */
                    $.ajax({
                        url: "/sign-in/checkProc",
                        type: "post",
                        data: {
                            'email': email,
                            'pwd': pwd
                        },
                        dataType: "text",
                        beforeSend: function (xhr) {
                            xhr.setRequestHeader(header, token);
                        },
                        success: function (data) {
                            console.log(data);
                            if (data == "didn't matching" || data == "not user") {
                                alert("잘못된 이메일 혹은 비밀번호 입니다.");
                                return false;
                            } else { // matched
                                $('#sign-up-form').submit();
                            }
                        },
                        error: function (request, status) {
                            alert("현재 내부 서버 문제로 로그인이 어렵습니다. 관리자에 문의바랍니다.");
                            console.log("code : " + status + "\nmessage : " + request.responseText);
                            return false;
                        }
                    });
                }
            }
        }

    });
    1
});

$(document).ready(function () {
    let oauth_message = $('#oauthMsg').val(); // "[[${oauth_message}]]"
    console.log(oauth_message);

    if (oauth_message === "kakao user") { // 카카오 로그인 api 구현시 사라질 조건
        window.alert("카카오 아이디를 통해 로그인이 필요한 회원입니다.");
    } else if (oauth_message === "conventional user") {
        window.alert("비밀번호를 통한 로그인이 필요한 회원입니다.");
    } else if (oauth_message === "not user") {
        let conf = window.confirm("가입되지 않은 사용자입니다. 회원가입 페이지로 이동하시겠습니까?");
        if (conf) {
            location.href = "/sign-up/oauthMem";
        }
    }
}, false);

// /* 네아로 API 호출 */
// function naverLogin() {
//     let naver_login_url = "[[${naverLoginUrl}]]";
//     location.href = naver_login_url;
// }
//
// function kakaoLogin() {
//     location.href = '#';
// }

function goLoginProc(url) {
    location.href = url;
}