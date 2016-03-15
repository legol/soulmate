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
                window.logincontroller.login($("#user_name_input").val(), $("#pwd_input").val(),
                    function(status, login_result){
                    alert("login callback");
                    alert(JSON.stringify(login_result));

                    var r = new Render();

                    // save login result.
                    if (status == true){
                        storage = sessionStorage;

                        if (login_result.err_no == 0){
                            storage.uid = login_result.data.uid;
                            storage.token = login_result.data.token;

                            var myself = new User();
                            myself.data.name = "myself";
                            myself.data.uid = storage.uid;
                            myself.data.token = storage.token;

                            window.userList.push(myself);

                            r.render($("#main_container"), "resources/templates/chat/lobby.html", JSON.stringify(login_result));
                        }
                        else{
                            r.render($("#main_container"), "resources/templates/login/login_failure.html", JSON.stringify(login_result));
                        }
                    }
                    else {
                        r.render($("#main_container"), "resources/templates/login/login_failure.html", JSON.stringify(login_result));
                    }

                });
            }
        },


    };

    window.uicontroller = new UIController();
    window.logincontroller = new LoginController();
}

