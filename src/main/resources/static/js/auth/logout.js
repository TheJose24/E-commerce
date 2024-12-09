document.addEventListener('DOMContentLoaded', function() {
    const logoutBtn = document.getElementById('logout-btn');

    if (logoutBtn) {
        logoutBtn.addEventListener('click', async function(e) {
            e.preventDefault();

            try {
                // Obtener el token del localStorage
                const token = localStorage.getItem('jwt_token');

                // Llamar al endpoint de logout
                const response = await fetch('/api/auth/logout', {
                    method: 'GET'
                });

                if (response.ok) {
                    // Limpiar localStorage
                    localStorage.removeItem('jwt_token');
                    localStorage.removeItem('user_role');
                    sessionStorage.clear();

                    // Redireccionar al login
                    window.location.href = '/login';
                } else {
                    console.error('Error al cerrar sesión');
                }
            } catch (error) {
                console.error('Error:', error);
            }
        });
    }
});