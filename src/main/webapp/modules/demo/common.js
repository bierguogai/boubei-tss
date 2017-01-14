
function initCombobox(id, code, params, init) {
    var url = '/tss/param/json/combo/' + code;
    $.get(url, params, function(data){
        var _data = [];
        $.each(data, function(i, item){
            _data.push({'id': item[0], 'text': item[1]});
        });

        $('#' + id).combobox( {
                    panelHeight: '120px',
                    valueField: 'id',
                    textField: 'text',
                    data: _data
                });

        if(data[init]) {
            $('#' + id).combobox('setValue', data[init][0]);
        }
    });
}

function save(){
    $('#fm').form('submit',{
        url: SAVE_URL,
        onSubmit: function(){
            return $(this).form('validate');
        },
        success: function(result){
            checkException(result, function() {
                closeDialog();
                $('#t1').datagrid('reload'); // reload the grid data
            });
        }
    });
}

function checkException(result, callback) {
    result = result || {};
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

function _remove(){
    var row = getSelectedRow();
    row && $.messager.confirm('Confirm', '您确定要删除这行数据吗?', function(result){
        result && $.ajax({
             url: DELTE_URL + row.id,
             type: 'DELETE',
             success: function(result) {
                checkException(result, function() {
                    $('#t1').datagrid('reload'); // reload the grid data
                });
             }
        });       
    });
}

var cmenu;
function createColumnMenu(){
    cmenu = $('<div/>').appendTo('body');
    cmenu.menu({
        onClick: function(item){
            if (item.iconCls == 'icon-ok'){
                $('#t1').datagrid('hideColumn', item.name);
                cmenu.menu('setIcon', {
                    target: item.target,
                    iconCls: 'icon-empty'
                });
            } else {
                $('#t1').datagrid('showColumn', item.name);
                cmenu.menu('setIcon', {
                    target: item.target,
                    iconCls: 'icon-ok'
                });
            }
        }
    });
    var fields = $('#t1').datagrid('getColumnFields');
    for(var i=0; i<fields.length; i++){
        var field = fields[i];
        var col = $('#t1').datagrid('getColumnOption', field);
        cmenu.menu('appendItem', {
            text: col.title,
            name: field,
            iconCls: 'icon-ok'
        });
    }
}

function _export() {

}