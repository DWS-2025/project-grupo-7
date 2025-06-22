$(document).ready(function() {
    var quill = new Quill('#editor', {
        theme: 'snow'
    });

    quill.on('text-change', function(delta, oldDelta, source) {
        var text = quill.getSemanticHTML();
        $('#commentBody').val(text);
    });
});
