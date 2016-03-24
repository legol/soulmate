if (!WebSocketController) {
    var WebSocketController = function () {
        this.data = new Object();
        this.data.endPointURL = "";
        this.data.wsclient = null;

        this.data.latestMessageId = 0;

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
            log.info("ws: init endpointURL: " + endpointURL);
        },

        onmessage: function(event){
            var log = log4javascript.getDefaultLogger();
            log.info("ws: onmessge: " + JSON.stringify(event.data));

            alert("onmessage: " + JSON.stringify(event.data));
            var msgObj = JSON.parse(event.data);
            if (this.data.onmessage) {
                this.data.onmessage(msgObj);
            }
        },

        getNewMessageId:function (){
            return ++this.data.latestMessageId;
        },

        onclose: function(event){
            var log = log4javascript.getDefaultLogger();
            log.info("ws: onclose: " + JSON.stringify(event));

            this.data.wsclient = null;
        },

        onopen: function(event){
            var log = log4javascript.getDefaultLogger();
            log.info("ws: onopen: " + JSON.stringify(event));

            var ws_authentication = new Object();

            ws_authentication.uid = window.myself.data.uid;
            ws_authentication.token = window.myself.data.token;
            ws_authentication.msgid = window.wscontroller.getNewMessageId();

            window.wscontroller.send(ws_authentication);
        },

        onerror: function(event){
            var log = log4javascript.getDefaultLogger();
            log.info("ws: onopen: " + JSON.stringify(event));
        },

        send: function(msgObj) {
            var log = log4javascript.getDefaultLogger();
            log.info("ws: send msg <" + JSON.stringify(msgObj) + "> to" + this.data.endPointURL);

            if (this.data.wsclient){
                this.data.wsclient.send(JSON.stringify(msgObj));
            }
        },

        connect: function () {
            var log = log4javascript.getDefaultLogger();
            log.info("ws: connecting to:" + this.data.endPointURL);

            var THIS = this;

            this.data.wsclient = new WebSocket(this.data.endPointURL);
            this.data.wsclient.onmessage = function(event){
                THIS.onmessage(event);
            };
            this.data.wsclient.onclose = function(event){
                THIS.onclose(event);
            };
            this.data.wsclient.onopen = function(event){
                THIS.onopen(event);
            };
            this.data.wsclient.onerror = function(event){
                THIS.onerror(event);
            }
        },

        close: function(){
            var log = log4javascript.getDefaultLogger();
            log.info("ws: close connection to:" + this.data.endPointURL);

            if (this.data.wsclient){
                this.data.wsclient.close();
            }
        },

    }

    window.wscontroller = new WebSocketController();
}

