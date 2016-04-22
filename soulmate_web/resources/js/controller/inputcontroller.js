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

            var msg = new Object();
            msg.type = "broadcast";
            msg.uid = window.myself.data.uid;
            msg.token = window.myself.data.token;
            msg.messages = new Array();

            var msg_segment = new Object();
            msg_segment.type = 1234;
            msg_segment.message = $("#messageInput").val();
            msg.messages.push(msg_segment);

            log.info("sending message..." + JSON.stringify(msg));


            $.ajax({
                url: "/chat/broadcast", // 不能跨域，只能访问自己本站内容
                async: true,
                timeout: 10000, // ms
                type: 'POST',
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                data: JSON.stringify(msg),
                success: function (response) {
                    // process data
                    var log = log4javascript.getDefaultLogger();
                    log.info("broadcast success.");
                    log.info(JSON.stringify(response));

                    $("#messageInput").val("");
                    $("#messageInput").focusin();
                },
                error: function(url, options){
                    var log = log4javascript.getDefaultLogger();
                    log.info("broadcast error.");
                    log.info(url.toString());
                    log.info(options.toString());

                }
            });


        },

    }

    window.inputcontroller = new InputController();
}
