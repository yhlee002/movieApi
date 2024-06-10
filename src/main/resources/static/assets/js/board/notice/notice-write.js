import note from '/assets/js/summernote.js';

$(document).ready(function () {

    note.init();

    const btn = document.getElementById('noticeSubmitBtn');
    btn.onclick = function() {
        let title = $('#notice_title').val();
        let writerNo = $('#writerNo').val();
        let content = $('.note-editable').html();
        let text = $('.note-editable').text();

        const data = {
            title: title,
            writerNo: writerNo,
            content: content,
            text: text
        }

        note.upload('/notice', data);
    }
});