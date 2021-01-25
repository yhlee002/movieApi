$(function () {

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    let oauth_message = $('#oauthMsg').val(); // "[[${oauth_message}]]"

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
                            if (data == "didn't matching" || data == "not user") {
                                alert("잘못된 이메일 혹은 비밀번호 입니다.");
                                return false;
                            } else if (data == "not certified") {
                                let conf = confirm("이메일 인증이 필요한 계정입니다. 이메일 재전송을 원하십니까?");
                                if (conf) {
                                    sendCertMailAgain(email);
                                }
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

    function sendCertMailAgain(email) {
        $.ajax({
            url: "/sendCertMail",
            data: {
                'email': email
            },
            type: "post",
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (result) {
                if (result.resultCode == "success") {
                    alert("인증 메일을 다시 발송했습니다.");
                } else {
                    alert("메일 전송에 실패했습니다.");
                }
            }, error: function (status, request) {
                console.warn("code : " + status + "\nmessage : " + request.responseText);
            }
        });
    }

});

function goLoginProc(url) {
    location.href = url;
}

