/**
 * Created by ChenJie3 on 2016/3/9.
 */

if (!UserListController) {
    var UserListController = function () {
    };

    UserListController.prototype = {
        init: function () {
            alert("user list controller")
        },
    }

    window.userlistcontroller = new UserListController();
}
