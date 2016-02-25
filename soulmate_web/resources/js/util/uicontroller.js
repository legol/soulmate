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
        },

        login: {
            init: function(data){
                var submitButton = $("#loginButton");
                //submitButton.on("click",this.submit); // this works too
                submitButton.bind("click", this.submit);
            },

            submit: function(){
                window.logincontroller.login("hello");
            }
        }
    };

    window.uicontroller = new UIController();
    window.logincontroller = new LoginController();
}

