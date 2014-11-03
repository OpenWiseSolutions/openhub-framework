$.datepicker.regional['cs'] = {
    closeText: "Hotov", // Display text for close link
    prevText: "Předchozí", // Display text for previous month link
    nextText: "Následující", // Display text for next month link
    currentText: "Dnes", // Display text for current month link
    monthNames: ["Leden", "Únor", "Březen", "Duben", "Květen", "Červen",
        "Červenec", "Srpen", "Září", "Říjen", "Listopad", "Prosinec"], // Names of months for drop-down and formatting
    monthNamesShort: ["Led", "Úno", "Bře", "Dub", "Kvě", "Čen", "Čec", "Srp", "Zář", "Říj", "Lis", "Pro"], // For formatting
    dayNames: ["Neděle", "Pondělí", "Úterý", "Středa", "Čtvrtek", "Pátek", "Sobota"], // For formatting
    dayNamesShort: ['Ne', 'Po', 'Út', 'St', 'Čt', 'Pá', 'So'],  // For formatting
    dayNamesMin: ["Ne", "Po", "Út", "St", "Čt", "Pá", "So"], // Column headings for days starting at Sunday
    weekHeader: "Sm", // Column header for week of the year
    dateFormat: "mm/dd/yy", // See format options on parseDate
    firstDay: 1, // The first day of the week, Sun = 0, Mon = 1, ...
    showMonthAfterYear: false
};
//$.datepicker.setDefaults($.datepicker.regional['cs']);
$(document).ready(function () {
    /* datepicker init*/
    $("#bcStartFromDate").datepicker({ dateFormat: "dd-mm-yy" });
    $("#bcStartToDate").datepicker({ dateFormat: "dd-mm-yy" });
    /*  logical test for the search panel*/
    svcCheck();
    var searchErrorText = "You must choose at least 1 search parameter:<br>";
    searchErrorText += "* [ID] or  [From + To] or [State/s] *<br>";
    searchErrorText += "Or combinations: <br>";
    searchErrorText += "* [ID + State/s] *<br>";
    searchErrorText += "* [ID + From + To] *<br>";
    searchErrorText += "* [State/s + From + To] *<br>";

    $('#search-panel > form').submit(function () {
        if (typeof $('#bcStates option:selected').val() !== "undefined"
            && $('#bcStates option:selected').length > 0
            && $('#bcStartFromDate').val().length > 0
            && $('#bcStartToDate').val().length > 0) {
            return true
        } else if ($('#bcStartFromDate').val().length > 0
            && $('#bcStartToDate').val().length > 0) {
            return true
        } else if (typeof $('#bcStates option:selected').val() !== "undefined"
            && $('#bcStates option:selected').length > 0) {
            return true
        } else if ($('#searchBCId').val().length > 0) {
            return true
        } else {
            $('#notificator').html(searchErrorText)
                .css('color', '#F00')
                .show().fadeOut(15000);
            return false
        }
    });
    /* Marks the selected clicked row in #resultTable and initialize #result-options fields*/
    var $restableTR = $('#resultTable').find('tr');
    $('#result-options input:text').attr('readonly', 'readonly'); // disable result operations text input fields
    $restableTR.click(function () {
        $(this).each(function () {
            $restableTR.removeClass("selected")
        });
        $(this).addClass("selected").find("th input:radio").prop('checked', true);
        roinputinit();
        robuttoninit();
    // psebela 01072013: 4) schoval bych ten text "Billin cycle oznacek ke zpracovani..." ;)
    // $('#notificator').text("Billing cycle byl označen pro zpracování...").css('color','green').show().fadeOut(1000);
    });

    /* getBillPreview() prompt handler */
    $('button[value*="billPreview"]').click(function(event) {
        var usrInput = prompt("Pro BillingCycle ID: " + $('#robcID').val() + " zadejte prosím Customer Account External Number (CRM): ");
        /* if the userInput is < 1 no request is sent to the controler*/
        if(usrInput < 1) {
            alert("You MUST specify the Customer Account Ext No.!");
            event.preventDefault()
        } else {
            return $('#robcCstAccExtNo').val(usrInput);
        }
    });
    $('.sbuttons button').click(function(e){
        if ( e.preventDefault ) {
            e.preventDefault();
        } else {
            e.returnValue = false;
        }
        if (window.location.pathname !== "/web/admin/bcops") {
            window.location.href = $('.sbuttons > a').attr('href')

        } else {
            history.back();
        }
        }
    );
    $('#bcStates').click(function() {
        if ($("#bcStates option:selected").eq(0).val() === 'ALL') {
            $('select option').prop('selected', true);
            $('select option[value="ALL"]').eq(0).prop('selected', false);
        }
    });
});
/* end of ready(function()) */

/* Results Operations buttons deactivation*/
function robuttondisable() {
    $('.robuttons li button').each(function () {
        $(this).attr('disabled', true);
    });
}
/* Results Operations input fields initialization*/
function roinputinit() {
    $('#robcID').val($('#resultTable tr.selected td').eq(1).text());
    $('#robcState').val($('#resultTable tr.selected td').eq(2).text());
    $('#robcPerStart').val($('#resultTable tr.selected td').eq(3).text());
    $('#robcPerEnd').val($('#resultTable tr.selected td').eq(4).text());
    $('#robcSpecCode').val($('#resultTable tr.selected td').eq(5).text());
    $('#robcClosureDate').val($('#resultTable tr.selected td').eq(6).text());
    $('#robcIssueDate').val($('#resultTable tr.selected td').eq(7).text());
    $('#robcDueDate').val($('#resultTable tr.selected td').eq(8).text());
    $('#robcBQCReached').val($('#resultTable tr.selected td').eq(9).text());
    $('#robcBQCConfrimed').val($('#resultTable tr.selected td').eq(10).text());
    $('#robcClosed').val($('#resultTable tr.selected td').eq(11).text());
    $('#robcIsClosing').val($('#resultTable tr.selected td').eq(12).text());
}

/* Results Operations buttons initialization*/
function robuttoninit() {

    robuttondisable(); // first disable all buttons, then enable

    var robcstate = $('#robcState').val();

    if($('#robcState').val() != ""
        && ($('#robcState').val() === "OPEN" || $('#robcState').val() === "BILLS_COMPILED" || $('#robcState').val() === "TAX_RECALC" || $('#robcState').val() === "BILL_RND" )
        && ($('#robcBQCConfrimed').val() === "-" && $('#robcBQCReached').val() === "-")
        && $('#robcIsClosing').val() === "false") {
        $('.robuttons li button').eq(0).removeAttr('disabled')
    }

    if($('#robcState').val() != "" && $('#robcState').val() === "BQC" && $('#robcBQCConfrimed').val() === "-" && $('#robcBQCReached').val() != "-"
        ) {
        for(var i = 1; i < 5; i++) {
            $('.robuttons li button').eq(i).removeAttr('disabled')
        }
    }

    if($('#robcState').val() != ""
        && ($('#robcState').val() === "BQC" || $('#robcState').val() === "BALANCE_DELTA_CALC" || $('#robcState').val() === "BILLS_NUMBERING" || $('#robcState').val() === "EXT_BILL_NO" )
        && ($('#robcBQCConfrimed').val() != "-" && $('#robcBQCReached').val() != "-")
        && $('#robcIsClosing').val() === "false") {
        $('.robuttons li button').eq(5).removeAttr('disabled')
    }
    if($('#robcState').val() != "" && $('#robcState').val() === "CLOSED" ) {
        $('.robuttons li button').eq(6).removeAttr('disabled')
    }
}

/* disable submit button if the webservice is not started*/
function svcCheck() {
    if($('#notificator').text().replace(/\s+/g, '') === "AdminServicenotstarted") {
        $('.sbuttons input:submit').attr('disabled',true);
    }
}
