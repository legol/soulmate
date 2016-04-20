/**
 * Created by ChenJie3 on 2016/3/9.
 */

if (!InputController) {
    var InputController = function () {
    };

    InputController.prototype = {

        init: function () {

            var log = log4javascript.getDefaultLogger();
            log.info("InputController init...");

            var r = new Render();
            var template_data = new Object();
            r.render($("#input_container"), "resources/templates/input/input.html", template_data);
        },

        sendMessage: function() {
            var log = log4javascript.getDefaultLogger();
            log.info("send message...");

            var chat_data = new Object();

            $.ajax({
                url: "/chat/broadcast", // 不能跨域，只能访问自己本站内容
                async: true,
                timeout: 10000, // ms
                type: 'POST',
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                data: JSON.stringify(chat_data),
                success: function (response) {
                    // process data
                    var log = log4javascript.getDefaultLogger();
                    log.info("chat success");
                    log.info(JSON.stringify(response));

                },
                error: function(url, options){
                    var log = log4javascript.getDefaultLogger();
                    log.info("chat error occured");
                    log.info(url.toString());
                    log.info(options.toString());

                }
            });


        },

    }

    window.inputcontroller = new InputController();
}
