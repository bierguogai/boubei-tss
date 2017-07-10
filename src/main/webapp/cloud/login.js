setTimeout( function() {
    if(window.parent && window.parent != window.self) {
        window.parent.location.href = "login.html";
    }
}, 300 );

URL_CHECK_USER = "/tss/getLoginInfo.in";
URL_LOGIN = "/tss/auth/login.do";

$(function() {     
     init();
});

function init() {
    setSysTitle(); 

    var accountEl = $1("loginName"), passwdEl  = $1("password");
    accountEl.onfocus = function() {  passwdEl.disabled = true; }
    accountEl.value = $.Cookie.getValue("iUserName") || "";
    accountEl.focus();

    $("#bt_login").click ( function() {
        doLogin(accountEl, passwdEl);
    } );

    $.Event.addEvent(document, "keydown", function(ev) {
        if(13 == ev.keyCode) { // enter
            $.Event.cancel(ev);
            $("#bt_login").focus();

            setTimeout(function() {
                doLogin(accountEl, passwdEl);
            }, 10);
        }
    });

    accountEl.onblur = function() { 
        var value = this.value;
        if( !value )  return $(accountEl).focus();
        
        $.ajax({
            url: URL_CHECK_USER,
            params: {"loginName": value},
            waiting: true,
            onexcption: function() {
                accountEl.focus();
            },
            onresult: function(){
                accountEl.identifier = this.getNodeValue("identifier");
                accountEl.randomKey  = this.getNodeValue("randomKey");
                
                passwdEl.disabled = false;
                passwdEl.focus();
            }
        });
    }
}

var doLogin = function(accountEl, passwdEl) {
    var identifier = accountEl.identifier;
    var randomKey  = accountEl.randomKey;   
    var loginName  = accountEl.value;
    var password   = passwdEl.value;
    
    if( !loginName ) {
        return $(accountEl).focus();
    } 
    else if( !password ) {
        return $(passwdEl).focus();
    }
    else if( !identifier ) {
        return $.alert("系统无法登录，服务器异常，请联系管理员。");
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

var indexPage = "tssbi.html";    /* 登录成功后跳转到的页面 */

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
