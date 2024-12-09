// authInterceptor.js
document.addEventListener("DOMContentLoaded", function() {
    const token = localStorage.getItem("jwt_token");
    const refreshToken = localStorage.getItem("refresh_token");

    if (token) {
        // Interceptar solicitudes fetch
        const originalFetch = window.fetch;
        window.fetch = async function(url, options = {}) {
            options.headers = options.headers || {};
            options.headers['Authorization'] = 'Bearer ' + token;

            let response = await originalFetch(url, options);

            if (response.status === 401 && refreshToken) {
                try {
                    const refreshResponse = await originalFetch('/api/auth/refresh', {
                        method: 'POST',
                        headers: {
                            'Authorization': 'Bearer ' + refreshToken
                        }
                    });

                    if (refreshResponse.ok) {
                        const newTokens = await refreshResponse.json();
                        localStorage.setItem('jwt_token', newTokens.accessToken);
                        localStorage.setItem('refresh_token', newTokens.refreshToken);

                        // Reintentar la solicitud original con el nuevo token
                        options.headers['Authorization'] = 'Bearer ' + newTokens.accessToken;
                        return originalFetch(url, options);
                    }
                } catch (error) {
                    console.error('Error refreshing token:', error);
                    window.location.href = '/login';
                }
            }

            return response;
        };

        // Interceptar formularios tradicionales
        document.querySelectorAll('form').forEach(form => {
            form.addEventListener('submit', function(event) {
                // No agregar input hidden, en su lugar modificar el header
                const formData = new FormData(this);

                event.preventDefault();

                fetch(this.action, {
                    method: this.method,
                    body: formData,
                    headers: {
                        'Authorization': 'Bearer ' + token
                    }
                })
                    .then(response => {
                        if (response.redirected) {
                            window.location.href = response.url;
                        }
                    })
                    .catch(error => {
                        console.error('Error en envío de formulario:', error);
                    });
            });
        });
    }
});