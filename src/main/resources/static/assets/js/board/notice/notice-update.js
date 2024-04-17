import note from '/assets/js/summernote.js';

$(document).ready(function () {

    note.init();

    const btn = document.getElementById('noticeUpdateSubmitBtn');
    btn.onclick = function() {
        const boardId = $('#boardId').val();
        const title = $('#notice_title').val();
        const writerNo = $('#writerNo').val();
        const content = $('.note-editable').html();
        const text = $('.note-editable').text();

        const data = {
            boardId: boardId,
            title: title,
            writerNo: writerNo,
            content: content,
            text: text
        }

        note.upload('/notice', data);
    }
});