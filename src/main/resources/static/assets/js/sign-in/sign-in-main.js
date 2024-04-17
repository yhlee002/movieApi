import util from '/assets/js/util2';

$(function () {

    let oauth_message = document.getElementById('oauthMsg').value; // "[[${oauth_message}]]"

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

    const signInBtn = document.querySelector('input[name=sign-in-submit]');
    signInBtn.onclick = async function () {
        const email = document.getElementById('email').value;
        const pwd = document.getElementById('password').value;

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
                    const header = util.getCsrfAthentication();
                    header['Content-Type'] = 'text';

                    const data = {
                        'email': email,
                        'pwd': pwd
                    }

                    /* 아이디, 비밀번호 일치 여부를 판단한 뒤, submit하거나 틀린 아이디/비밀번호 임을 알려주기 */
                    await fetch('/sign-in/check', {
                        method: 'POST',
                        headers: header,
                        data: data,
                    }).then(result => {
                        if (result == "didn't matching" || result == "not user") {
                            alert("잘못된 이메일 혹은 비밀번호 입니다.");
                            return false;
                        } else if (result == "not certified") {
                            let conf = confirm("이메일 인증이 필요한 계정입니다. 이메일 재전송을 원하십니까?");
                            if (conf) {
                                sendCertMail(email);
                            }
                            return false;
                        } else {
                            $('#sign-up-form').submit();
                        }
                    }).catch(error => {
                        alert("현재 내부 서버 문제로 로그인이 어렵습니다. 관리자에 문의바랍니다.");
                        console.log(error);
                        return false;
                    })
                }
            }
        }
    }

    async function sendCertMail(email) {
        const csrfHeader = util.getCsrfAthentication();
        await fetch('/certMail', {
            method: 'POST',
            headers: csrfHeader,
            data: {
                'email': email
            },
            type: "post",
        }).then(
            result => {
                if (result.resultCode == "success") alert("인증 메일을 다시 발송했습니다.");
                else alert("메일 전송에 실패했습니다.");
            })
            .catch(error => {
                console.warn(error);
            });
    }

});

function goLoginProc(url) {
    location.href = url;
}

