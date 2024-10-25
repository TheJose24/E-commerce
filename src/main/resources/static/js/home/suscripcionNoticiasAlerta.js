document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('suscripcionForm');
    form.addEventListener('submit', function (event) {
        event.preventDefault();
        const email = form.email.value;
        console.log(email);

        fetch('/api/email/suscripcion-noticias', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({email: email})
        }).then(function (response) {
            if (response.ok) {
                Swal.fire({
                    title: 'Te has suscrito correctamente',
                    text: response.message,
                    icon: 'success',
                    confirmButtonText: 'Aceptar'
                })
                form.reset();
            } else {
                Swal.fire({
                    title: 'Error al suscribirte',
                    text: 'Ha ocurrido un error al suscribirte, por favor intenta de nuevo',
                    icon: 'error',
                    confirmButtonText: 'Aceptar'
                })
            }

        });
    });
});