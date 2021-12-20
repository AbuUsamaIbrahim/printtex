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

function changeViewPage() {
    var customerId = $('#customerId').val();
    localStorage.setItem("customerId", customerId);
    window.location = "by_customer_new";
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
    // var type="stockInfo";
    if (type === "stockInfo" || type === "saleByBranch") {
        id = $('#branchId').val();
    }
    if (type === "saleByItem") {
        id = $('#item').val();
        type = "saleByItemFromBranch";
    }
    api = "/branch/download?branch_id=" + id + "&report_type=" + type + "&customer_id=" + 0;

    var link = document.createElement('a');
    link.href = api;
    link.download = "Hello";
    link.click();

}