$(document).ready(function() {

    // Format the roles to be more readable
    $('.role').each(function() {
        var role = $(this).text();
        switch(role) {
            case 'USER':
                $(this).text('Usuario');
                break;
            case 'ADMIN':
                $(this).text('Administrador');
                break;
            default:
                $(this).text(role);
        }
    });

});
