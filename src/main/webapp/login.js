setTimeout( function() {
    if(window.parent && window.parent != window.self) {
        window.parent.location.href = "login.html";
    }
}, 300 );

/*********************** 应用配置 开始 **********************************/
 var 
    indexPage = "index.html";    /* 登录成功后跳转到的页面 */
 
 function setSysTitle() {
    getParam('sysTitle', function(title) {
        if( title ) {
            indexPage = "tssbi.html";
        } else {
            title = '它山石基础平台';       
        }
        
        $("#sysTitle").html(title);
     });
 }

/*********************** 应用配置 END **********************************/

URL_GET_USER_NAME = "getLoginInfo.in";
URL_LOGIN = AUTH_PATH + "login.do";

window.onresize = function() {
    $("#login_box").center();
}

$(function(){
    if(document.body.offsetWidth < 768 || $.isMobile ) {
        location.href = "mobile/login.html";
    }

     setSysTitle(); 
     window.onresize();

     init();
     window.onresize();
});

function init() {
    var accoutEl = $1("loginName"), passwdEl  = $1("password");
    accoutEl.onfocus = function() {  passwdEl.disabled = true; }
    accoutEl.value = $.Cookie.getValue("iUserName") || "";
    accoutEl.focus();

    $("#bt_login").click ( function() {
        doLogin(accoutEl, passwdEl);
    } );

    $.Event.addEvent(document, "keydown", function(ev) {
        if(13 == ev.keyCode) { // enter
            $.Event.cancel(ev);
            $("#bt_login").focus();

            setTimeout(function() {
                doLogin(accoutEl, passwdEl);
            }, 10);
        }
    });

    accoutEl.onblur = function() { 
        var value = this.value;
        if( !value ) {
            return $(accoutEl).focus().notice("请输入账号");
        }
        if(accoutEl.identifier) {
            delete accoutEl.identifier;
        }
        $.ajax({
            url: URL_GET_USER_NAME,
            params: {"loginName": value},
            waiting: true,
            onexcption: function() {
                accoutEl.focus();
            },
            onresult: function(){
                accoutEl.identifier = this.getNodeValue("identifier");
                accoutEl.randomKey  = this.getNodeValue("randomKey");
                
                passwdEl.disabled = false;
                passwdEl.focus();
            }
        });
    }
}

var doLogin = function(accoutEl, passwdEl) {
    var identifier = accoutEl.identifier;
    var randomKey  = accoutEl.randomKey;   
    var loginName  = accoutEl.value;
    var password   = passwdEl.value;
    
    if( !loginName ) {
        return $(accoutEl).focus().notice("请输入账号");
    } 
    else if( !password ) {
        return $(passwdEl).focus().notice("请输入密码");
    }
    else if( !identifier ) {
        return alert("系统无法登录，服务器异常，请联系管理员。");
    }

    $.ajax({
        url: URL_LOGIN,
        waiting: true,
        headers : {
            "loginName": $.encode(loginName, randomKey), 
            "password":  $.encode(password, randomKey), 
            "identifier": identifier
        },
        params: { 
        	"_password": hex_md5(password)
        },
        onexception: function(errorMsg) {
            passwdEl.focus();
        },
        onsuccess: function() {
            $.Cookie.setValue("iUserName", loginName);
                       
            checkPasswdSecurity(loginName, password, function(securityLevel) {
                if( securityLevel >= 3 ) return gotoIndex(loginName);

                $.prompt("因您的密码过于简单或长期未修改，存在安全隐患，请重新设置密码", "输入更安全密码(8位以上大小写字母和数字组合)", function(newPasswd) {
                    $.prompt("请再次输入新密码", "请再次输入新密码", function(newPasswd2) {
                        if( newPasswd !== newPasswd2) {
                            $.alert("两次密码输入不一致，设置失败，请用原密码登录再次重置密码。");
                            setTimeout( function() { logout(); }, 2000);
                            return;
                        }

                        checkPasswdSecurity(loginName, newPasswd, function(level) {
                            if(level >= 3) {
                                setNewPassword(loginName, password, newPasswd);
                            } else {
                                $.alert("您的新密码还是过于简单，设置失败，请用原密码登录再次重置密码。");
                                setTimeout( function() { logout(); }, 2000);
                            }
                        });
                    }, "", true);
                }, "", true);
            } );      
        }
    });
}

function checkPasswdSecurity(loginName, passwd, callback) {
    $.ajax({
        url : "/tss/getPasswordStrength.in",
        params : {"password": passwd, "loginName": loginName}, 
        onresult : function() {
            var securityLevel = parseInt(this.getNodeValue("SecurityLevel"));
            callback(securityLevel);
        }
    });
}

/* 设置新密码 */
function setNewPassword(loginName, passwd, newPasswd) {
    $.getJSON("/tss/auth/user/has", {}, function(ui) {
        var params = {};
        params.userId = ui[2];
        params.password = passwd;
        params.newPassword = newPasswd;
        $.ajax({
            url : "/tss/resetPassword.in",
            params : params,
            onsuccess : function() { 
                gotoIndex(loginName);
            }
        });
    }, "GET");  
}

function logout() {
    $.ajax({
        url : "/tss/logout.in",
        method : "GET",
        onsuccess : function() { 
            $.Cookie.del("token", "");
            $.Cookie.del("token", "/");
            $.Cookie.del("token", "/tss");
            $.Cookie.del("token", "/tss/");
            location.href = "/tss/login.html";
        }
    });
}

function gotoIndex(loginName) {
    if(loginName === 'Admin') {
        indexPage = 'index.html';
    }

    if(window.parent && window.parent != window.self) {
        window.parent.location.href = indexPage;
    } else {
        window.location.href = indexPage;
    }
}

/* 找回密码  */
function forget() {
    $.openIframePanel("forgetPanel", "重置密码", 460, 300, "modules/um/_forget.html", true);
}

/* 注册 */
function register() {
    $.openIframePanel("registerPanel", "注册新用户", 550, 320, "modules/um/_register.htm", true);
}