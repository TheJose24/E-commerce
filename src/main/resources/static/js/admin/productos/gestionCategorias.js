document.addEventListener('DOMContentLoaded', function() {
    // Manejar edición de categoría
    document.querySelectorAll('.editar-categoria').forEach(button => {
        button.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const nombre = this.getAttribute('data-nombre');

            document.getElementById('editarCategoriaId').value = id;
            document.getElementById('editarNombreCategoria').value = nombre;

            const editarModal = new bootstrap.Modal(document.getElementById('editarCategoriaModal'));
            editarModal.show();
        });
    });
});