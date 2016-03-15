/**
 * Created by ChenJie3 on 2016/3/9.
 */
if (!User) {
    var User = function () {
        this.data = new Object();
        this.data.name = "";
    };

    User.version = "1.0.0";
    User.prototype = {
        getEmptyUser: function () {
        },
    }

}

if (!window.userList){
    var userList = new Array();
    window.userList = userList;
}
