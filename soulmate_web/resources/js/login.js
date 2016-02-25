/**
 * Created by ChenJie3 on 2016/2/25.
 */
if (!LoginController) {
    var LoginController = function () {
    };

    LoginController.prototype.login = function (user_name, pwd) {
        alert("LoginController: login button clicked");

        $.ajax({
            url: "", // 不能跨域，只能访问自己本站内容
            dataType: 'text',
            async: true,
            timeout: 5000, // ms
            type: 'POST',
            success: function (data) {
                // process data
                alert("success");
                alert(data.toString());
            },
            error: function(url, options){
                alert("error occured");
                alert(url.toString());
                alert(options.toString());
            }
        });

    };
}
