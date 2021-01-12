$(document).ready(function () {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    getCommentList(1);

    // let movieCd = $('#movieCd').val();
    // $('#movieNo').val(movieCd);

    $('#commentSubmitBtn').on("click", function () {
        let commentVal = $('#commentContent').val();
        // let movieNo = $('#movieNo').val();
        let movieNo = $('#movieCd').val();
        let memNo = $('#memNo').val();
        let rating = $('#ratingSelector').val();

        if (memNo == null) {
            alert("로그인 후 가능한 작업입니다.");
        } else {
            if (commentVal != null && commentVal.trim().length != 0) {

                $.ajax({
                    url: "/writeComment",
                    data: {
                        'commentContent': commentVal,
                        'memNo': memNo,
                        'movieNo': movieNo,
                        'rating': rating
                    },
                    type: 'post',
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(header, token);
                    },
                    success(data) {
                        $('#commentContent').reset();
                        // 페이지 새로고침없이 바로 불러오는것이 자동으로 될까?
                        getCommentList(1);

                    }, error: function (request, status) {
                        alert("댓글 작성 중 문제가 발생했습니다. 지속될 경우 관리자에 문의바랍니다.");
                        console.log("code : " + status + "\nmessage : " + request.responseText);
                    }

                });

            } else {
                alert("댓글 내용을 작성해주세요.");
            }
        }


    });

    function getCommentList(pageNum) {
        $.ajax({
            url: "/getCommentListOrderByRegDt",
            data: {
                'p': pageNum
            },
            type: 'get',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success(data) {
                console.log(data);
                $('#commentMovList').html(data.list);

                let str = "";

                if(data.totalPageCnt === 0){
                    $('#commentMovList').html("<p>작성된 코멘트가 없습니다.</p>")
                } else {
                    for (let i = 1; i <= data.totalPageCnt; i++) {
                        str += "<a style='margin:0.2em;' href=\'/getCommentListOrderByRegDt?p=" + i + "\'><p>" + i + "</p></a>";
                    }
                }

                $('#pagenation_commMov').html(str);
            }, error: function (request, status) {
                alert("문제가 발생했습니다. 지속될 경우 관리자에 문의바랍니다.");
                console.log("code : " + status + "\nmessage : " + request.responseText);
            }
        });
    }

});