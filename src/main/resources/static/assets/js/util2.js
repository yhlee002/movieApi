export default {
    getCsrfAthentication: function() {
        const header = {};
        header[this.getHeader()] = this.getToken();
        return header;
    },

    getToken: function() {
        return $("meta[name='_csrf']").attr("content");
    },

    getHeader: function() {
        return $("meta[name='_csrf_header']").attr("content");
    }
}