document.addEventListener('DOMContentLoaded', function () {
    const btnAbrirCarrito = document.getElementById('btn-abrirCarrito');
    const btnCerrarCarrito = document.getElementById('btn-cerrarCarrito');
    const contenedorCarrito = document.getElementById('contenedor-carrito');
    let pedido = null;
    let detalles = [];

    // Event Listeners
    btnAbrirCarrito?.addEventListener('click', () => contenedorCarrito.classList.add('abierto'));
    btnCerrarCarrito?.addEventListener('click', () => contenedorCarrito.classList.remove('abierto'));

    // Inicialización
    cargarCarrito();

    // Funciones globales para acceso externo
    window.cargarCarrito = cargarCarrito;
    window.mostrarNotificacion = function(mensaje, tipo = 'success') {
        const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer);
                toast.addEventListener('mouseleave', Swal.resumeTimer);
            }
        });

        Toast.fire({
            icon: tipo,
            title: mensaje
        });
    };

    // Función para agregar producto al carrito
    window.agregarAlCarrito = async function(productoId) {
        try {
            const response = await fetch('/api/v1/carrito/agregarProducto', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(productoId) // Enviamos directamente el ID como número
            });

            if (!response.ok) {
                throw new Error('Error al agregar el producto al carrito');
            }

            await cargarCarrito();
            mostrarNotificacion('Producto agregado al carrito');
            btnAbrirCarrito.click();
        } catch (error) {
            console.error('Error:', error);
            mostrarNotificacion(error.message, 'error');
        }
    };

    // Funciones de utilidad
    function calcularPrecioConDescuento(precio, descuento) {
        return precio - (precio * (descuento / 100));
    }

    // Funciones de interfaz
    function actualizarInterfaz(data) {
        const productosEnCarritoContainer = document.getElementById('productosEnCarritoContainer');
        const subtotalText = document.getElementById('subtotal');
        const totalText = document.getElementById('total');

        if (!data.detalles || data.detalles.length === 0) {
            mostrarCarritoVacio(productosEnCarritoContainer, subtotalText, totalText);
            return;
        }

        renderizarProductos(data, productosEnCarritoContainer);
        actualizarTotales(data, subtotalText, totalText);
        configurarBotonesControl();
    }

    function mostrarCarritoVacio(container, subtotalText, totalText) {
        const mensaje = document.createElement('p');
        mensaje.classList.add('text-center', 'text-lg');
        mensaje.textContent = 'No tienes nada en tu carrito aún.';
        container.innerHTML = '';
        container.appendChild(mensaje);
        subtotalText.innerText = '0.00';
        totalText.innerText = '0.00';
    }

    function actualizarTotales(data, subtotalText, totalText) {
        const subtotal = data.detalles.reduce((acc, detalle) =>
            acc + (calcularPrecioConDescuento(detalle.producto.precio, detalle.producto.descuento) * detalle.cantidad), 0);

        subtotalText.innerText = subtotal.toFixed(2);
        totalText.innerText = subtotal.toFixed(2);
    }

    function renderizarProductos(data, container) {
        container.innerHTML = '';
        data.detalles.forEach(detalle => {
            const divProducto = crearElementoProducto(detalle);
            container.appendChild(divProducto);
        });
    }

    function crearElementoProducto(detalle) {
        const divProducto = document.createElement('div');
        divProducto.classList.add('producto', 'd-flex', 'justify-content-between', 'align-items-center', 'border', 'border-dark', 'rounded', 'mb-2', 'p-2');

        const precioConDescuento = calcularPrecioConDescuento(
            detalle.producto.precio,
            detalle.producto.descuento
        ).toFixed(2);

        divProducto.innerHTML = `
            <div class="d-flex align-items-center">
                <img class="img-fluid me-3" style="width: 70px;" 
                     src="/productoImages/${detalle.producto.imagenes[0].urlImagen}" 
                     alt="${detalle.producto.slug}">
                <div>
                    <h5 class="mb-0">${detalle.producto.nombre} ${detalle.producto.color}</h5>
                    <small>${detalle.producto.material} ™</small>
                </div>
            </div>
            <div class="d-flex align-items-center">
                <span class="cantidad-producto me-2" data-id="${detalle.producto.productoId}">${detalle.cantidad}</span>
                <div class="d-flex flex-column">
                    <button class="btn-aumentar btn btn-outline-secondary mb-1" data-id="${detalle.producto.productoId}">▲</button>
                    <button class="btn-disminuir btn btn-outline-secondary" data-id="${detalle.producto.productoId}">▼</button>
                </div>
            </div>
            <div class="text-end">
                <p class="mb-1">S/. ${precioConDescuento}</p>
                <button class="btn-eliminar btn btn-danger btn-sm" data-id="${detalle.producto.productoId}">
                    <i class="fas fa-trash"></i> Eliminar
                </button>
            </div>
        `;
        return divProducto;
    }

    // Funciones de API
    async function actualizarCantidad(productoId, incremento) {
        try {
            const response = await fetch(`/api/v1/carrito/actualizarCantidad/${productoId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ incremento: incremento })
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.mensaje || 'Error al actualizar cantidad');
            }

            await cargarCarrito();
            mostrarNotificacion('Cantidad actualizada correctamente');
        } catch (error) {
            console.error('Error:', error);
            mostrarNotificacion(error.message, 'error');
        }
    }

    async function cargarCarrito() {
        try {
            const response = await fetch('/api/v1/carrito/verCarrito');
            if (!response.ok) {
                throw new Error('Error al obtener el carrito');
            }
            const data = await response.json();
            pedido = data;
            detalles = data.detalles || [];
            actualizarInterfaz(data);
        } catch (error) {
            console.error('Error al cargar el carrito:', error);
            mostrarNotificacion('Error al cargar el carrito', 'error');
        }
    }

    async function eliminarProducto(id) {
        try {
            const response = await fetch(`/api/v1/carrito/eliminarProducto/${id}`, {
                method: 'DELETE',
                headers: {
                    'Accept': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Error al eliminar el producto');
            }

            await cargarCarrito();
            mostrarNotificacion('Producto eliminado del carrito');
        } catch (error) {
            console.error('Error:', error);
            mostrarNotificacion('Error al eliminar el producto', 'error');
        }
    }

    async function irAPagar() {
        if (!pedido.detalles || pedido.detalles.length === 0) {
            mostrarNotificacion('No tienes nada en tu carrito aún.', 'warning');
            return;
        }

        const pedidoDTO = {
            subtotal: parseFloat(document.getElementById('subtotal').textContent),
            total: parseFloat(document.getElementById('total').textContent),
            detalles: pedido.detalles.map(detalle => ({
                productoId: detalle.producto.productoId,
                cantidad: parseInt(detalle.cantidad),
                precioUnitario: detalle.producto.precio,
                total: calcularPrecioConDescuento(
                    detalle.producto.precio,
                    detalle.producto.descuento
                ) * detalle.cantidad
            }))
        };

        fetch('/api/v1/carrito/pagar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(pedidoDTO)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al procesar el pago');
                }
                window.location.href = '/procesoPago';
            })
            .catch(error => {
                console.error('Error:', error);
                mostrarNotificacion('Error al procesar el pago', 'error');
            });
    }

    // Configuración de eventos
    function configurarBotonesControl() {
        document.querySelectorAll('.btn-aumentar').forEach(btn => {
            btn.addEventListener('click', () => actualizarCantidad(btn.dataset.id, true));
        });

        document.querySelectorAll('.btn-disminuir').forEach(btn => {
            btn.addEventListener('click', () => actualizarCantidad(btn.dataset.id, false));
        });

        document.querySelectorAll('.btn-eliminar').forEach(btn => {
            btn.addEventListener('click', () => eliminarProducto(btn.dataset.id));
        });

        document.getElementById('btn-procesoPago')?.addEventListener('click', irAPagar);
    }
});