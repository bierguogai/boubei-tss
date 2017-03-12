tssJS && tssJS(function() {
    var scriptNode = tssJS.createElement("script");
    scriptNode.src = "http://s95.cnzz.com/z_stat.php?id=1259158241&web_id=1259158241";
    scriptNode.async = false;
    tssJS('head').appendChild(scriptNode);
});

// BASE_DATA_URL = 'http://btrbi.800best.com/tss/data/';
// BASE_DATA_URL = 'http://10.9.44.68:8080/tss/data/'; 
BASE_DATA_URL = 'http://polaris.800best.com/tss/data/'

var
	IS_TEST = (location.protocol === 'file:'),
	API_IDS = [2099, 2101, 2103, 2102, 2100, 2104, 2106, 2107, 2105],
    MENU_ROOT = 77;

if(IS_TEST) {
	// API_IDS = [2088, 1937, 2096, 1938, 2083, 2089, 2107, 2112, 2098];
    // MENU_ROOT = 76;
    BASE_DATA_URL = 'http://btrbi.800best.com/tss/data/';
}

BASE_DATA_JSON  = BASE_DATA_URL + 'json/';
BASE_DATA_JSONP = BASE_DATA_URL + 'jsonp/';


function checkLoign() {
	if(IS_TEST) return;

	var iUser = $.Cookie.getValue("iUser");
	if( !iUser || iUser == 'null'  ) {		
		window.location.href = "login.html";
	}
	else {
		$("#userInfo").text( "【" + iUser + "】");
	}
}

function toDate(dataStr) {
    if(!dataStr) return dataStr;

    var d = new Date(dataStr.replace(/-/g, "/"));
    return d;
}
function subDate(day, x) {
    return new Date(day.getTime() - x*1000*60*60*24);
}
function subDateS(day, x) {
    return new Date(day.getTime() - x*1000*60*60*24).format("MM-dd");
}
function getFirstMDay(day) {
    return day.format("yyyy-MM") + "-01";
}
function getLastMDay(day) {
    return getLastDateOfMonth(day).format("yyyy-MM-dd");
}
function getLastMDate(day) {
    var year = day.getFullYear();
    var month = (day.getMonth() + 1);
    if(month > 12) {
        month = 1;
        year ++;
    }
    if(month < 10) {
        month = '0' + month;
    }
    var tempDay = new Date(year, month, 1);
    var lastDay = (new Date(tempDay.getTime() - 1000*60*60*24));

    return lastDay.getDate();
}

function genLabels(x) {
	var labels = [];
	for(var i = x; i > 0; i--) {
		labels.push( subDateS(new Date(), i) );
	}

	return labels;
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
