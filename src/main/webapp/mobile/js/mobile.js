if( location.hostname == 'www.boubei.com' ) {
    tssJS && tssJS(function() {
        var scriptNode = document.createElement("script");
        scriptNode.src = "http://s11.cnzz.com/z_stat.php?id=1256153120&web_id=1256153120";
        scriptNode.async = false;
        tssJS('head').appendChild(scriptNode);
    });
}

BASE_URL = 'http://www.boubei.com/tss/'

BASE_JSON_URL  = BASE_URL + '/data/json/';
BASE_JSONP_URL = BASE_URL + '/data/jsonp/';

function json_url(id, appCode)  { return BASE_JSON_URL  + id + (appCode ? "?appCode="+appCode : ""); }
function jsonp_url(id, appCode) { return BASE_JSONP_URL + id + (appCode ? "?appCode="+appCode : ""); }


function checkLoign() {
	var iUser = $.Cookie.getValue("iUser");
	if( !iUser || iUser == 'null'  ) {		
		window.location.href = "login.html";
	}
	else {
		$("#userInfo").text( "【" + iUser + "】");
	}
}

function genLabels(x) {
	var labels = [];
	for(var i = x; i > 0; i--) {
		labels.push( _subDateS(new Date(), i) );
	}

	return labels;
}

function _subDateS(day, x) {
    return new Date(day.getTime() - x*1000*60*60*24).format("MM-dd");
}

// 格式化金额数据
function fmoney(s, n) {  
    n = n > 0 && n <= 20 ? n : 2;  
    s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";  
    var l = s.split(".")[0].split("").reverse(), r = s.split(".")[1];  
    t = "";  
    for (i = 0; i < l.length; i++) {  
        t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");  
    }  
    return t.split("").reverse().join("") + "." + r;  
}  
