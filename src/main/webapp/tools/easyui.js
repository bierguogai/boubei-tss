TOMCAT_URL = "/tss";

if(location.protocol === 'file:') {
    TOMCAT_URL = 'http://localhost:9000/tss';
}

BAR_CODE_URL   = TOMCAT_URL + "/imgcode/bar/";

BASE_JSON_URL  = TOMCAT_URL + '/data/json/';
BASE_JSONP_URL = TOMCAT_URL + '/data/jsonp/';
function json_url(id, appCode)  { return BASE_JSON_URL  + id + (appCode ? "?appCode="+appCode : ""); }
function jsonp_url(id, appCode) { return BASE_JSONP_URL + id + (appCode ? "?appCode="+appCode : ""); }

BASE_RECORD_URL= TOMCAT_URL + '/auth/xdata/';
function record_urls(recordTableId) {   // 一个录入表所拥有的增、删、改、查等接口
    var result = {};
    result.CREATE = BASE_RECORD_URL + recordTableId;
    result.UPDATE = BASE_RECORD_URL + recordTableId+ '/';  //{id}
    result.DELETE = BASE_RECORD_URL + recordTableId+ '/';  //{id}
    result.GET    = BASE_RECORD_URL + recordTableId+ '/';  //{id}
    result.QUERY  = BASE_RECORD_URL + 'json/' +recordTableId;
    result.CUD    = BASE_RECORD_URL + 'cud/'  +recordTableId;
    result.ATTACH = BASE_RECORD_URL + 'attach/json/' +recordTableId+ '/';

    return result; 
}

// 用户权限信息
var userCode, 
    userName, 
    userGroups = [], 
    userRoles = [], 
    userHas;

tssJS.getJSON("/tss/auth/user/has", {}, function(result) {
        userGroups = result[0];
        userRoles  = result[1];
        userCode   = result[3];
        userName   = result[4];
        userHas    = result;
    }, "GET");

function initCombobox(id, code, params, init) {
    var url = '/tss/param/json/combo/' + code;
    $.get(url, params, function(data){
        var _data = [];
        $.each(data, function(i, item){
            _data.push({'id': item[0], 'text': item[1]});
        });

        $('#' + id).combobox( {
            panelHeight: '120px',
            width: '130px',
            valueField: 'id',
            textField: 'text',
            editable: false,
            data: _data
        });

        if(data[init]) {
            $('#' + id).combobox('setValue', data[init][0]);
        }
    });
}

function save(recordId) {
    var id = $("input[name='id']").val();
    var isCreate = !id;

    var $saveBtn = $('#dlg-buttons>a[onclick^="save"]');
    $saveBtn.linkbutton("disable");
    $('#fm').form('submit',{
        url: BASE_RECORD_URL + recordId + (!isCreate ? "/"+id : ''),
        onSubmit: function(){
            var flag = $(this).form('validate');
            if( !flag ) {
                $saveBtn.linkbutton("enable");
            }
            return flag;
        },
        success: function(result){
            $saveBtn.linkbutton("enable");
            checkException(result, function() {
                closeDialog();
                $('#t1').datagrid('reload'); // reload the grid data
            });
        }
    });
}

function checkException(result, callback) {
    result = result ? eval('(' + result + ')') : "";
    if (result.errorMsg){
        $.messager.show({
            title: '异常信息提示',
            msg: result.errorMsg
        });
    } 
    else {
        callback();
    }
}

function openDialog(title, clear) {
    $('#dlg').dialog( {"modal": true} ).dialog('open').dialog('setTitle', title).dialog('center');

    clear && $('#fm').form('clear');
}

function closeDialog() {
    $('#dlg').dialog('close'); // close the dialog
    $('#fm').form('clear');
}

function getSelectedRow(tblID) {
    tblID = tblID || 't1';
    var row = $('#' + tblID).datagrid('getSelected');
    if (!row) {
        $.messager.alert({
            title: '提示',
            msg: '您没有选中任何行，请先点击选中需要操作的行。'
        });
    }
    return row;
}

function doRemove(elID, recordID, index){
    elID = elID || "t1";
    var row = getSelectedRow(elID);
    row && $.messager.confirm('Confirm', '删除该行以后将无法再恢复，您确定要删除这行数据吗? ', function(result){
        result && tssJS.ajax({
             url: BASE_RECORD_URL + recordID +"/"+ row.id,
             method: 'DELETE',
             ondata: function(result) {
                checkException(result, function() {
                    if( index >= 0 ) {
                        $('#' + elID).datagrid('deleteRow', index);
                    } else {
                        $('#' + elID).datagrid('reload'); // reload the grid data
                    }
                });

                $.messager.show({
                    title: '提示',
                    msg: '删除成功！'
                });
             }
        });       
    });
}

function getAttachs(tableId, itemId, callback) {
    tssJS.ajax({ 
        url: BASE_RECORD_URL + "attach/json/" + tableId + "/" + itemId, 
        method: "GET", 
        ondata: function(){
            var data  = this.getResponseJSON();
            data && data.each(function(i, item) {
                callback(item);
            });
        } 
    });
}

function clone(from, to){   
   for(var key in from){    
      to[key] = from[key];   
   }   
}  

function _export(recordId, _params) {
   if( _params ) {
        _params.page = 1;
        _params.pagesize = 10*0000;
    } else {
        _params = {};
    }

    var queryString = "?";
    $.each(_params, function(key, value) {
        if( queryString.length > 1 ) {
            queryString += "&";
        }
        queryString += (key + "=" + value);
    });

    var url = encodeURI("/tss/auth/xdata/export/" + recordId + queryString);
    tssJS("#" + createExportFrame()).attr( "src", url);
}

 /* 创建导出用iframe */
function createExportFrame() {
    var frameName = "exportFrame";
    if( $1(frameName) == null ) {
        var exportDiv = tssJS.createElement("div"); 
        tssJS(exportDiv).hide().html("<iframe id='" + frameName + "' style='display:none'></iframe>");
        document.body.appendChild(exportDiv);
    }
    return frameName;
}

function batchImport(recordId) {
    function checkFileWrong(subfix) {
        return subfix != ".csv";
    }

    var url = "/tss/auth/file/upload?afterUploadClass=com.boubei.tss.dm.record.file.ImportCSV";
    url += "&recordId=" + recordId;
    var importDiv = createImportDiv("请点击图标选择CSV文件导入", checkFileWrong, url);
    tssJS(importDiv).show();
}

/* 创建导入Div */
function createImportDiv(remark, checkFileWrong, importUrl) {
    var importDiv = $1("importDiv");
    if( importDiv == null ) {
        importDiv = tssJS.createElement("div", null, "importDiv");    
        document.body.appendChild(importDiv);

        var str = [];
        str[str.length] = "<form id='importForm' method='post' target='fileUpload' enctype='multipart/form-data'>";
        str[str.length] = "  <div class='fileUpload'> <input type='file' name='file' id='sourceFile' onchange=\"$('#importDiv h2').html(this.value)\" /> </div> ";
        str[str.length] = "  <input type='button' id='importBt' value='确定导入' class='tssbutton blue'/> ";
        str[str.length] = "</form>";
        str[str.length] = "<iframe style='width:0; height:0;' name='fileUpload'></iframe>";

        tssJS(importDiv).panel(remark, str.join("\r\n"), false);
        tssJS(importDiv).css("height", "300px").center();
    } else {
        tssJS("#sourceFile").value("");
        tssJS('#importDiv h2').text(" - " + remark);
    }

    // 每次 importUrl 可能不一样，比如导入门户组件时。不能缓存
    tssJS("#importBt").click( function() {
        var fileValue = $1("sourceFile").value;
        if( !fileValue ) {
             return tssJS("#importDiv h2").notice("请选择导入文件!");               
        }

        var length = fileValue.length;
        var subfix = fileValue.substring(length - 4, length);
        if( checkFileWrong && checkFileWrong(subfix) ) {
           return tssJS("#importDiv h2").notice(remark);
        }

        var form = $1("importForm");
        form.action = importUrl;
        form.submit();

        tssJS(importDiv).hide();
    } );

    return importDiv;
}
