document.addEventListener('DOMContentLoaded', function () {
    let currentIndex = 0;
    const slides = document.getElementById('slides');
    const totalImages = slides.children.length;

    function showNextImages() {
        if (totalImages >= 5) {
            currentIndex = (currentIndex + 1) % (totalImages - 4);
            const offset = -currentIndex * (100 / 5); // 5 imágenes visibles
            slides.style.transform = `translateX(${offset}%)`;
        }
    }

    // Cambia de imagen cada 3 segundos
    setInterval(showNextImages, 5000);
});