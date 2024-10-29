document.addEventListener("DOMContentLoaded", function() {
    const editButtons = document.querySelectorAll('button[data-bs-target="#editarProductoModal"]');

    editButtons.forEach(button => {
        button.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const nombre = this.getAttribute('data-nombre');
            const categoria = this.getAttribute('data-categoria');
            const precio = this.getAttribute('data-precio');
            const stock = this.getAttribute('data-stock');
            const descuento = this.getAttribute('data-descuento');
            const color = this.getAttribute('data-color');
            const material = this.getAttribute('data-material');
            const imagenesString = this.getAttribute('data-img');


            const imagenes = imagenesString.match(/urlImagen='([^']+)'/g) || [];
            const urls = imagenes.map(imagen => imagen.match(/'([^']+)'/)[1]);
            // Llenar el modal con los datos del producto
            document.getElementById('editarProductoId').value = id;
            document.getElementById('editarNombre').value = nombre;
            document.getElementById('editarCategoria').value = categoria;
            document.getElementById('editarPrecio').value = precio;
            document.getElementById('editarStock').value = stock;
            document.getElementById('editarDescuento').value = descuento;
            document.getElementById('editarColor').value = color;
            document.getElementById('editarMaterial').value = material;

            // Mostrar imágenes actuales
            const imagenesContainer = document.getElementById('imagenesActuales');
            imagenesContainer.innerHTML = '';

            urls.forEach(url => {
                const imgElement = document.createElement('img');
                imgElement.src = "/productoImages/"+url;
                imgElement.alt = "Imagen del producto";
                imgElement.classList.add('img-thumbnail', 'me-2');
                imgElement.style.width = "100px";
                imagenesContainer.appendChild(imgElement);
            });
        });
    });
});
