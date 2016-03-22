if (!WebSocketController) {
    var WebSocketController = function () {
        this.data = new Object();
        this.data.endPointURL = "";
        this.data.wsclient = null;

        this.data.onmessage = null;
        this.data.onopen = null;
        this.data.onclose = null;
        this.data.onerror = null;
    };

    WebSocketController.version = "1.0.0";
    WebSocketController.prototype = {
        init: function(endpointURL, fnOnMsg){
            this.data.endPointURL = endpointURL;
            this.data.onmessage = fnOnMsg;

            var log = log4javascript.getDefaultLogger();
            log.info("ws: init endpointURL=" + endpointURL);
        },

        onmessage: function(event){
            log.info("ws: onmessge" + this.data.endPointURL);

            var msgObj = JSON.parse(event.data);
            if (this.data.onmessage) {
                this.data.onmessage(msgObj);
            }
        },

        onclose: function(event){
            log.info("ws: onclose" + this.data.endPointURL);

            this.data.wsclient = null;
        },

        onopen: function(event){
            log.info("ws: onopen" + this.data.endPointURL);

        },

        connect: function () {
            log.info("ws: connecting to" + this.data.endPointURL);

            this.data.wsclient = new WebSocket(this.data.endPointURL);
            this.data.wsclient.onmessage = this.onmessage;
            this.data.wsclient.onclose = this.onclose;
            this.data.wsclient.onopen = this.onopen;
            this.data.wsclient.onerror = this.onerror;
        },

        close: function(){
            log.info("ws: close connection to" + this.data.endPointURL);

            if (this.data.wsclient){
                this.data.wsclient.close();
            }
        },

        send: function(msgObj) {
            log.info("ws: send msg to" + this.data.endPointURL);

            if (this.data.wsclient){
                this.data.wsclient.send(JSON.stringify(msgObj));
            }
        }
    }

    window.wscontroller = new WebSocketController();
}

