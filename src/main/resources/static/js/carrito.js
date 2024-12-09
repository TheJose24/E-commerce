const btnAbrirCarrito = document.getElementById('btn-abrirCarrito');
const btnCerrarCarrito = document.getElementById('btn-cerrarCarrito');
const contenedorCarrito = document.getElementById('contenedor-carrito');

// Función para agregar al carrito
function agregarAlCarrito(event) {
    event.preventDefault();
    const boton = event.target;
    const productoId = boton.getAttribute('data-producto-id');
    
    console.log('Intentando agregar producto:', productoId);

    fetch('/api/v1/carrito/agregar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            productoId: parseInt(productoId),
            cantidad: 1
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error al agregar el producto');
        }
        return response.json();
    })
    .then(data => {
        console.log('Producto agregado exitosamente:', data);
        cargarCarrito();
        contenedorCarrito.classList.add('abierto'); // Abre el carrito automáticamente
    })
    .catch(error => {
        console.error('Error al agregar producto:', error);
    });
}

// Event Listeners
document.addEventListener('DOMContentLoaded', function() {
    // Event listener para botones de agregar
    document.querySelectorAll('.btn-success').forEach(boton => {
        boton.addEventListener('click', agregarAlCarrito);
    });

    btnAbrirCarrito.addEventListener('click', function() {
        contenedorCarrito.classList.add('abierto');
    });

    btnCerrarCarrito.addEventListener('click', function() {
        contenedorCarrito.classList.remove('abierto');
    });

    cargarCarrito();
});

function cargarCarrito() {
    fetch('/api/v1/carrito')
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al obtener el carrito');
            }
            return response.json();
        })
        .then(data => {
            console.log('Datos del carrito:', data);
            const productosEnCarritoContainer = document.getElementById('productosEnCarritoContainer');
            productosEnCarritoContainer.innerHTML = ''; // Limpiar el contenedor

            if (!data || !data.detalles || data.detalles.length === 0) {
                productosEnCarritoContainer.innerHTML = `
                    <p class="text-center text-lg">No tienes nada en tu carrito aún.</p>
                `;
                actualizarTotales(0, 0, 0);
                return;
            }

            // Renderizar cada producto
            data.detalles.forEach(detalle => {
                const divProducto = document.createElement('div');
                divProducto.classList.add('producto', 'd-flex', 'justify-content-between', 'align-items-center', 'border', 'border-dark', 'rounded', 'mb-2', 'p-2');
                
                divProducto.innerHTML = `
                    <div class="d-flex align-items-center">
                        <img class="img-fluid me-3" style="width: 70px;" 
                             src="/images/${detalle.producto.imagenes[0].url}" 
                             alt="${detalle.producto.nombre}">
                        <div>
                            <h5 class="mb-0">${detalle.producto.nombre}</h5>
                            <small>Cantidad: ${detalle.cantidad}</small>
                        </div>
                    </div>
                    <div class="text-end">
                        <p class="mb-1">S/. ${calcularPrecioConDescuento(detalle.producto)}</p>
                        <button class="btn-eliminar btn btn-link text-danger" 
                                data-id="${detalle.producto.id}">
                            Eliminar
                        </button>
                    </div>
                `;
                
                productosEnCarritoContainer.appendChild(divProducto);
            });

            // Actualizar totales
            actualizarTotales(data.subTotal, data.envio, data.precioTotal);

            // Agregar event listeners para los botones de eliminar
            agregarEventListenersEliminar();
        })
        .catch(error => {
            console.error('Error al cargar el carrito:', error);
            document.getElementById('productosEnCarritoContainer').innerHTML = `
                <p class="text-center text-danger">Error al cargar el carrito</p>
            `;
        });
}

function calcularPrecioConDescuento(producto) {
    const precio = producto.precio_unitario;
    const descuento = producto.descuento / 100;
    return (precio - (precio * descuento)).toFixed(2);
}

function actualizarTotales(subtotal, envio, total) {
    document.getElementById('subtotal').textContent = `S/. ${subtotal.toFixed(2)}`;
    document.getElementById('envio').textContent = `S/. ${envio.toFixed(2)}`;
    document.getElementById('total').textContent = `S/. ${total.toFixed(2)}`;
}

function agregarEventListenersEliminar() {
    document.querySelectorAll('.btn-eliminar').forEach(btn => {
        btn.addEventListener('click', function() {
            const productoId = this.dataset.id;
            eliminarProducto(productoId);
        });
    });
}

function eliminarProducto(productoId) {
    fetch(`/api/v1/carrito/eliminar/${productoId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error al eliminar el producto');
        }
        cargarCarrito(); // Recargar el carrito después de eliminar
    })
    .catch(error => {
        console.error('Error al eliminar producto:', error);
    });
}
