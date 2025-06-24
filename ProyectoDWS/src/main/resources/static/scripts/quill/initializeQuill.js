$(document).ready(function() {

    var text = $('#commentBody').val();

    var quill = new Quill('#editor', {
        theme: 'snow'
    });

    quill.on('text-change', function(delta, oldDelta, source) {
        var text = quill.getSemanticHTML();
        $('#commentBody').val(text);
    });

    if (text) {
        quill.clipboard.dangerouslyPasteHTML(text);
    }
});
