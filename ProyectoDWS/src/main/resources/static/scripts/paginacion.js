$(document).ready(function() {
    let currentPage = 1;  // current page
    let subjectsPerPage = 10;  // subjects for page

    function loadSubjects(page) {
        $.ajax({
            url: `/api/subjects?page=${page}&size=${subjectsPerPage}`,
            method: 'GET',
            success: function(data) {

                console.log("Data", data)

                let htmlContent = '';
                data.subjects.forEach(function(subject) {
                    htmlContent += `
                        <div class="card">
                            <img class="card-image" src="/subject/${subject.id}/image" alt="${subject.title}">
                            <a href="/subject/${subject.id}">${subject.title}</a>
                            <p><u>Descripción:</u> ${subject.text}</p>
                        </div>
                    `;
                });

                if (page == 1) {
                    $('#prev-page').addClass('disabled');
                } else {
                    $('#prev-page').removeClass('disabled');
                }

                if (page == data.totalPages) {
                    $('#next-page').addClass('disabled');
                } else {
                    $('#next-page').removeClass('disabled');
                }

                console.log("page", page);
                console.log("totalPages", data.totalPages);

                $('#subjects-list').html(htmlContent);
                $('#currentPage').text(page);
                $('#totalPages').text(data.totalPages);
                $('#elementsShown').text(`Mostrando ${data.subjects.length} asignaturas`);
            },
            error: function(xhr, status, error) {
                console.log("Error fetching subjects", error);
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

    $(".pageSizes button").click(function() {
        let pageSize = $(this).text();
        currentPage = 1;
        subjectsPerPage = pageSize;
        loadSubjects(currentPage);
    });
});

