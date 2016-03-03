/**
 * Created by ChenJie3 on 2016/2/25.
 */
if (!LoginController) {
    var LoginController = function () {
    };

    LoginController.prototype.login = function (user_name, pwd, callback) {
        alert("LoginController: login button clicked");

        var login_data = {
            "phone":user_name,
            "password":pwd
        };

        $.ajax({
            url: "/login", // 不能跨域，只能访问自己本站内容
            async: true,
            timeout: 10000, // ms
            type: 'POST',
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(login_data),
            success: function (data) {
                // process data
                alert("success");
                alert(JSON.stringify(data));

                if (callback)
                {
                    var login_result = {
                        "status": 0,
                        "data":data
                    };
                    callback(login_result);
                }
            },
            error: function(url, options){
                alert("error occured");
                alert(url.toString());
                alert(options.toString());
                if (callback)
                {
                    var login_result = {
                        "status": -1
                    };
                    callback(login_result);
                }
            }
        });

    };
}
