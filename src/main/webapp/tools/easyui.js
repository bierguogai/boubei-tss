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
    result.CREATE = BASE_RECORD_URL + '/' + recordTableId;
    result.UPDATE = BASE_RECORD_URL + '/' +recordTableId+ '/';  //{id}
    result.DELETE = BASE_RECORD_URL + '/' +recordTableId+ '/';  //{id}
    result.GET    = BASE_RECORD_URL + '/' +recordTableId+ '/';  //{id}
    result.QUERY  = BASE_RECORD_URL + '/json/' +recordTableId;
    result.CUD    = BASE_RECORD_URL + '/cud/'  +recordTableId;
    result.ATTACH = BASE_RECORD_URL + '/attach/json/' +recordTableId+ '/';

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
            return $(this).form('validate');
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

function _export() {

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
