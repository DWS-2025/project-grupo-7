$(document).ready(function() {
    let searchTimeout;
    $("#searchInput").on("input", function() {
        clearTimeout(searchTimeout);

        const $input = $(this);
        searchTimeout = setTimeout(() => {
            const searchValue = $input.val().toLowerCase();
            $.ajax({
                url: "/api/courses/search",
                type: "GET",
                data: { search: searchValue },
                success: function(data) {
                    const courses = data.courses;
                    $("#courses-container").empty();

                    let coursesHTML = "";

                    courses.forEach(course => {
                        let courseHTML = "";
                        let subjectsHTML = "";
                        course.subjects.forEach(subject => {
                            subjectsHTML += `
                                <li class="list-group-item">${subject.title}</li>
                            `;
                        });

                        courseHTML += `
                            <h5 class="card-title">
                                <a href="/course/${course.id}">${course.title}</a>
                            </h5>
                            <p class="card-text"><strong>Asignaturas:</strong></p>
                            <ul class="list-group list-group-flush">
                                ${subjectsHTML}
                            </ul>
                        `;

                        coursesHTML += `
                            <div class="col">
                                <div class="card h-100">
                                    <img class="card-img-top" src="/course/${course.id}/image" alt="${course.title}">
                                    <div class="card-body">
                                        ${courseHTML}
                                    </div>
                                </div>
                            </div>
                        `;
                    });

                    $("#courses-container").html(coursesHTML);
                },
                error: function(xhr, status, error) {
                    console.error("Error al buscar cursos:", error);
                }
            });
        }, 300);
    });
});
