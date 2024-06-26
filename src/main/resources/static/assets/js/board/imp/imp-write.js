import note from '/assets/js/summernote.js';

$(document).ready(function () {

    note.init();

    const btn = document.getElementById('impSubmitBtn');
    btn.onclick = function() {
        let title = $('#imp_title').val();
        let writerNo = $('#writerNo').val();
        let content = $('.note-editable').html();
        let text = $('.note-editable').text();

        const data = {
            title: title,
            writerNo: writerNo,
            content: content,
            text: text
        }

        note.upload('/imp', data);
    }
});