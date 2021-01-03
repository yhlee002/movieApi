$(function () {
   $('button[name=delete_info]').on("click", function(){
      let conf = window.confirm("유저 정보를 삭제하시겠습니까? 작성하신 글과 댓글이 모두 함께 삭제됩니다.");
   });
});