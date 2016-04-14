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
        init: function(endpointURL, fnOnMsg, fnOnOpen, fnOnClose, fnOnError){
            this.data.endPointURL = endpointURL;
            this.data.onmessage = fnOnMsg;
            this.data.onopen = fnOnOpen;
            this.data.onclose = fnOnClose;
            this.data.onerror = fnOnError;

            var log = log4javascript.getDefaultLogger();
            log.info("ws: init endpointURL: " + endpointURL);
        },

        onmessage: function(event){
            var log = log4javascript.getDefaultLogger();
            log.info("ws: onmessge: " + JSON.stringify(event.data));

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
            if (this.data.onclose) {
                this.data.onclose(event);
            }
        },

        onopen: function(event){
            var log = log4javascript.getDefaultLogger();
            log.info("ws: onopen: " + JSON.stringify(event));

            if (this.data.onopen) {
                this.data.onopen(event);
            }
        },

        onerror: function(event){
            var log = log4javascript.getDefaultLogger();
            log.info("ws: onopen: " + JSON.stringify(event));

            if (this.data.onerror) {
                this.data.onerror(event);
            }
        },

        send: function(msgObj) {
            var log = log4javascript.getDefaultLogger();
            log.info("ws: send msg <" + JSON.stringify(msgObj) + "> to " + this.data.endPointURL);

            if (this.data.wsclient){
                this.data.wsclient.send(JSON.stringify(msgObj));
            }
        },

        connect: function () {
            var log = log4javascript.getDefaultLogger();
            log.info("ws: connecting to:" + this.data.endPointURL);

            this.data.wsclient = new WebSocket(this.data.endPointURL);
            this.data.wsclient.onmessage = $.proxy(this.onmessage, this);
            this.data.wsclient.onclose = $.proxy(this.onclose,this);
            this.data.wsclient.onopen = $.proxy(this.onopen,this);
            this.data.wsclient.onerror = $.proxy(this.onerror,this);
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

    $(window).unload(function() {
        window.wscontroller.close();
    });
}

