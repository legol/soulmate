if (!WebSocketController) {
    var WebSocketController = function () {
        this.data = new Object();
        this.data.endPointURL = "";
        this.data.wsclient = null;

        this.data.onmsg = null;
        this.data.onopen = null;
        this.data.onclose = null;
        this.data.onerror = null;
    };

    WebSocketController.version = "1.0.0";
    WebSocketController.prototype = {
        init: function(endpointURL, fnOnMsg){
            this.data.endPointURL = endpointURL;
            this.data.onmsg = fnOnMsg;

            var log = log4javascript.getDefaultLogger();
            log.info("WebSocketController");
        },

        onmsg: function(event){
            var msgObj = JSON.parse(event.data);
            if (this.data.onmsg) {
                this.data.onmsg(msgObj);
            }
        },

        onclose: function(event){
            this.data.wsclient = null;
        },

        onopen: function(event){

        },

        connect: function () {
            this.data.wsclient = new WebSocket(this.data.endPointURL);
            this.data.wsclient.onmessage = this.onmsg;
            this.data.wsclient.onclose = this.onclose;
            this.data.wsclient.onopen = this.onopen;
            this.data.wsclient.onerror = this.onerror;
        },

        close: function(){
            if (this.data.wsclient){
                this.data.wsclient.close();
            }
        },

        send: function(msgObj) {
            if (this.data.wsclient){
                this.data.wsclient.send(JSON.stringify(msgObj));
            }
        }
    }

    window.wscontroller = new WebSocketController();
}

