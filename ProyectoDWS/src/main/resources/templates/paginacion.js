$(document).ready(function() {
    let currentPage = 0;  // current page
    const subjectsPerPage = 10;  // subjects for page


    function loadSubjects(page) {
        $.ajax({
            url: `/subjects?page=${page}&size=${subjectsPerPage}`,
            method: 'GET',
            success: function(data) {
                let htmlContent = '';
                data.subjects.forEach(function(subject) {
                    htmlContent += `
                        <div class="card">
                            <a href="/subject/${subject.id}">${subject.title}</a>
                            <p><u>Descripción:</u> ${subject.text}</p>
                        </div>
                    `;
                });
                $('#subjects-list').html(htmlContent);
                $('#currentPage').text(page + 1);
            }
        });
    }


    loadSubjects(currentPage);


    $('#prev-page').click(function() {
        if (currentPage > 0) {
            currentPage--;
            loadSubjects(currentPage);
        }
    });

    $('#next-page').click(function() {
        currentPage++;
        loadSubjects(currentPage);
    });
});

