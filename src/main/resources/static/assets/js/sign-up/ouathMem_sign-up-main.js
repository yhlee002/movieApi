$(function () {

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    // let phone_o = $('#phone').val();
    let phoneCk_o = false;

    $('#phoneBnt_o').on("click", function () {
        phoneCk_o = false;
        if (phone_o.eq("") || phone_o.length > 0) {
            $('#phone').val("");
        }
        window.open("/sign-up/phoneCkForm", "Phone Check Form", "width=500, height=300")
    });

    $('#phoneSbm').on("click", function () {
        let phone = $('input[name=phoneNum]').val();
        let phoneReg = RegExp(/^(01[016789]{1})(\d{3,4})(\d{4})$/);
        let provider = $('input[name=provider]').val();

        console.log("phone : " + phone);

        if (phone == "") {
            alert("핸드폰 번호를 입력해주세요.");
            console.log("핸드폰 번호를 입력해주세요.")
        } else if (phoneReg.test(phone) == false) {
            alert('핸드폰 번호 양식을 확인해주세요');
        } else if (phone.length < 10 || phone.length > 11) {
            alert("핸드폰 번호를 확인해주세요.");
        } else {

            phone = phone.replace(/[^0-9]/g, "").replace(/(^0[0-9]{2})([0-9]+)?([0-9]{4})/, "$1-$2-$3");

            if (provider.eq("naver" || "kakao")) {
                $.ajax({
                    url: "/sign-up/phoneCk/naver",
                    type: "post",
                    data: {"phone": phone},
                    dataType: "text",
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(header, token);
                    },
                    success: function (data) {
                        if (data == "") {
                            let conf = window.confirm("해당하는 번호로 인증 문자를 보냅니다.");
                            if (conf) {
                                location.href = "/sign-up/phoneCkProc?phone=" + phone;
                            }
                        } else {
                            alert("이미 가입된 번호입니다.");
                        }
                        return false;
                    }
                });
            } else if (provider.eq("none")) {
                $.ajax({
                    url: "/sign-up/phoneCk",
                    type: "post",
                    data: {"phone": phone},
                    dataType: "text",
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(header, token);
                    },
                    success: function (data) {
                        if (data == "") {
                            let conf = window.confirm("해당하는 번호로 인증 문자를 보냅니다.");
                            if (conf) {
                                location.href = "/sign-up/phoneCkProc?phone=" + phone;
                            }
                        } else {
                            alert("이미 가입된 번호입니다.");
                        }
                        return false;
                    }
                });
            }
        }
    });

    $('#phoneSbm2').on("click", function () {
        let authKey = $('#authKey').val();
        let phoneAuthKey = $('#phoneAuthKey').val();
        let phoneNum = $('#phoneNum').val();

        // ajax로 컨트롤러에 전송, authKey의 해시값이 phoneAuthKey와 일치할 경우 인증 성공 메세지 전달
        $.ajax({
            url: "/sign-up/phoneCkProc2",
            type: 'post',
            dataType: 'text',
            data: {
                'phoneAuthKey': phoneAuthKey,
                'authKey': authKey
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (data) {
                if (data == "인증되었습니다.") {
                    alert(data);
                    $(opener.document).find('#phone').val(phoneNum);
                    $(opener.document).find('#phoneBnt').val("재인증");
                    window.close();
                    phoneCk_o = true;
                } else {
                    alert(data); // 인증에 실패했습니다.
                    window.close();
                }
            },
            error: function (request, status) {
                alert("내부 서버의 문제로 인해 인증에 실패했습니다.")
                console.log("code : " + status + "\nmessage : " + request.responseText);
                return false;
            }
        });
    });

    // 전체 인증(이메일, 이름, 연락처)
    $('input[name=submit_o]').on("click", function () {
        let phone_o = $('#phone').val();
        if (phone_o != "" || phone_o.length > 0) {
            phoneCk_o = true;
            console.log("phoneCk_o : " + phoneCk_o);
            console.log("phone : "+phone_o);

            let formData = $('#form_signup_o').serialize();
            console.log(formData);

            $.ajax({
                url: "/sign-up/sign-up-processor_n",
                type: "post",
                data: formData,
                dataType: "text",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function (data) {
                    console.log(data);
                    if (data != "") {
                        location.href = "/sign-up/success?oauthMemNo=" + data; /* 로그인 처리되는 화면으로 이동해 바로 메인페이지로 가게 하기 */
                    } else {
                        alert("오류가 발생했습니다. 문제가 반복될 경우 고객센터로 문의바랍니다.");
                    }
                },
                error: function (request, status) {
                    console.log("code : " + status + "\nmessage : " + request.responseText);
                    alert("회원가입에 실패했습니다. 문제가 반복될 경우 고객센터로 문의바랍니다.");
                }
            });
        } else {
            alert("정보를 다시 확인해주세요.");
        }
        return false;
    });
});