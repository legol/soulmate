/**
 * Created by ChenJie3 on 2016/3/9.
 */

if (!UserListController) {
    var UserListController = function () {
    };

    UserListController.prototype = {



        init: function (userlist) {
            this.reloadList(userlist);

            //start a timer & execute a function every 10 seconds and then reset the timer at the end of 10 seconds.
            var THIS = this;

            $('#timer').timer({
                duration: '30s',
                callback: function() {
                    var log = log4javascript.getDefaultLogger();
                    log.info("timer callback");
                    $('#timer').timer('reset');

                    THIS.queryOnlineClients();
                },
                repeat: true //repeatedly call the callback
            });
        },

        queryOnlineClients: function(){
            var request_data = new Object();
            request_data.uid = window.myself.data.uid;
            request_data.token = window.myself.data.token;

            var THIS = this;

            $.ajax({
                url: "/login/query_online_clients", // 不能跨域，只能访问自己本站内容
                async: true,
                timeout: 10000, // ms
                type: 'POST',
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                data: JSON.stringify(request_data),
                success: function (response) {
                    // process data
                    var log = log4javascript.getDefaultLogger();
                    log.info("query_online_clients result:");
                    log.info(JSON.stringify(response));

                    if (response.err_no == 0){
                        window.userList = new Array();
                        var arrayLength = response.data.clients.length;
                        for (var i = 0; i < arrayLength; i++) {
                            var user = new User();
                            user.data.name = response.data.clients[i].name;
                            user.data.uid = response.data.clients[i].uid;
                            user.data.phone = response.data.clients[i].phone;

                            window.userList.push(user);
                        }

                        THIS.reloadList(window.userList);
                    }else{
                        log.info("query_online_clients failed. ignore silently");
                    }
                },
                error: function(url, options){
                    var log = log4javascript.getDefaultLogger();
                    log.info("query_online_clients failed. ignore silently");
                    log.info(url.toString());
                    log.info(options.toString());
                }
            });
        },

        reloadList: function(userlist){
            var r = new Render();
            var template_data = new Object();
            template_data.users = userlist;

            var log = log4javascript.getDefaultLogger();
            log.info(JSON.stringify(template_data));
            r.render($("#member_container"), "resources/templates/userlist/userlist.html", template_data);
        },


    }

    window.userlistcontroller = new UserListController();
}
