function getAllBills(val, elementId) {
    var count = 0;
    var api;
    if (val != null && elementId != null) {
        if (elementId === "searchValue") {
            api = "/branch/bill/allNew??search=" + val;
            count = 0;
        }

        if (elementId === "pagination") {
            api = "/branch/bill/allNew?page=" + val;
            count = val * 20
        }
    }
    if (val == null && elementId == null) {
        if (localStorage.getItem("customerId") !== null) {
            api = "/branch/bill/allNewByCustomer?customerId=" + localStorage.getItem("customerId");
        } else {
            api = "/branch/bill/allNew";
        }

    }


    var truckChalan = '<a class=\"btn btn-success\" href=\"/branch/downloadBillChallan?bill_id={{billId}}"  "\" target=\"_blank\">Details</a><a class=\"btn btn-danger\" onclick=\"return confirm(\'Are you sure you want to delete this transaction?\')\" href=\"/branch/bill/delete/id/{{billId}}"  "\">Remove</a>';

    var rowCount = myTable2.rows.length;
    for (var i = rowCount - 1; i > 0; i--) {
        myTable2.deleteRow(i);
    }
    var tbody = $('#myTable2').children('tbody');
    var table = tbody.length ? tbody : $('#myTable2');
    var row = '<tr >' +
        '<td>{{serial}}</td>' +
        '<td>{{billNo}}</td>' +
        '<td >{{billingDate}}</td>' +
        '<td >{{customerName}}</td>' +
        '<td >{{totalPrice}}</td>' +
        '<td >{{previousDue}}</td>' +
        '<td >{{payableAmount}}</td>' +
        '<td >{{paidAmount}}</td>' +
        '<td >{{newDue}}</td>' +
        '<td>' + truckChalan + '</td>' +
        '</tr>';
    $.ajax({
        type: "GET",
        url: api,
        contentType: 'application/json',
        error: function (res) {
            alert(JSON.stringify(res));
        },
        success: function (res) {
            if (res.statusCode === 200) {
                var totalData = res.totalCount;
                var totalFractionalPage = totalData / 20;
                var totalPage = Math.ceil(totalFractionalPage);

                for (var i = 0; i < res.content.length; i++) {
                    count++;
                    table.append(row.compose({
                        'serial': count,
                        'billNo': res.content[i].billNo,
                        'billingDate': res.content[i].billingDate,
                        'customerName': res.content[i].customerName,
                        'totalPrice': res.content[i].totalAmount,
                        'previousDue': res.content[i].previousDueAmount,
                        'payableAmount': res.content[i].totalPayableAmount,
                        'paidAmount': res.content[i].paidAmount,
                        'newDue': res.content[i].newDueAmount,
                        'billId': res.content[i].billId
                    }));
                    $('#customerName').text(res.content[i].customerName);
                }

                if (val == null && elementId == null) {
                    $('#myTable2').after('<div id="nav" class="pagination"></div>');
                    for (i = 0; i < totalPage; i++) {
                        var pageNum = i + 1;
                        $('#nav').append('<button type="button" class="btn btn-success save" id="pagination" value="' + i + '" onclick="getAllSales(this.value,this.id)">' + pageNum + '</button> ');
                    }
                    // $('#nav').append('<a href="#">&raquo;</a>');
                }
            }

        }

    })

}

$(document).ready(function () {
    getAllBills(null, null);
    localStorage.clear();
})

String.prototype.compose = (function () {
    var re = /\{{(.+?)\}}/g;
    return function (o) {
        return this.replace(re, function (_, k) {
            return typeof o[k] != 'undefined' ? o[k] : '';
        });
    }
}());