var Payment = (function () {

    $(function () {
        var events = $('.events table tbody');
        if (events.length) {
            $.getJSON('/status', function(es) {
                $.each(es, function(i, e) {
                    events.append('<tr>' + 
                                  '<td>' + e.number + '</td>' +
                                  '<td>' + e.sum + '</td>' +
                                  '<td>' + e.result + '</td>' +
                                  '<td>' + e.timestamp + '</td>' +
                                  '</tr>');
                });
            });
        }
    });

    var submitPaymentForm = function () {
        $('.loader').show();
        $('#pay').submit();
    }

    return {
        submitPaymentForm: submitPaymentForm
    }

})();

