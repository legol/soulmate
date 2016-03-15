/**
 * Created by ChenJie3 on 2016/3/9.
 */

if (!UserListController) {
    var UserListController = function () {
    };

    UserListController.prototype = {
        init: function (data) {
            alert("user list controller");

            var r = new Render();
            var template_data = new Object();
            template_data.users = data;

            alert(JSON.stringify(template_data));
            r.render($("#member_container"), "resources/templates/userlist/userlist.html", template_data);
        },

    }

    window.userlistcontroller = new UserListController();
}
