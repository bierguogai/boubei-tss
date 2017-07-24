TOMCAT_URL = "";
// TOMCAT_URL = "http://10.9.45.69:8080";

BASE_JSON_URL  = TOMCAT_URL + '/tss/data/json/';
BASE_JSONP_URL = TOMCAT_URL + '/tss/data/jsonp/';

function json_url(id) { return BASE_JSON_URL + id; }
function jsonp_url(id) { return BASE_JSONP_URL + id; }

var user_info = {}, preMonth, fromTime, toTime, orgs;
var carnumMap;
function initCarsInfo(callback) {
    tssJS.getJSON( json_url(27), {"uCache": "true"}, function(data) {
        carnumMap = {};
        data.each(function(i, item) {
            delete item.name;
            carnumMap[item.carnum] = item;
        });

        callback && callback();
    });
}

function appendCarInfo(data, callback) {
    function f() {
        data.each(function(i, item){
            clone(carnumMap[item.carnum], item);
        });
        callback();
    }

    if( carnumMap ) {
        f();
    } else {
        initCarsInfo( f );
    }
}

function clone(from, to){   
   for(var key in from){    
      to[key] = from[key];   
   }   
}  

function showCar(carnum) {
    if(window.parent.addTab) {
        window.parent.addTab("车辆【"+carnum+"】信息", "/tss/cloud/carbi/_car.html?carId=" + encodeURIComponent(carnum));
    }
    else if(window.parent.parent.addTab) { // 在report_portlet.html打开，则框架页在网上第三级
        window.parent.parent.addTab("车辆【"+carnum+"】信息", "/tss/cloud/carbi/_car.html?carId=" + encodeURIComponent(carnum));
    }
}

function showXianlu(xianlu) {
    if(window.parent.addTab) {
        window.parent.addTab("线路【"+xianlu+"】信息", "/tss/cloud/carbi/_xianlu.html?xianlu=" + encodeURIComponent(xianlu));
    }
    else if(window.parent.parent.addTab) { 
        window.parent.parent.addTab("线路【"+xianlu+"】信息", "/tss/cloud/carbi/_xianlu.html?xianlu=" + encodeURIComponent(xianlu));
    }
}

function getParams(params) {
    var params = {};
    var keys = ["_g1", "_g2", "_g3", "_g4", "_g5", "_day1", "_day2", "_month1", "_month2", "_other1", "_other2", "_week1", "_week2", "_season1", "_season2", "_year1", "_year2"];
    keys.each(function(i, key) {        
        var value = tssJS.Cookie.getValue(key);
        if(value) {
            var paramKey = "param" + (i+1);
            params[paramKey] = value;
        }
    });

    // 页面跳转时，优先使用通过QueryString传递参数
    for(var i=1; i <= 18; i++ ) {
        var paramN = "param" + i;
        var paramX  = tssJS.Query.get(paramN); 
        if(paramX) {
            params[paramN] = paramX;
        }               
    }

    filterEmptyParams(params);
	return params;
}

/*
 * 过滤参数集，剔除掉为空的参数项。
 * PASS: 因JQuery的ajax会发送空字符串，而TSS的ajax不发送，导致数据缓存条件失效。
 */
function filterEmptyParams(params) {
    for(var key in params) {
        if(params[key] == null || params[key] == "") { // value = 0 不应该被过滤
            delete params[key];
        }
    }
}

/* 
 * 按权限过滤车辆，BI数据服务查出来的数据已经按条件过滤，直接从G7取来的数据没做任何过滤 
 */
var globalV = parent.parent.globalV;
function filterByPermission(result) {  
    if(!globalV) return result;

    var data = [], ui = globalV.userInfo, 
        roles = ui[1], orgName = ui[4], uGroups = ui[0], 
        gnames = [];
    uGroups.each(function(i, item) {
        gnames.push( item[1] );
    });

    result.each(function(i, item) {
        var car = carnumMap[item.carnum];
        if(!car) return true;
        var remark = (item.remark || "").trim();

        // 如果是承运商或总部的人登陆，车辆按承运商（按_g1的值）再次过滤（承运商登陆_g1即自己）
        var _g1_ = tssJS.Cookie.getValue("_g1");
        if(_g1_ && _g1_.indexOf(item.org) < 0 ) return true;

        // 过滤路线、品牌、车型、车牌等
        var _g2_ = tssJS.Cookie.getValue("_g2");
        if(_g2_ && !_g2_.split(",").contains(item.xianlu) ) return true;

        var _g3_ = tssJS.Cookie.getValue("_g3");
        if(_g3_ && _g3_ != item.brand ) return true;

        var _g4_ = tssJS.Cookie.getValue("_g4");
        if(_g4_ && _g4_ != item.carlen ) return true;

        var _g5_ = tssJS.Cookie.getValue("_g5");
        if(_g5_ && _g5_ != item.carnum ) return true;

        var _g7_ = tssJS.Cookie.getValue("_g7");
        if(_g7_ && _g7_ != item.jkgc ) return true;

        var _g8_ = tssJS.Cookie.getValue("_g8");
        if(_g8_ && _g8_ != item.qdlx ) return true;

        var _day1 = tssJS.Cookie.getValue("_day1");
        if( item.xxsj && toDate(item.xxsj) < toDate(_day1)) return true; // 在查询起始日期前已经下线的车辆

        // 如果是快递或快运汽运部的人登陆，则只过滤快递 或 快运的车
        if( gnames[0] == "百世快运" && remark.indexOf("快运") < 0 ) return true;
        if( gnames[0] == "百世快递" && remark.indexOf("快递") < 0 ) return true;
        

        data.push(item);
    });
    return data;
}

function stylerCursor(value, row, index) {
    return 'cursor: pointer;';
} 

// 排序数组
function sortArray(arr, field) {
    tssJS.Data.sortArray(arr, field);
}

// 按字段排序数组, 值如果一样支持名次重复
function sort(arr, field, noField) {    
    tssJS.Data.sortArray2(arr, field, noField);
}

// 纵向合并单元格
function mergeGridColCells(dg, field)  {  
    var rows = dg.datagrid('getRows' );  
    var startIndex=0;  
    var endIndex=0;  
    if( !rows.length ) return;  

    $.each(rows, function(i,row){  
        if(row[field]==rows[startIndex][field])  {  
            endIndex=i;  
        }  
        else{  
              dg.datagrid( 'mergeCells',{  
                    index: startIndex,  
                    field: field,  
                    rowspan: endIndex -startIndex+1  
              });  
              startIndex=i;  
              endIndex=i;  
        }  

    });  
    dg.datagrid( 'mergeCells',{  
        index: startIndex,  
        field: field,  
        rowspan: endIndex -startIndex+1  
    });  
}  