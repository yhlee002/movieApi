$(function () {
    $('input[name=delete_info]').on("click", function () {
        let conf = window.confirm("유저 정보를 삭제하시겠습니까? 작성하신 글과 댓글이 모두 함께 삭제됩니다.");
        if (conf) {
            $('form[name=delete_user_form]').submit();
        } else {
            return false;
        }

    });

    let provider = $('input[name=provider]').val();
    if (provider === "naver" || provider === "kakao") {
        $('input[name=pwd]').attr("readonly", true);
        $('input[name=pwd_checked]').attr("readonly", true);
    }

    $('input[name=modifyinfo_submitBtn]').on("click", function () {
        let pwd = $('input[name=pwd]').val();
        let pwdChecked = $('input[name=pwd_checked]').val();

        /** 패스워드 칸이 빈칸이면 값 전송 X(Document Elements remove) */
        if ((pwd == "") || (pwd.length == 0)) {
            pwd = null;
            let conf = confirm("정보가 변경됩니다. 계속하시겠습니까?");
            if (conf) {
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
                        $('form[name=modify_info_form]').submit();
                    }
                } else {
                    alert("비밀번호는 최소 8자리에 숫자, 문자, 특수문자 각각 1개 이상을 포함해야합니다.");
                }
            }
        }


    });

});