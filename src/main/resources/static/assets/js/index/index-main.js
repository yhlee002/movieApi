$(function () {
    $('.searchMovieBtn').on("click", function () {
        let keyword = $('input[name=keyword]').val().replace(" ", "");
        if (keyword != null && keyword.length >= 2) {
            location.href = "/movieInfo/search?q=" + keyword + "&page=1";
        } else {
            alert("2글자이상 입력해주세요.");
        }
    });


});


