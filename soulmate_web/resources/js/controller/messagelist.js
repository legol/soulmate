/**
 * Created by ChenJie3 on 2016/3/9.
 */

if (!MessageListController) {
    var MessageListController = function () {
    };

    MessageListController.prototype = {

        init: function () {

            var log = log4javascript.getDefaultLogger();
            log.info("MessageListController init...");

            var r = new Render();
            var template_data = new Object();
            r.render($("#message_container"), "resources/templates/message/messagelist.html", template_data);
        },

    }

    window.messagelistcontroller = new MessageListController();
}
