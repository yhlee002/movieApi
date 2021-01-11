$(document).ready(function () {
    
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    
    // 썸머노트 에디터 구현
    $('#summernote').summernote(
        {
            height: 300,                 // 에디터 높이
            focus: true,                  // 에디터 로딩후 포커스를 맞출지 여부
            lang: "ko-KR",					// 한글 설정
            tabsize: 1,
            toolbar: [
                ['style', ['style']],
                ['fontname', ['fontNames']],
                ['fontsize', ['fontsize']],
                ['color', ['color']], // 'forecolor',
                ['para', ['ul', 'ol', 'paragraph']],
                ['table', ['table']],
                ['insert', ['link', 'picture', 'video']],
                ['view', ['codeview', 'help']] // 'fullscreen'
            ],
            style: ['bold', 'italic', 'underline', 'strikethrough', 'clear'],
            fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New', '맑은 고딕', '궁서', '굴림체', '굴림', '돋음체', '바탕체'],
            fontSizes: ['8', '9', '10', '11', '12', '14', '16', '18', '20', '22', '24', '28', '30', '36', '50', '72'],
            callbacks: {
                onImageUpload: function (files) {
                    uploadSummernoteImageFile(files[0], this);
                },
                onPaste: function (data) {
                    let clipboardData = data.originalEvent.clipboardData;
                    if (clipboardData && clipboardData.items && clipboardData.items.length) {
                        data.preventDefault();
                    }
                }
            }

        }
    );
    
    // 이미지 저장
    function uploadSummernoteImageFile(file, editor) {
        let data = new FormData();
        data.append("file", file);
        $.ajax({
            data: data,
            type: "post",
            url: "/uploadSummernoteImageFile",
            contentType: false,
            processData: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (data) {
                console.log("data : " + data);
                $(editor).summernote('insertImage', data.url);
            },
            error: function (request, status) {
                alert("문제가 발생했습니다. 지속될 경우 관리자에 문의바랍니다.");
                console.log("code : " + status + "\nmessage : " + request.responseText);
            }
        });
    }

    // 게시글 작성시 유효성 검사
    $('#noticeSubmitBtn').on("click", function () {
        let title = $('#notice_title').val();
        let writerNo = $('#writerNo').val();
        let content = $('.note-editable').html();
        let text = $('.note-editable').text();

        if (title.trim().length == 0 || text.trim().length == 0) { 
            alert("제목이나 내용은 비어있을 수 없습니다.");
        } else if (content.length > 2048) {
            alert("최대 2048자 까지 쓸 수 있습니다.");
        } else {
            $.ajax({
                url: "/notice/writeProc",
                type: 'post',
                dataType: 'text',
                data: {
                    'title': title,
                    'writerNo': writerNo,
                    'content': content
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function (data) {
                    console.log("board id : " + data);
                    location.href = "/notice/" + data;
                },
                error: function (request, status) {
                    alert("문제가 발생했습니다. 지속될 경우 관리자에 문의바랍니다." + "title : " + title);
                    console.log("code : " + status + "\nmessage : " + request.responseText);
                }
            });
        }
    });

});