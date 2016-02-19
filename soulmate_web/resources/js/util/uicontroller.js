/**
 * Created by ChenJie3 on 2016/2/18.
 */

if (!UIController) {
    var UIController = function () {
    };

    UIController.prototype = {
        main: {
            init: function (data) {
                var r = new Render();
                r.render($("#main_container"), "resources/templates/login/login.html", data);
            },
        }
    };

    window.uicontroller = new UIController();
}

