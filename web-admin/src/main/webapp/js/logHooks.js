// Hooks for logging console

function initLogHooks() {

    // open asynchronous message
    $('.line .CORRELATION_ID').click(function () {
        window.open("../messages/searchForm?source=" + $(this).prev().text() + "&correlation="
            + $(this).text());
        return false;
    });

    // open dialog when the user clicks on req/resp which were sent into integration platform
    $(".line.o\\.s\\.w\\.s\\.s\\.e\\.i\\.SoapEnvelopeLoggingInterceptor .msg,.soapMessage").each(function (i, block) {
        $(this).click(function() {
            var $dialog = $('<div></div>')
                .dialog({
                    height: 600,
                    width: 800});
            $dialog.addClass("dialogbox");
            $dialog.dialog('open');

            $dialog.text(formatXml($(this).text()));
            $dialog.wrap('<pre><code class="xml"></code></pre>');
            $('pre code').each(function(i, block) {
                hljs.highlightBlock(block);
            });
        })
    });

    // open dialog when the user clicks on req/resp which were sent from integration platform
    $(".line.o\\.s\\.ws\\.client\\.MessageTracing\\.sent .msg, .line.o\\.s\\.ws\\.client\\.MessageTracing\\" +
        ".received .msg").each(function (i, block) {

        $(this).click(function() {
            var $dialog = $('<div></div>')
                .dialog({
                    height: 600,
                    width: 800});
            $dialog.addClass("dialogbox");
            $dialog.dialog('open');

            $dialog.text(formatXml($(this).text()));
            $dialog.wrap('<pre><code class="xml"></code></pre>');
            $('pre code').each(function(i, block) {
                hljs.highlightBlock(block);
            });
        })
    });

    // all containers that exceed the height of the element are truncated
    $(".truncateContainer").each(function () {

        var maxheight = 250;
        var showText = "More";
        var hideText = "Less";

        var text = $(this);

        if (text.height() > maxheight) {
            text.css({ 'overflow': 'hidden', 'height': maxheight + 'px' });

            var link = $('<a class="showMoreBtn" href="#">' + showText + '</a>');
            var linkDiv = $('<div></div>');
            linkDiv.append(link);
            $(this).after(linkDiv);

            link.click(function (event) {
                event.preventDefault();
                if (text.height() > maxheight) {
                    $(this).html(showText);
                    text.css('height', maxheight + 'px');
                } else {
                    $(this).html(hideText);
                    text.css('height', 'auto');
                }
            });
        }
    });

}

// format xml
function formatXml(xml) {
    var formatted = '';
    var reg = /(>)(<)(\/*)/g;
    xml = xml.replace(reg, '$1\r\n$2$3');
    var pad = 0;
    jQuery.each(xml.split('\r\n'), function(index, node) {
        var indent = 0;
        if (node.match( /.+<\/\w[^>]*>$/ )) {
            indent = 0;
        } else if (node.match( /^<\/\w/ )) {
            if (pad != 0) {
                pad -= 1;
            }
        } else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
            indent = 1;
        } else {
            indent = 0;
        }

        var padding = '';
        for (var i = 0; i < pad; i++) {
            padding += '  ';
        }

        formatted += padding + node + '\r\n';
        pad += indent;
    });

    return formatted;
}