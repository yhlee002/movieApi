$(function () {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    $('#delete_info').on("click", function () {
        let conf = window.confirm("유저 정보를 삭제하시겠습니까? 작성하신 글과 댓글이 모두 함께 삭제됩니다.");
        if (conf) {
            location.href='/mypage/delete_info';
        } else {
            return false;
        }

    });

    $('#profileImageDlBtn').on("click", function () {
        $('.profile_image').attr("src", "/images/test-account-96.png");
    });

    let provider = $('input[name=provider]').val();
    if (provider === "naver" || provider === "kakao") {
        $('input[name=pwd]').attr("readonly", true);
        $('input[name=pwd_checked]').attr("readonly", true);
    }

    $('input[name=modifyinfo_submitBtn]').on("click", function () {
        let pwd = $('input[name=pwd]').val();
        let pwdChecked = $('input[name=pwd_checked]').val();
        let profileImage = $('.profile_image').attr("src");

        if (profileImage === "/images/test-account-96.png") {
            profileImage = null;
        }

        let inputProfileImage = document.createElement("input");
        inputProfileImage.type = "hidden";
        inputProfileImage.name = "profileImage";
        inputProfileImage.value = profileImage;

        /** 패스워드 칸이 빈칸이면 값 전송 X(Document Elements remove) */
        if ((pwd == "") || (pwd.length == 0)) {
            pwd = null;
            let conf = confirm("정보가 변경됩니다. 계속하시겠습니까?");
            if (conf) {
                $('form[name=modify_info_form]').append(inputProfileImage);
                $('form[name=modify_info_form]').submit();
            }
        } else { /* 빈값이 아니라면 pwdChecked와 비교해 같을 경우에만 값 전송, 값이 다를 경우엔 전송하지 않고 return false; */
            if (pwd != pwdChecked) {
                window.alert("비밀번호를 확인해주세요.");
            } else {
                //유효성 검사
                let pwdReg = RegExp(/^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{8,}$/);
                if (pwdReg.test(pwd)) {
                    let conf2 = confirm("정보가 변경됩니다. 계속하시겠습니까?");
                    if (conf2) {
                        $('form[name=modify_info_form]').append(inputProfileImage);
                        $('form[name=modify_info_form]').submit();
                    }
                } else {
                    alert("비밀번호는 최소 8자리에 숫자, 문자, 특수문자 각각 1개 이상을 포함해야합니다.");
                }
            }
        }
    });

    // 변경할 핸드폰 번호
    $('#phoneUpdateBtn').on("click", function () {
        window.open("/mypage/modify_info/phoneCk", "Phone Check Form", "width=500, height=300");
    });

    $('#phoneSbm').on("click", function () {
        let phone = $('input[name=phoneNum]').val();
        let phoneReg = RegExp(/^(01[016789]{1})(\d{3,4})(\d{4})$/);

        if (phone == "") {
            alert("핸드폰 번호를 입력해주세요.");
        } else if (phoneReg.test(phone) == false) {
            alert('핸드폰 번호 양식을 확인해주세요');
        } else if (phone.length < 10 || phone.length > 11) {
            alert("핸드폰 번호를 확인해주세요.");
        } else {
            phone = phone.replace(/[^0-9]/g, "").replace(/(^0[0-9]{2})([0-9]+)?([0-9]{4})/, "$1-$2-$3");

            provider = $("#provider", opener.document).val();
            console.log("provider : "+provider);

            $.ajax({
                url: "/mypage/modify_info/phoneCkProc",
                type: "post",
                data: {"phone": phone},
                dataType: "text",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function (data) {
                    if (data == "not exist") {
                        let conf = window.confirm("해당하는 번호로 인증 문자를 보냅니다.");
                        if (conf) {
                            location.href = "/mypage/modify_info/phoneCkCert?phone=" + phone;
                        }
                    } else {
                        alert("이미 가입된 번호입니다.");
                    }
                    return false;
                }
            });

        }
    });

    $('#phoneSbm2').on("click", function () {
        let certKey = $('#certKey').val();

        // ajax로 컨트롤러에 전송, authKey의 해시값이 phoneAuthKey와 일치할 경우 인증 성공 메세지 전달
        $.ajax({
            url: "/mypage/modify_info/phoneCkCertProc",
            type: 'post',
            dataType: 'text',
            data: {
                'certKey': certKey,
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (data) {
                let result = JSON.parse(data);
                if (result.resultCode == "true") {
                    alert("인증에 성공하였습니다.");
                    $(opener.document).find('#phone').val(result.phoneNum);
                    window.close();
                    phoneCk = true;
                } else { // result.resultCode == "false"
                    alert("인증에 실패했습니다.");
                    window.close();
                }
            },
            error: function (request, status) {
                alert("내부 서버의 문제로 인해 인증에 실패했습니다.")
                console.warn("code : " + status + "\nmessage : " + request.responseText);
                return false;
            }
        });

    });

});