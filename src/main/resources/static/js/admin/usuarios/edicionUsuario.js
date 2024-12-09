document.addEventListener('DOMContentLoaded', function() {
    // Obtener el modal
    const modal = document.getElementById('editarUsuarioModal');

    // Escuchar el evento show.bs.modal
    modal.addEventListener('show.bs.modal', function(event) {
        // Botón que activó el modal
        const button = event.relatedTarget;

        // Extraer datos del botón
        const id = button.getAttribute('data-id');
        const nombre = button.getAttribute('data-nombre');
        const email = button.getAttribute('data-email');

        // Actualizar campos del formulario
        document.getElementById('editarUsuarioId').value = id;
        document.getElementById('editarNombre').value = nombre;
        document.getElementById('editarEmail').value = email;
    });

    // Manejar envío del formulario
    document.getElementById('formEditarUsuario').addEventListener('submit', function(e) {
        e.preventDefault();

        // Obtener datos del formulario
        const formData = new FormData(this);

        // Enviar petición
        fetch('/admin/usuariosAdmin/actualizarUsuario', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    window.location.reload(); // Recargar página
                } else {
                    alert('Error al actualizar usuario');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al actualizar usuario');
            });
    });
});