/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function isNumberKeyDot(evt) {
    var charCode = (evt.which) ? evt.which : event.keyCode
    //if (charCode > 31 && (charCode < 48 || charCode > 57)) /// without dot
    if (charCode > 31 && (charCode < 45 || charCode > 57)) {
        alert("Letter is not allowed!!!!");
        return false;
    } else {
        return true;
    }
}

function modalClick() { // Click to only happen on announce links
    $('#myModal').modal({backdrop: 'static', keyboard: false});
    $('#myModal').modal('show');
}

function dowloadReport(type) {
    // var data = dt.row($(btn).parents('tr')).data();
    // var id = data[0];
    var api;
    var id;
    if (type === "totalSale") {
        id = 0;
        api = "/admin/download?branch_id=" + id + "&report_type=" + type + "&customer_id=" + 0;
    }

    if (type === "stockInfo" || type === "saleByBranch") {
        id = $('#branchId').val();
        if (id === "0" && type === "stockInfo") {
            type = "stockInfoAll";
        }
        if (id === "0" && type === "saleByBranch") {
            type = "branchInfoAll";
        }
        api = "/admin/download?branch_id=" + id + "&report_type=" + type + "&customer_id=" + 0;
    }
    if (type === "saleByItem") {
        id = $('#item').val();
        api = "/admin/download?branch_id=" + id + "&report_type=" + type + "&customer_id=" + 0;
    }
    if (type === "saleByBranchWiseCustomer") {
        id = $('#branchId').val();
        var customer = $('#customer').val();
        if ((id === null || id === "" || id === 0) || (customer === null || customer === "" || customer === 0)) {
            alert("Select Branch and Customer");
            return;
        }
        api = "/admin/download?branch_id=" + id + "&report_type=" + type + "&customer_id=" + customer;
    }
    if (type === "saleByBranchWiseItem") {
        id = $('#branchId').val();
        var customer = $('#item').val();
        if ((id === null || id === "" || id === 0) || (customer === null || customer === "" || customer === 0)) {
            alert("Select Branch and Item");
            return;
        }
        api = "/admin/download?branch_id=" + id + "&report_type=" + type + "&customer_id=" + customer;
    }

    if (type === "saleByBranchWiseSalesPerson") {
        id = $('#branchId').val();
        var customer = $('#salesperson').val();
        if ((id === null || id === "" || id === 0) || (customer === null || customer === "" || customer === 0)) {
            alert("Select Branch and Salesperson");
            return;
        }
        api = "/admin/download?branch_id=" + id + "&report_type=" + type + "&customer_id=" + customer;
    }


    var link = document.createElement('a');
    link.href = api;
    link.download = "Hello";
    link.click();

}

function saveTiles() {
    var user = $('#userId').val();
    var tilesList = $("#tilesId :selected").map(function (i, el) {
        return $(el).text();
    }).get();
    var apiString = "&tiles=";
    var radioButtonValue = 0;
    for (var i = 0; i < tilesList.length; i++) {
        apiString = apiString.concat(tilesList[i]);
        if (i === (tilesList.length) - 1) {
            break;
        }
        apiString = apiString.concat("&tiles=");
    }
    if ($('#giveRadio').is(':checked')) {
        radioButtonValue = $('#giveRadio').val();
    }
    if ($('#revokeRadio').is(':checked')) {
        radioButtonValue = $('#revokeRadio').val();
    }
    var api = "/admin/addtiles?userId=" + user + apiString + "&isRevoke=" + radioButtonValue;
    $.ajax({
        type: "POST",
        url: api,
        contentType: 'application/json',
        error: function (res) {
            alert(JSON.stringify(res));
        },
        success: function (res) {
            if (res === "savedSuccessfully") {
                alert("Tiles Permission given");
            } else {
                alert("Tiles Permission Failed");
            }

        }

    });
}


String.prototype.compose = (function () {
    var re = /\{{(.+?)\}}/g;
    return function (o) {
        return this.replace(re, function (_, k) {
            return typeof o[k] != 'undefined' ? o[k] : '';
        });
    }
}());

function getCustomerByBranch(id, branchId) {
    $.ajax({
        type: "GET",
        url: "/admin/getCustomerByBranch?branchId=" + $('#' + branchId).val(),
        success: function (response) {
            //response = JSON.parse(response);
            $('#' + id).empty();
            if (response != null) {
                var selectOption = new Option("--Select Customer--", "0", false, false);
                $('#' + id).append(selectOption);
                var len = response.length;
                for (var i = 0; i < len; i++) {
                    var options = new Option(response[i].customerName, response[i].id, false, false);
                    $('#' + id).append(options);
                }
            }
        }
    });
}

function getItemByBranch(id, branchId) {
    $.ajax({
        type: "GET",
        url: "/admin/getItemByBranch?branchId=" + $('#' + branchId).val(),
        success: function (response) {
            //response = JSON.parse(response);
            $('#' + id).empty();
            if (response != null) {
                var selectOption = new Option("--Select Item--", "0", false, false);
                $('#' + id).append(selectOption);
                var len = response.length;
                for (var i = 0; i < len; i++) {
                    var options = new Option(response[i].itemName, response[i].itemId, false, false);
                    $('#' + id).append(options);
                }
            }
        }
    });
}

function getSalespersonByBranch(id, branchId) {
    $.ajax({
        type: "GET",
        url: "/admin/getSalespersonByBranch?branchId=" + $('#' + branchId).val(),
        success: function (response) {
            //response = JSON.parse(response);
            $('#' + id).empty();
            if (response != null) {
                var selectOption = new Option("--Select Salesperson--", "0", false, false);
                $('#' + id).append(selectOption);
                var len = response.length;
                for (var i = 0; i < len; i++) {
                    var options = new Option(response[i].salespersonName, response[i].id, false, false);
                    $('#' + id).append(options);
                }
            }
        }
    });
}

function getRelatedBranch() {
    var role = $('#roleId').val();
    if (role !== "0") {
        $.ajax({
            type: "GET",
            url: "/admin/branchList?roleId=" + role,
            success: function (response) {
                //response = JSON.parse(response);
                $('#branchId').empty();
                if (response != null) {
                    var selectOption = new Option("--Select Branch--", "0", false, false);
                    $('#branchId').append(selectOption);
                    var len = response.length;
                    for (var i = 0; i < len; i++) {
                        var options = new Option(response[i].name, response[i].id, false, false);
                        $('#branchId').append(options);
                    }
                }
            }
        });
    }

}

function changeRadioButtonSelection(idValue) {
    if (idValue === "giveRadio") {
        $("#revokeRadio").prop("checked", false);
    }
    if (idValue === "revokeRadio") {
        $("#giveRadio").prop("checked", false);
    }

}

function changeStatus() {
    var user = $('#userId').val();
    var status = $('#status').val();
    var api = "/admin/changeStatus?userId=" + user + "&status=" + status;
    $.ajax({
        type: "PUT",
        url: api,
        contentType: 'application/json',
        error: function (res) {
            alert(JSON.stringify(res));
        },
        success: function (res) {
            if (res === "successfullySaved") {
                alert("Access Control Changed Successfully");
            } else {
                alert("Access Control Changed Failed");
            }

        }

    });
}

function getAdminCustomerList() {
    if ($('#branchId').val() !== null && $('#branchId').val() !== "" && $('#branchId').val() !== "0") {
        var api = "/admin/allcustomerbranch/" + $('#branchId').val();
        $.ajax({
            type: "GET",
            url: api,
            contentType: 'application/json',
            error: function (res) {
                alert(JSON.stringify(res));
            },
            success: function (response) {

                $('#myTable_wrapper').attr("hidden", "hidden");
                var tableHead = "<thead id='theadUser'></thead>" +
                    "<tbody id='tbodyUser'></tbody>"
                $('#myTable').html(tableHead);
                if (response != null && response.length > 0) {
                    $('#myTable_wrapper').removeAttr("hidden", "hidden");
                    destroyTable('myTable');
                    var headArr = ['#', 'Customer Name', 'Mobile No.', 'Address', 'Action'];
                    createHeader('theadUser', headArr);
                    prepareDataGridCustomer(response, "tbodyUser");
                }
            }

        });
    }
}

function getAdminSalespersonList() {
    if ($('#branchId').val() !== null && $('#branchId').val() !== "" && $('#branchId').val() !== "0") {
        var api = "/admin/allsalespersonbranch/" + $('#branchId').val();
        $.ajax({
            type: "GET",
            url: api,
            contentType: 'application/json',
            error: function (res) {
                alert(JSON.stringify(res));
            },
            success: function (response) {

                $('#myTable_wrapper').attr("hidden", "hidden");
                var tableHead = "<thead id='theadUser'></thead>" +
                    "<tbody id='tbodyUser'></tbody>"
                $('#myTable').html(tableHead);
                if (response != null && response.length > 0) {
                    $('#myTable_wrapper').removeAttr("hidden", "hidden");
                    destroyTable('myTable');
                    var headArr = ['#', 'Salesperson Name', 'Mobile No.', 'Action'];
                    createHeader('theadUser', headArr);
                    prepareDataGridSalesperson(response, "tbodyUser");
                }
            }

        });
    }
}

function getAdminItemList() {
    if ($('#branchId').val() !== null && $('#branchId').val() !== "" && $('#branchId').val() !== "0") {
        var api = "/admin/allitembybranch/" + $('#branchId').val();
        $.ajax({
            type: "GET",
            url: api,
            contentType: 'application/json',
            error: function (res) {
                alert(JSON.stringify(res));
            },
            success: function (response) {

                $('#myTable_wrapper').attr("hidden", "hidden");
                var tableHead = "<thead id='theadUser'></thead>" +
                    "<tbody id='tbodyUser'></tbody>"
                $('#myTable').html(tableHead);
                if (response != null && response.length > 0) {
                    $('#myTable_wrapper').removeAttr("hidden", "hidden");
                    destroyTable('myTable');
                    var headArr = ['#', 'Item Name', 'Unit Price', 'Quantity', 'Action'];
                    createHeader('theadUser', headArr);
                    prepareDataGrid(response, "tbodyUser");
                }
            }

        });
    }
}

function prepareDataGrid(response, tbodyUser) {
    var count = 0;
    for (var i = 0; i < response.length; i++) {
        count++;
        var trTableBody = $('<tr class="text-center"></tr>').appendTo($('#' + tbodyUser));
        $('<td>' + count + '</td>').appendTo(trTableBody);
        $('<td>' + checkValue(response[i].itemName) + '</td>').appendTo(trTableBody);
        $('<td>' + checkValue(response[i].itemUnitPrice) + '</td>').appendTo(trTableBody);
        $('<td>' + checkValue(response[i].itemAmount) + '</td>').appendTo(trTableBody);
        $('<td><a href="/admin/item/edit/id/' + checkValue(response[i].itemId) + '" class="btn btn-success">Edit</a>&nbsp;<a href="/admin/item/delete/id/' + checkValue(response[i].itemId) + '" onclick="return confirm(\'Are you sure to delete this Item?\')" class="btn btn-danger">Delete</a></td>').appendTo(trTableBody);
    }
}

function prepareDataGridSalesperson(response, tbodyUser) {
    var count = 0;
    for (var i = 0; i < response.length; i++) {
        count++;
        var trTableBody = $('<tr class="text-center"></tr>').appendTo($('#' + tbodyUser));
        $('<td>' + count + '</td>').appendTo(trTableBody);
        $('<td>' + checkValue(response[i].salespersonName) + '</td>').appendTo(trTableBody);
        $('<td>' + checkValue(response[i].mobileNo) + '</td>').appendTo(trTableBody);
        // $('<td>' + checkValue(response[i].itemAmount) + '</td>').appendTo(trTableBody);
        $('<td><a href="/admin/salesperson/edit/id/' + checkValue(response[i].id) + '" class="btn btn-success">Edit</a>&nbsp;<a href="/admin/salesperson/delete/id/' + checkValue(response[i].id) + '" onclick="return confirm(\'Are you sure to delete this Item?\')" class="btn btn-danger">Delete</a></td>').appendTo(trTableBody);
    }
}


function prepareDataGridCustomer(response, tbodyUser) {
    var count = 0;
    for (var i = 0; i < response.length; i++) {
        count++;
        var trTableBody = $('<tr class="text-center"></tr>').appendTo($('#' + tbodyUser));
        $('<td>' + count + '</td>').appendTo(trTableBody);
        $('<td>' + checkValue(response[i].customerName) + '</td>').appendTo(trTableBody);
        $('<td>' + checkValue(response[i].mobileNo) + '</td>').appendTo(trTableBody);
        $('<td>' + checkValue(response[i].address) + '</td>').appendTo(trTableBody);
        $('<td><a href="/admin/customer/edit/id/' + checkValue(response[i].id) + '" class="btn btn-success">Edit</a>&nbsp;<a href="/admin/customer/delete/id/' + checkValue(response[i].id) + '" onclick="return confirm(\'Are you sure to delete this Item?\')" class="btn btn-danger">Delete</a></td>').appendTo(trTableBody);
    }
}


function checkValue(val) {
    if (val === '' || val === undefined || val === 'undefined' || val === null || val === 'null') {
        val = '-';
    }
    return val;
}

function destroyTable(tableId) {
    if ($.fn.DataTable.isDataTable('#' + tableId)) {
        $('#' + tableId).DataTable().destroy();
    }
}

function createHeader(headID, headArr) {
    $('#' + headID).empty();
    var trHead = $('<tr class="text-center"></tr>').appendTo($('#' + headID));

    for (var i = 0; i < headArr.length; i++) {
        $('<td><b><span class="trn">' + headArr[i] + '</span></b></td>').appendTo(trHead);
    }
}

$(document).ready(function () {

    var a = localStorage.getItem("id");
    const
        string = a,
        array = string.slice(1, -1).split(',');
    var api = "/admin/get_all_tiles";
    var newArr = [];
    $.ajax({
        type: "GET",
        url: api,
        contentType: 'application/json',
        error: function (res) {
            alert(JSON.stringify(res));
        },
        success: function (res) {
            for (var j = 0; j < res.length; j++) {

                newArr.push(res[j].tileId);

            }
            let difference = newArr.filter(x = > !array.includes(x)
        )
            ;
            if (difference.length !== 0) {
                for (var i = 0; i < difference.length; i++) {
                    $('#' + difference[i]).hide();
                }
            }

            for (var i = 0; i < array.length; i++) {
                $('#' + array[i].trim()).show();
            }

        }


    })
});

function getAccessibleTiles(optionValue) {
    var rowCount = myTable2.rows.length;
    for (var i = rowCount - 1; i > 0; i--) {
        myTable2.deleteRow(i);
    }
    var tbody = $('#myTable2').children('tbody');
    var table = tbody.length ? tbody : $('#myTable');
    var row = '<tr >' +
        '<td>{{serial}}</td>' +
        '<td >{{tilesName}}</td>' +
        '</tr>';

    if (optionValue !== 0) {
        var api = "/admin/getPermittedTiles?userId=" + optionValue;
        var count = 0;
        $.ajax({
            type: "POST",
            url: api,
            contentType: 'application/json',
            error: function (res) {
                alert(JSON.stringify(res));
            },
            success: function (res) {
                for (var i = 0; i < res.length; i++) {
                    count++;
                    table.append(row.compose({
                        'serial': count,
                        'tilesName': res[i].tilesName,
                    }));
                }
            }

        })
    }
}