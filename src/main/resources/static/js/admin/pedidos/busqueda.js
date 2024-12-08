document.querySelector('.search-bar button').addEventListener('click', function() {
    const query = document.querySelector('.search-bar input').value.trim();
    if (query) {
        window.location.href = `/admin/pedidosAdmin/buscar?query=${encodeURIComponent(query)}`;
    }
});