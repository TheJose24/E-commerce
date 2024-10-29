document.addEventListener('DOMContentLoaded', function () {
    // Obtener todos los checkboxes y productos
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');
    const products = document.querySelectorAll('.col');

    // Función para filtrar productos
    function filterProducts() {
        const filters = {
            disponibilidad: [],
            nombre: [],
            material: [],
            color: []
        };

        // Obtener los valores de los checkboxes seleccionados
        checkboxes.forEach((checkbox) => {
            if (checkbox.checked) {
                filters[checkbox.name].push(checkbox.value);
            }
        });

        // Mostrar/ocultar productos según los filtros seleccionados
        products.forEach((product) => {
            const productName = product.querySelector('.card-title').textContent.toLowerCase();
            const productDisponibilidad = productName.includes("fabric") ? "disponible" : "hacer_pedido";
            const productModelo = productName.includes("sr9") ? "Modelo C" : (productName.includes("sr7") ? "Modelo B" : "Modelo A");
            const productMaterial = productName.includes("leather") ? "Cuero" : "Tela";
            const productColor = productName.includes("black") ? "Negro" : (productName.includes("red") ? "Rojo" : "Gris");

            // Verificar si el producto coincide con los filtros seleccionados
            const matchesDisponibilidad = filters.disponibilidad.length === 0 || filters.disponibilidad.includes(productDisponibilidad);
            const matchesModelo = filters.nombre.length === 0 || filters.nombre.includes(productModelo);
            const matchesMaterial = filters.material.length === 0 || filters.material.includes(productMaterial);
            const matchesColor = filters.color.length === 0 || filters.color.includes(productColor);

            // Mostrar u ocultar el producto basado en los filtros
            if (matchesDisponibilidad && matchesModelo && matchesMaterial && matchesColor) {
                product.style.display = 'block'; // Mostrar
            } else {
                product.style.display = 'none'; // Ocultar
            }
        });
    }

    // Agregar un event listener a todos los checkboxes
    checkboxes.forEach((checkbox) => {
        checkbox.addEventListener('change', filterProducts);
    });
});
