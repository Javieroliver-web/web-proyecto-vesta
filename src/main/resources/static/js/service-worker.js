self.addEventListener('push', function(event) {
    const data = event.data.json();
    
    const options = {
        body: data.mensaje,
        icon: '/img/vesta-icon.png',
        badge: '/img/badge.png',
        vibrate: [200, 100, 200],
        data: {
            url: data.url,
            tipo: data.tipo // 'vencimiento', 'recomendacion', 'siniestro'
        },
        actions: [
            {action: 'ver', title: 'Ver Detalles'},
            {action: 'renovar', title: 'Renovar Ahora'}
        ]
    };
    
    event.waitUntil(
        self.registration.showNotification('Vesta Seguros', options)
    );
});