document.addEventListener('DOMContentLoaded', function () {

    // ===================== Cambiar de imagen con el movimiento horizontal del mouse =====================

    const container = document.getElementById('imagen-principal');
    const images = container.querySelectorAll('.imagenProducto');
    const numImages = images.length;

    container.addEventListener('mousemove', function (event) {
        // Calcula la posición horizontal del mouse relativa al contenedor
        const rect = container.getBoundingClientRect();
        const mouseX = event.clientX - rect.left;

        // Calcula el índice de la imagen basada en la posición horizontal del mouse
        const index = Math.floor((mouseX / container.offsetWidth) * numImages);

        // Muestra la imagen correspondiente
        images.forEach((image, i) => {
            if (i === index) {
                image.style.display = 'block';
            } else {
                image.style.display = 'none';
            }
        });
    });

    // ============================ Carrusel de imágenes ===========================

    const carrusel = document.getElementById('carrucel-imagenes');
    const imagenesCarrusel = carrusel.querySelectorAll('.carrucelItem');
    const prevBtn = document.querySelector('#prev-btn');
    const nextBtn = document.querySelector('#next-btn');
    const carouselInner = document.querySelector('#carrucel-imagenes');

    // Obtener el valor del margen de la primera imagen del carrusel
    const estiloImagen = window.getComputedStyle(imagenesCarrusel[0]);
    const margen = parseFloat(estiloImagen.marginRight); // Convertimos el margen a un entero


    let currentIndex = 0;
    const slides = document.querySelectorAll('.carrucelItem');
    const slideWidth = slides[0].offsetWidth + margen; // Ancho de cada slide más el espacio entre ellos

    const slidesInView = 6; // Número de imágenes visibles en el carrusel

    const maxIndex = slides.length - slidesInView;


    nextBtn.addEventListener('click', function() {
        if (currentIndex < maxIndex) {
            currentIndex++;
            updateCarousel();
            prevBtn.classList.remove('hidden')
        }
        if (currentIndex === maxIndex){
            nextBtn.classList.add('hidden')
        }
    });

    prevBtn.addEventListener('click', function() {
        if (currentIndex > 0) {
            currentIndex--;
            updateCarousel();
            nextBtn.classList.remove('hidden')
        }
        if (currentIndex === 0){
            prevBtn.classList.add('hidden')
        }
    });

    function updateCarousel() {
        const offset = -currentIndex * slideWidth;
        const maxOffset = -(slides.length - slidesInView) * slideWidth;


        // Limitar el offset máximo para que la última imagen esté completamente visible
        if (offset < maxOffset) {
            currentIndex = slides.length - slidesInView;

            return;
        }

        carouselInner.style.transform = `translateX(${offset}px)`;
    }



    //===================================================================================================


});



document.addEventListener('DOMContentLoaded', function () {
    const images = document.querySelectorAll('.galeria-imagenes-modal');
    const prevButton = document.getElementById('anterior');
    const nextButton = document.getElementById('siguiente');
    let currentIndex = 0;

    // Oculta todas las imágenes excepto la primera
    for (let i = 1; i < images.length; i++) {
        images[i].style.display = 'none';
    }

    // Función para mostrar la imagen siguiente
    function showNextImage() {
        images.forEach(imagen => imagen.style.display = 'none');
        currentIndex = (currentIndex + 1) % images.length;
        images[currentIndex].style.display = 'block';
    }

    // Función para mostrar la imagen anterior
    function showPrevImage() {
        images.forEach(imagen => imagen.style.display = 'none');
        currentIndex = (currentIndex - 1 + images.length) % images.length;
        images[currentIndex].style.display = 'block';
    }

    // Agrega eventos a los botones
    nextButton.addEventListener('click', showNextImage);
    prevButton.addEventListener('click', showPrevImage);

    // Función para abrir el modal con la imagen correspondiente
    function abrirModal(id) {
        const modal = document.getElementById('modal');
        const modalImagen = document.querySelectorAll('.galeria-imagenes-modal');

        // Oculta el modal
        modal.classList.remove('hidden');
        modal.classList.add('fixed');

        // Oculta todas las imágenes del modal
        modalImagen.forEach(imagen => imagen.style.display = 'none');

        // Muestra la imagen correspondiente al ID en el modal
        modalImagen[id - 5].style.display = 'block';
        currentIndex = id - 5;
    }

    // Agrega eventos a las imágenes del carrusel
    const carrucelImagenes = document.querySelectorAll('.carrucelItem');
    carrucelImagenes.forEach(function(imagen, index) {
        imagen.addEventListener('click', function() {
            abrirModal(index + 5); // Sumamos 5 porque parece que las imágenes comienzan desde el índice 5
        });
    });

    // Cerrar modal al hacer clic en el botón de cerrar
    const cerrarModal = document.getElementById('cerrar-modal');
    cerrarModal.addEventListener('click', function () {
        const modal = document.getElementById('modal');
        modal.classList.remove('fixed'); // Remueve la clase 'fixed'
        modal.classList.add('hidden'); // Agrega la clase 'hidden'
    });
});