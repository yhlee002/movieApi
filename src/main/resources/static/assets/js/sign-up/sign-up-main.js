$(function () {

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    let emailCk = false;
    let nameCk = false;
    let pwdCk = false;
    let phoneCk = false; // 번호를 입력했는데($('phone').val().length != 0) 인증 버튼을 눌러서 인증을 하지 않은 경우(phoneCk != true) submit 불가

    let eMessage = $('#emailMessage');
    let nMessage = $('#nameMessage');
    let pMessage = $('#pwdMessage');
    let pConfMessage = $('#pwdConfMessage');
    let phMessage = $('#phoneMessage');


    // 회원가입 페이지의 이메일 체크
    $('#email').on("blur", function () {
        let email = $('#email').val();
        // let eMessage = $('#emailMessage');
        console.log("email check - email : " + email);

        // 빈 문자열 검사
        if (email == "" || email.length == 0) {
            eMessage.html("이메일을 입력해주세요.");
            eMessage.addClass("visible");
            eMessage.removeClass("invisible");
        } else {
            // 이메일 형식 검사
            let emailCheck = RegExp(/^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/);
            if (emailCheck.test(email)) {
                // email = encodeURIComponent(email);

                $.ajax({
                    url: "/sign-up/emailCk",
                    type: "post",
                    headers: header,
                    data: {'email': email},
                    dataType: "text",
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(header, token);
                    },
                    success: function (data) {
                        console.log(data);
                        if (data == "none") {
                            eMessage.html("사용 가능한 이메일입니다.");
                            eMessage.addClass("visible");
                            eMessage.removeClass("invisible");
                            emailCk = true;
                        } else {
                            eMessage.html("이미 가입된 이메일입니다.");
                            eMessage.addClass("visible");
                            eMessage.removeClass("invisible");
                        }
                    },
                    error: function (request, status) {
                        eMessage.html("죄송합니다. 잠시 후 다시 시도해주세요.");
                        eMessage.addClass("visible");
                        eMessage.removeClass("invisible");
                        console.log("code : " + status + "\nmessage : " + request.responseText);
                        return false;
                    }
                });
            } else {
                eMessage.html("올바른 이메일 형식이 아닙니다.");
                eMessage.addClass("visible");
                eMessage.removeClass("invisible");
            }
        }
    });


    // 이름(닉네임) 체크
    $('#name').on("blur", function () {
        let name = $('#name').val();
        // let nMessage = $('#nameMessage');
        console.log("name check - name : " + name);

        if (name == "" || name.length == 0) {
            nMessage.html("유저명을 입력해주세요.");
            nMessage.addClass("visible");
            nMessage.removeClass("invisible");

        } else {
            // 이름 형식 검사 - 한글, 영문(대,소), 숫자 가능, 띄워쓰기 가능
            let nameReg = RegExp(/^[가-힣|a-z|A-Z|0-9|\S]+$/); // \S : non space <-> \s(space)
            let nameReg2 = RegExp(/[`~!@#$%^&*|\\\'\";:\/?]/gi);

            if (nameReg.test(name) && !nameReg2.test(name)) {
                if (name.length < 2 || name.length > 11) {
                    nMessage.html('닉네임은 2-11자여야합니다.');
                    nMessage.addClass("visible");
                    nMessage.removeClass("invisible");

                } else {
                    name = encodeURIComponent(name);

                    // 이름(닉네임)을 가져와 에이잭스로 DB에 존재하는 이름(닉네임)인지 확인
                    $.ajax({
                        url: "/sign-up/nameCk",
                        type: "post",
                        data: {'name': name},
                        dataType: "text",
                        beforeSend: function (xhr) {
                            xhr.setRequestHeader(header, token);
                        },
                        success: function (data) {
                            console.log(data);
                            if (data == "none") {
                                nMessage.html("사용가능한 회원명입니다.");
                                nMessage.addClass("visible");
                                nMessage.removeClass("invisible");
                                nameCk = true;
                            } else {
                                nMessage.html("이미 존재하는 회원명입니다.");
                                nMessage.addClass("visible");
                                nMessage.removeClass("invisible");
                            }
                        },
                        error: function (request, status) {
                            nMessage.html('죄송합니다. 잠시 후 다시 시도해주세요.');
                            nMessage.addClass("visible");
                            nMessage.removeClass("invisible");
                            console.log("code : " + status + "\nmessage : " + request.responseText);
                            return false;
                        }
                    })
                }
            } else {
                nMessage.html("회원명은 한글, 영문(대소문자), 숫자만 가능하며, 띄워쓰기는 불가능합니다.");
                nMessage.addClass("visible");
                nMessage.removeClass("invisible");
            }
        }
    });

    let pwdCk_pwdBlank = false;
    let pwdCk_pwdConfBlank = false;
    let pwdCk_pwdReg = false;
    let pwdCk_auth = false;


    $('#password').on("blur", function () {
        let pwd = $('#password').val();
        console.log("입력된 password : " + pwd);
        // (1) pwd 빈값 검사
        if (pwd == "" || pwd.length == 0) {
            pMessage.html("비밀번호를 입력해주세요.");
            pMessage.addClass("visible");
            pMessage.removeClass("invisible");
        } else {
            pwdCk_pwdBlank = true;

            // (2) pwd 정규식 검사
            let pwdReg = RegExp(/^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{8,}$/);
            if (pwdReg.test(pwd)) {
                pwdCk_pwdReg = true;
                // 사용 가능한 비밀번호일때 경고 메세지 숨김
                pMessage.addClass("invisible");
                pMessage.removeClass("visible");
            } else {
                pMessage.html("비밀번호는 최소 8자리에 숫자, 문자, 특수문자 각각 1개 이상을 포함해야합니다.");
                pMessage.addClass("visible");
                pMessage.removeClass("invisible");
            }
        }
    });

    $('#passwordConf').on("blur", function () {
        let pwd = $('#password').val();
        let pwdConf = $('#passwordConf').val();
        console.log("입력된 password : " + pwd + "\npasswordConf : " + pwdConf);

        // (3) pwdCk 빈값 검사
        if (pwdCk_pwdBlank == true) {
            if (pwdConf == "" || pwdConf.length == 0) {
                pConfMessage.html("비밀번호를 한번 더 입력해주세요.")
                pConfMessage.addClass("visible");
                pConfMessage.removeClass("invisible");
            } else {
                pwdCk_pwdConfBlank = true;

                // (4)pwd값과 pwdCk의 값 일치 검사
                if (pwdCk_pwdBlank && pwd === pwdConf) { // pwdCk_pwdConfBlank는 이미 true
                    pwdCk_auth = true;
                    pConfMessage.addClass("invisible");
                    pConfMessage.removeClass("visible");

                } else {
                    pConfMessage.html("비밀번호가 일치하지 않습니다.");
                    pConfMessage.addClass("visible");
                    pConfMessage.removeClass("invisible");
                }
            }
        } else {
            pConfMessage.html("비밀번호를 먼저 입력해주세요.");
            pConfMessage.addClass("visible");
            pConfMessage.removeClass("invisible");
        }
        console.log("pwdCk_pwdBlank = " + pwdCk_pwdBlank
            + "\npwdCk_pwdConfBlank = " + pwdCk_pwdConfBlank
            + "\npwdCk_pwdReg = " + pwdCk_pwdReg
            + "\npwdCk_auth = " + pwdCk_auth);
    });

    // 연락처 인증(핸드폰 번호만 가능(010 형식만 가능) : read-only로 두고 번호 변경하기 버튼을 눌러서 인증창 접근 가능.
    // 인증 완료시 사용가능한 핸드폰번호라면 read-only인 채로 $('#phone')에 값만 넣어주기 + 다시 인증하기 버튼 show)
    // 다시 인증하기 버튼 클릭시 $('phone')값은 그대로. 다시 인증하기 버튼 클릭시 인증창으로 보내주고, 인증 완료시 사용 가능한 핸드폰번호라면 $('#phone')에 값 변경 + 다시 인증하기 버튼 show
    $("#phoneBnt").on("click", function () {
        let phone = $('input[name=phone]').val().replace(/-/gi, '');
        let phoneReg = RegExp(/^(01[016789]{1})(\d{3,4})(\d{4})$/);

        // $("#phoneReAuthBnt").show(); // 인증 완료시 다시 인증하기 버튼 보여주기

        if (phone == "") {
            alert("핸드폰 번호를 입력해주세요.");
        } else if (phoneReg.test(phone) == false) {
            alert('핸드폰 번호 양식을 확인해주세요');
        } else if (phone.length < 10 || phone.length > 11) {
            alert("핸드폰 번호를 확인해주세요.");
        } else {
            phone = phone.replace(/[^0-9]/g, "").replace(/(^0[0-9]{2})([0-9]+)?([0-9]{4})/, "$1-$2-$3").replace("--", "-");

            $.ajax({
                url: "/sign-up/phoneCk",
                type: "post",
                data: {"phone": phone},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function (data) {

                    if (data == null) {
                        let conf = window.confirm("해당하는 번호로 인증 문자를 보냅니다.");
                        if (conf) {
                            location.href = "/sign-up/phoneCkPage";
                        }
                    } else {
                        alert("이미 가입된 번호입니다.");
                    }
                    return false;
                }
            });
        }
    });


    // 전체 인증(이메일, 이름, 비밀번호)
    $('input[name=submit]').on("click", function () {
        console.log("emailCk : " + emailCk
            + "\nnameCk : " + nameCk
            + "\npwdCk : " + pwdCk
            + "\nphoneCk : " + phoneCk);
        if (emailCk && nameCk && pwdCk && phoneCk) { // 전화번호는 인증되면 true로 바꾸되, 인증하지 않아도 가입 가능.
            $("form").submit();

            // $.ajax({
            //     url: "/sign-in/sign-in-processor",
            //     type: "post",
            //     data: {'name': name},
            //     dataType: "text",
            //     beforeSend: function (xhr) {
            //         xhr.setRequestHeader(header, token);
            //     },
            //     success: function () {
            //         location.href = "/sign-up/welcome";
            //     },
            //     error: function (request, status) {
            //         console.log("code : " + status + "\nmessage : " + request.responseText);
            //         alert("회원가입에 실패했습니다. 문제가 반복될 경우 고객센터로 문의바랍니다.");
            //     }
            // });
        } else {
            alert("정보를 다시 확인해주세요.");
        }
        return false;
    });

    $('input[name=reset]').on("click", function () {
        console.log("리셋 버튼 클릭");
        eMessage.addClass("invisible");
        eMessage.removeClass("visible");

        nMessage.addClass("invisible");
        nMessage.removeClass("visible");

        pMessage.addClass("invisible");
        pMessage.removeClass("visible");

        pConfMessage.addClass("invisible");
        pConfMessage.removeClass("visible");
    });

})
;