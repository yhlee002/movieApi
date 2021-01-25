$(function () {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    $('#phoneBnt').on("click", function () {
        window.open("/sign-up/phoneCkForm", "Phone Check Form", "width=500, height=300")
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

            $.ajax({
                url: "/findEmail/phoneCk",
                type: "post",
                data: {"phone": phone},
                dataType: "text",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function (data) {
                    let result = JSON.parse(data);
                    if (result.resultCode === "exist") {
                        let conf = window.confirm("해당하는 번호로 인증 문자를 보냅니다.");
                        if (conf) {
                            location.href = "/findEmail/checkCertKey?p="+phone;
                        }
                    } else { // not exist
                        alert("해당 정보의 계정이 없습니다.");
                    }
                },
                error: function (request, status) {
                    console.warn("code : " + status + "\nmessage : " + request.responseText);
                }
            });
        }
    });

    $('#phoneSbm2').on("click", function () {
        let certKey = $('#certKey').val();

        $.ajax({
            url: "/findEmail/checkAuthKey",
            type: 'post',
            data: {
                'certKeyInput': certKey
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (data) {
                if (data.resultCode === "true") {
                    alert("인증에 성공했습니다.");
                    location.href="/findEmail/result";
                } else {
                    alert("인증번호가 다릅니다."); //
                }
            },
            error: function (request, status) {
                alert("내부 서버의 문제로 인해 인증에 실패했습니다.")
                console.warn("code : " + status + "\nmessage : " + request.responseText);
            }
        });
    });
});