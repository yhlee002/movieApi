$(function () {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    $('#findPwdBtn').on("click", function () {
        let email = $('input[name=email]').val();
        let emailCheck = RegExp(/^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/);

        if (emailCheck.test(email)) {
            $.ajax({
                url: "/findPwd/checkEmail",
                type: "post",
                data: {
                    'email': email
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function (data) {
                    if (data === "exist") {
                        let conf = confirm("해당 이메일로 인증 메일을 전송합니다.");
                        if (conf) {
                            sendMail(email);
                        }
                    }
                },
                error: function (request, status) {
                    console.warn("code : " + status + "\nmessage : " + request.responseText);
                }


            });
        }

    });

    function sendMail(email) {
        $.ajax({
           url:"/findPwd/sendMail",
            type: "post",
            data: {
               'email': email
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (data) {
                if (data === "success") {
                    window.close();
                    opener.alert("이메일 전송에 성공했습니다.");
                } else{
                    window.close();
                    opener.alert("서버 내부 문제로 이메일 전송에 실패했습니다. 문제가 지속될 경우 관리자에게 문의바랍니다.");
                }
            },
            error: function (request, status) {
                console.warn("code : " + status + "\nmessage : " + request.responseText);
            }
        });
    }

    $('#updatePwdBtn').on("click", function () {
        let pwd = $('input[name=pwd]').val();
        let pwdCk = $('input[name=pwdCk]').val();

        console.log(pwd);

        if (pwd === "") {
            alert("변경할 비밀번호를 입력해주세요.");
        } else if (pwd !== pwdCk) {
            alert("비밀번호가 일치하지 않습니다.");
        } else {
            let pwdReg = RegExp(/^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{8,}$/);
            if (pwdReg.test(pwd)) {
                $.ajax({
                    url: "/findPwd/updatePwdProc",
                    type: "post",
                    data: {
                        'pwd': pwd
                    },
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(header, token);
                    },
                    success: function (data) { // ""
                        alert("비밀번호가 변경되었습니다.");
                        location.href="/";
                    },
                    error: function (request, status) {
                        console.warn("code : " + status + "\nmessage : " + request.responseText);
                    }
                });
            }else{
                alert("비밀번호는 최소 8자리에 숫자, 문자, 특수문자 각각 1개 이상을 포함해야합니다.");
            }
        }
    });
});
