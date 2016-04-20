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

        },

    }

    window.inputcontroller = new InputController();
}
