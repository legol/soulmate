/**
 * Created by ChenJie3 on 2016/2/18.
 */

if (!UIController) {
    var UIController = function () {
        this.data = new Object();
        var log = log4javascript.getDefaultLogger();
        log.info("hello world!");
    };

    UIController.prototype = {
        main: {
            init: function (data) {
                var r = new Render();
                r.render($("#main_container"), "resources/templates/login/login.html", data);
            },
        },

        login: {
            init: function(data){
                var submitButton = $("#loginButton");
                //submitButton.on("click",this.submit); // this works too
                submitButton.bind("click", $.proxy(this.submit, this));
            },

            onLoginResult: function(status, login_result){

                var log = log4javascript.getDefaultLogger();
                log.info("login callback");
                log.info(JSON.stringify(login_result));

                var r = new Render();

                // save login result.
                if (status == true){
                    storage = sessionStorage;

                    if (login_result.err_no == 0){
                        storage.uid = login_result.data.uid;
                        storage.token = login_result.data.token;

                        var myself = new User();
                        myself.data.name = "myself";
                        myself.data.uid = storage.uid;
                        myself.data.token = storage.token;

                        window.myself = myself;

                        r.render($("#main_container"), "resources/templates/chat/lobby.html", JSON.stringify(login_result));

                        var servers = login_result.data.servers.info;
                        var arrayLength = servers.length;
                        for (var i = 0; i < arrayLength; i++) {
                            if (servers[i].role.toUpperCase() == "WEBSOCKET"){
                                log.info("websocket info: " + JSON.stringify(servers[i]));
                                window.wscontroller.init(servers[i].url,
                                    $.proxy(window.uicontroller.websocket.onmessage, window.uicontroller.websocket),
                                    $.proxy(window.uicontroller.websocket.onopen, window.uicontroller.websocket),
                                    $.proxy(window.uicontroller.websocket.onclose, window.uicontroller.websocket),
                                    $.proxy(window.uicontroller.websocket.onerror, window.uicontroller.websocket));


                                $.proxy(window.wscontroller.connect, window.wscontroller)();

                                break;
                            }
                        }

                    }
                    else{
                        r.render($("#main_container"), "resources/templates/login/login_failure.html", JSON.stringify(login_result));
                    }
                }
                else {
                    r.render($("#main_container"), "resources/templates/login/login_failure.html", JSON.stringify(login_result));
                }
            },

            submit: function(){
                window.logincontroller.login($("#user_name_input").val(), $("#pwd_input").val(), $.proxy(this.onLoginResult, this));
            }
        },

        websocket:{
            onmessage:function(msg){
                var log = log4javascript.getDefaultLogger();
                log.info("uicontroller.websocket.onmessage: " + JSON.stringify(msg));

                window.messagelistcontroller.addMsg(msg);

                if (msg.type == "online_clients_changed"){
                    window.userlistcontroller.queryOnlineClients();
                }else if(msg.type == "chat"){

                }
            },

            onopen:function(event){
                var log = log4javascript.getDefaultLogger();
                log.info("uicontroller.websocket.onopen");

                var ws_authentication = new Object();

                ws_authentication.uid = window.myself.data.uid;
                ws_authentication.token = window.myself.data.token;
                ws_authentication.msgid = window.wscontroller.getNewMessageId();

                $.proxy(window.wscontroller.send, window.wscontroller)(ws_authentication);
            },

            onclose:function(event){
                var log = log4javascript.getDefaultLogger();
                log.info("retry connect to websocket in 3 seconds...");

                // auto reconnect after 3 seconds
                $('#timer').timer({
                    duration: '10s',
                    callback: function() {
                        $.proxy(window.wscontroller.connect, window.wscontroller)();
                        $('#timer').timer('remove');
                    },
                    repeat: false //repeatedly call the callback
                });


            },

            onerror:function(event){

            },
        },
    };

    window.uicontroller = new UIController();
    window.logincontroller = new LoginController();
}

