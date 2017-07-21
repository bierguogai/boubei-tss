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
    accountEl.value = $.Cookie.getValue("iUser") || "";
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
        return $.alert("系统无法登录，服务器异常，请刷新或稍后再尝试。");
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
            $.Cookie.setValue("iUser", loginName);
            gotoIndex(loginName);   
        }
    });
}

var indexPage = "tssbi.html";    /* 登录成功后跳转到的页面 */

function gotoIndex(loginName) {
    if(window.parent && window.parent != window.self) {
        window.parent.location.href = indexPage;
    } else {
        window.location.href = indexPage;
    }
}
