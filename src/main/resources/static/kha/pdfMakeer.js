function downloadBillReport(billId, isBranch) {
    var url = '/branch/downloadBillChallan?bill_id=' + billId;
    if (isBranch === false || isBranch === undefined) {
        url = '/admin/downloadBillChallan?bill_id=' + billId;
    }
    $.ajax({
            type: "GET",
            url: url,
            success: function (response) {
                var tableDataReport = [];
                var tableDataReport2 = [];
                var len = response.saleList.length;
                for (var i = 0; i < len; i++) {
                    var abRecord = [];
                    abRecord.push({text: i + 1, fontSize: 8});
                    abRecord.push({text: response.saleList[i].goods, fontSize: 8});
                    abRecord.push({text: response.saleList[i].quantityChallan, fontSize: 8});
                    tableDataReport.push((abRecord));
                    if (i === (len - 1)) {
                        var abRecordLast = [];
                        abRecordLast.push({text: '', fontSize: 8});
                        abRecordLast.push({text: 'Total Quantity', fontSize: 8});
                        abRecordLast.push({text: response.totalQuantity, fontSize: 8});
                        tableDataReport.push((abRecordLast));
                    }
                }

                for (var i = 0; i < len; i++) {
                    var abRecord2 = [];
                    abRecord2.push({text: i + 1, fontSize: 8});
                    abRecord2.push({text: response.saleList[i].goods, fontSize: 8});
                    abRecord2.push({text: response.saleList[i].quantityChallan, fontSize: 8});
                    tableDataReport2.push((abRecord2));
                    if (i === (len - 1)) {
                        var abRecordLast2 = [];
                        abRecordLast2.push({text: '', fontSize: 8});
                        abRecordLast2.push({text: 'Total Quantity', fontSize: 8});
                        abRecordLast2.push({text: response.totalQuantity, fontSize: 8});
                        tableDataReport2.push((abRecordLast2));
                    }
                }

                var tableDataReportBill = [];
                var tableDataReportBill2 = [];
                for (var i = 0; i < len; i++) {
                    var abRecordBill = [];
                    abRecordBill.push({text: i + 1, fontSize: 8});
                    abRecordBill.push({text: response.saleList[i].goods, fontSize: 8});
                    abRecordBill.push({text: response.saleList[i].quantity, fontSize: 8});
                    abRecordBill.push({text: response.saleList[i].unitPrice, fontSize: 8});
                    abRecordBill.push({text: response.saleList[i].totalPrice, fontSize: 8});
                    tableDataReportBill.push((abRecordBill));
                    if (i === (len - 1)) {
                        var abRecordBillSecondLast = [];
                        abRecordBillSecondLast.push({text: '', fontSize: 8});
                        abRecordBillSecondLast.push({text: 'Total Quantity', fontSize: 8});
                        abRecordBillSecondLast.push({text: response.totalQuantity, fontSize: 8});
                        abRecordBillSecondLast.push({text: 'Total in Taka', fontSize: 8});
                        abRecordBillSecondLast.push({text: response.currentBill, fontSize: 8});
                        tableDataReportBill.push((abRecordBillSecondLast));
                    }
                }
                for (var i = 0; i < len; i++) {
                    var abRecordBill2 = [];
                    abRecordBill2.push({text: i + 1, fontSize: 8});
                    abRecordBill2.push({text: response.saleList[i].goods, fontSize: 8});
                    abRecordBill2.push({text: response.saleList[i].quantity, fontSize: 8});
                    abRecordBill2.push({text: response.saleList[i].unitPrice, fontSize: 8});
                    abRecordBill2.push({text: response.saleList[i].totalPrice, fontSize: 8});
                    tableDataReportBill2.push((abRecordBill2));
                    if (i === (len - 1)) {
                        var abRecordBillSecondLast2 = [];
                        abRecordBillSecondLast2.push({text: '', fontSize: 8});
                        abRecordBillSecondLast2.push({text: 'Total Quantity', fontSize: 8});
                        abRecordBillSecondLast2.push({text: response.totalQuantity, fontSize: 8});
                        abRecordBillSecondLast2.push({text: 'Total in Taka', fontSize: 8});
                        abRecordBillSecondLast2.push({text: response.currentBill, fontSize: 8});
                        tableDataReportBill2.push((abRecordBillSecondLast2));
                    }
                }
                makePdfReportApplicantView(tableDataReport, tableDataReport2, tableDataReportBill, tableDataReportBill2, response);
            }
        }
    )
    ;

}

function makePdfReportApplicantView(tableData, tableData1, tableDataBill, tableDataBill1, response) {
//define styles
    var styles = {
        header: {
            fontSize: 18,
            bold: true,
            margin: [0, 0, 0, 10]
        },
        subheader: {
            fontSize: 16,
            bold: true,
            margin: [0, 10, 0, 5]
        },
        tableExample: {
            margin: [0, 5, 0, 15]
        },
        tableHeader: {
            bold: true,
            fontSize: 13,
            color: 'black'
        },
        quote: {
            italics: true
        },
        small: {
            fontSize: 8
        }
    }
// you will get a response from backend api.
    var data = {
        tableData1: tableData,
        tableData: tableData1,
        tableDataBill1: tableDataBill1,
        tableDataBill: tableDataBill
    };
// you will define a report structure.
    var report = {
        content: [
            {
                style: 'tableExample',
                table: {
                    body: [
                        [
                            {
                                border: [false, false, false, false],
                                image: 'data:image/jpeg;base64, ' + response.companyLogo,
                                width: 70,
                                height: 70,
                                alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                image: 'data:image/jpeg;base64, ' + response.companyLogoName,
                                width: 400,
                                height: 70,
                                alignment: 'center'
                            }
                        ],

                    ]
                }
            },
            {text: response.branchName + ' : ' + response.companyAddress, alignment: 'center'},
            {canvas: [{type: 'line', x1: 0, y1: 5, x2: 595 - 2 * 40, y2: 5, lineWidth: 3}]},
            {text: 'Challan (Office Copy)', alignment: 'center'},
            {text: '---------------------------------------', alignment: 'center'},
            {
                style: 'tableExample',
                table: {
                    widths: ['*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                border: [false, false, false, false], text: response.customerName, alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Date: ' + response.collectionDate,
                                alignment: 'right'
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false], text: response.customerAddress, alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Challan No: ' + response.billNo,
                                alignment: 'right'
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: 'Mobile No:' + response.customerPhone,
                                alignment: 'left'
                            },
                            {
                                border: [false, false, false, false], text: '', alignment: 'right'
                            }
                        ]
                    ]
                },
            },
            {
                style: 'tableExample',
                table: {
                    widths: [20, '*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                text: 'SL', alignment: 'center', fillColor: '#eeffee'
                            },
                            {
                                text: 'Description Of Goods', alignment: 'center', fillColor: '#eeeeff'
                            },
                            {
                                text: 'Quantity', alignment: 'center', fillColor: '#cccccc'
                            }
                        ]
                    ]
                },
            },
            {
                style: 'tableExample',
                table: {
                    widths: ['*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                border: [false, false, false, false],
                                text: "\n\n\n____________________________________",
                                alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: '\n\n\n_____________________________________',
                                alignment: 'right'
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false], text: "Buyer's Authorizes", alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: 'For ' + response.companyName,
                                alignment: 'right'
                            }
                        ]
                    ]
                },
            },
            {canvas: [{type: 'line', x1: 0, y1: 5, x2: 595 - 2 * 40, y2: 5, lineWidth: 3}]},
            {text: 'Head Office : ' + response.footerBranchAddress, alignment: 'center', pageBreak: 'after'},
            {
                style: 'tableExample',
                table: {
                    body: [
                        [
                            {
                                border: [false, false, false, false],
                                image: 'data:image/jpeg;base64, ' + response.companyLogo,
                                width: 70,
                                height: 70,
                                alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                image: 'data:image/jpeg;base64, ' + response.companyLogoName,
                                width: 400,
                                height: 70,
                                alignment: 'center'
                            }
                        ],

                    ]
                }
            },
            {text: response.branchName + ':' + response.companyAddress, alignment: 'center'},
            {canvas: [{type: 'line', x1: 0, y1: 5, x2: 595 - 2 * 40, y2: 5, lineWidth: 3}]},
            {text: 'Challan (Customer Copy)', alignment: 'center'},
            {text: '---------------------------------------', alignment: 'center'},
            {
                style: 'tableExample',
                table: {
                    widths: ['*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                border: [false, false, false, false], text: response.customerName, alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Date: ' + response.collectionDate,
                                alignment: 'right'
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false], text: response.customerAddress, alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Challan No: ' + response.billNo,
                                alignment: 'right'
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: 'Mobile No:' + response.customerPhone,
                                alignment: 'left'
                            },
                            {
                                border: [false, false, false, false], text: '', alignment: 'right'
                            }
                        ]
                    ]
                },
            },
            {
                style: 'tableExample',
                table: {
                    widths: [20, '*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                text: 'SL', alignment: 'center', fillColor: '#eeffee'
                            },
                            {
                                text: 'Description Of Goods', alignment: 'center', fillColor: '#eeeeff'
                            },
                            {
                                text: 'Quantity', alignment: 'center', fillColor: '#cccccc'
                            }
                        ]
                    ]
                },
            },
            {
                text: '\n\n\n',
                alignment: 'center'
            },
            {
                style: 'tableExample',
                table: {
                    widths: ['*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                border: [false, false, false, false],
                                text: "____________________________________",
                                alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: '_____________________________________',
                                alignment: 'right'
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false], text: "Buyer's Authorizes", alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: 'For ' + response.companyName,
                                alignment: 'right'
                            }
                        ]
                    ]
                },
            },
            {canvas: [{type: 'line', x1: 0, y1: 5, x2: 595 - 2 * 40, y2: 5, lineWidth: 3}]},
            {text: 'Head Office :' + response.footerBranchAddress, alignment: 'center', pageBreak: 'after'},
            {
                style: 'tableExample',
                table: {
                    body: [
                        [
                            {
                                border: [false, false, false, false],
                                image: 'data:image/jpeg;base64, ' + response.companyLogo,
                                width: 70,
                                height: 70,
                                alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                image: 'data:image/jpeg;base64, ' + response.companyLogoName,
                                width: 400,
                                height: 70,
                                alignment: 'center'
                            }
                        ],

                    ]
                }
            },
            {text: response.branchName + ':' + response.companyAddress, alignment: 'center'},
            {canvas: [{type: 'line', x1: 0, y1: 5, x2: 595 - 2 * 40, y2: 5, lineWidth: 3}]},
            {text: 'Bill (Office Copy)', alignment: 'center'},
            {text: '-------------------------------', alignment: 'center'},
            {
                style: 'tableExample',
                table: {
                    widths: ['*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                border: [false, false, false, false], text: response.customerName, alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Date: ' + response.collectionDate,
                                alignment: 'right'
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false], text: response.customerAddress, alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Challan No: ' + response.billNo,
                                alignment: 'right'
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: 'Mobile No:' + response.customerPhone,
                                alignment: 'left'
                            },
                            {
                                border: [false, false, false, false], text: '', alignment: 'right'
                            }
                        ]
                    ]
                },
            },
            {
                style: 'tableExample',
                table: {
                    widths: [20, '*', '*', '*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                text: 'SL', alignment: 'center', fillColor: '#eeffee'
                            },
                            {
                                text: 'Description Of Goods', alignment: 'center', fillColor: '#eeeeff'
                            },
                            {
                                text: 'Quantity', alignment: 'center', fillColor: '#cccccc'
                            },
                            {
                                text: 'Unit Price in Taka', alignment: 'center', fillColor: '#cccccc'
                            },
                            {
                                text: 'Total Amount in Taka', alignment: 'center', fillColor: '#cccccc'
                            }
                        ]
                    ]
                },
            },
            {
                text: 'In Word: ' + response.totalInWord + ' Taka Only.\n',
                alignment: 'left'
            },

            {
                style: 'tableExample',
                table: {
                    widths: ['*', '*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Previous Due',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: response.previousDue + '/=',
                                alignment: 'left',
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Current Bill',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: response.currentBill + '/=',
                                alignment: 'left',
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Total Payable Amount',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: response.totalPayable + '/=',
                                alignment: 'left',
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Current Bill Collection',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: response.currentBillCollection + '/=',
                                alignment: 'left',
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Previous Collection Date\n(' + response.previousCollectionDate + ')',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: '__________________________',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: '____________',
                                alignment: 'left',
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Total Due',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: response.totalDue + '/=',
                                alignment: 'left',
                            }
                        ]
                    ]
                },
            },
            {
                text: '\n\n\n',
                alignment: 'center'
            },
            {
                style: 'tableExample',
                table: {
                    widths: ['*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                border: [false, false, false, false],
                                text: "____________________________________",
                                alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: '_____________________________________',
                                alignment: 'right'
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false], text: "Buyer's Authorizes", alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: 'For ' + response.companyName,
                                alignment: 'right'
                            }
                        ]
                    ]
                },
            },
            {canvas: [{type: 'line', x1: 0, y1: 5, x2: 595 - 2 * 40, y2: 5, lineWidth: 3}]},
            {text: 'Head Office :' + response.footerBranchAddress, alignment: 'center', pageBreak: 'after'},

            {
                style: 'tableExample',
                table: {
                    body: [
                        [
                            {
                                border: [false, false, false, false],
                                image: 'data:image/jpeg;base64, ' + response.companyLogo,
                                width: 70,
                                height: 70,
                                alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                image: 'data:image/jpeg;base64, ' + response.companyLogoName,
                                width: 400,
                                height: 70,
                                alignment: 'center'
                            }
                        ],

                    ]
                }
            },
            {text: response.branchName + ':' + response.companyAddress, alignment: 'center'},
            {canvas: [{type: 'line', x1: 0, y1: 5, x2: 595 - 2 * 40, y2: 5, lineWidth: 3}]},
            {text: 'Bill (Office Copy)', alignment: 'center'},
            {text: '-----------------------------------------', alignment: 'center'},
            {
                style: 'tableExample',
                table: {
                    widths: ['*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                border: [false, false, false, false], text: response.customerName, alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Date: ' + response.collectionDate,
                                alignment: 'right'
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false], text: response.customerAddress, alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Challan No: ' + response.billNo,
                                alignment: 'right'
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: 'Mobile No:' + response.customerPhone,
                                alignment: 'left'
                            },
                            {
                                border: [false, false, false, false], text: '', alignment: 'right'
                            }
                        ]
                    ]
                },
            },
            {
                style: 'tableExample',
                table: {
                    widths: [20, '*', '*', '*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                text: 'SL', alignment: 'center', fillColor: '#eeffee'
                            },
                            {
                                text: 'Description Of Goods', alignment: 'center', fillColor: '#eeeeff'
                            },
                            {
                                text: 'Quantity', alignment: 'center', fillColor: '#cccccc'
                            },
                            {
                                text: 'Unit Price in Taka', alignment: 'center', fillColor: '#cccccc'
                            },
                            {
                                text: 'Total Amount in Taka', alignment: 'center', fillColor: '#cccccc'
                            }
                        ]
                    ]
                },
            },
            {
                text: 'In Word: ' + response.totalInWord + ' Taka Only.\n',
                alignment: 'left'
            },

            {
                style: 'tableExample',
                table: {
                    widths: ['*', '*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Previous Due',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: response.previousDue + '/=',
                                alignment: 'left',
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Current Bill',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: response.currentBill + '/=',
                                alignment: 'left',
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Total Payable Amount',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: response.totalPayable + '/=',
                                alignment: 'left',
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Current Bill Collection',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: response.currentBillCollection + '/=',
                                alignment: 'left',
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Previous Collection Date\n(' + response.previousCollectionDate + ')',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: '__________________________',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: '____________',
                                alignment: 'left',
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false],
                                text: '',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: 'Total Due',
                                alignment: 'left',
                            },
                            {
                                border: [false, false, false, false],
                                text: response.totalDue + '/=',
                                alignment: 'left',
                            }
                        ]
                    ]
                },
            },
            {
                text: '\n\n\n',
                alignment: 'center'
            },
            {
                style: 'tableExample',
                table: {
                    widths: ['*', '*'],
                    headerRows: 1,
                    body: [
                        [
                            {
                                border: [false, false, false, false],
                                text: "____________________________________",
                                alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: '_____________________________________',
                                alignment: 'right'
                            }
                        ],
                        [
                            {
                                border: [false, false, false, false], text: "Buyer's Authorizes", alignment: 'left'
                            },
                            {
                                border: [false, false, false, false],
                                text: 'For ' + response.companyName,
                                alignment: 'right'
                            }
                        ]
                    ]
                },
            },
            {canvas: [{type: 'line', x1: 0, y1: 5, x2: 595 - 2 * 40, y2: 5, lineWidth: 3}]},
            {text: 'Head Office :' + response.footerBranchAddress, alignment: 'center'}
        ],
        styles: styles,
        pageSize: 'A4',
        pageOrientation: 'portrait',
        layout: 'noBorders',
        defaultStyle: {font: 'Kalpurush'}
    }
    for (var j = 0; j < data.tableData1.length; j++) {
        report.content[6].table.body.push(data.tableData1[j]);
    }
    for (var j = 0; j < data.tableData.length; j++) {
        report.content[16].table.body.push(data.tableData[j]);
    }
    for (var j = 0; j < data.tableDataBill1.length; j++) {
        report.content[27].table.body.push(data.tableDataBill1[j]);
    }
    for (var j = 0; j < data.tableDataBill.length; j++) {
        report.content[40].table.body.push(data.tableDataBill[j]);
    }
//pdfMake.createPdf(report).open();
    pdfMake.createPdf(report).download('bill_challan_report_' + create_UUID() + '.pdf');
}

function create_UUID() {
    var dt = new Date().getTime();
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (dt + Math.random() * 16) % 16 | 0;
        dt = Math.floor(dt / 16);
        return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
    return uuid;
}