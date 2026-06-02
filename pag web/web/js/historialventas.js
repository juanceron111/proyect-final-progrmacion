const API = 'http://localhost:8080';

fetch(`${API}/ventas`)
    .then(r => r.json())
    .then(ventas => {
        const tbody = document.querySelector('tbody');
        tbody.innerHTML = '';
        if (ventas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5">No hay ventas registradas aún</td></tr>';
            return;
        }
        // Mostrar más recientes primero
        ventas.reverse().forEach(v => {
            tbody.innerHTML += `
            <tr>
                <td>${v.id}</td>
                <td>${v.idUsuario}</td>
                <td>${v.fecha}</td>
                <td>$${v.total.toLocaleString('es-CO')}</td>
                <td>${v.tipo}</td>
            </tr>`;
        });
    })
    .catch(() => {
        document.querySelector('tbody').innerHTML =
            '<tr><td colspan="5">Error cargando historial. ¿Está el servidor corriendo?</td></tr>';
    });