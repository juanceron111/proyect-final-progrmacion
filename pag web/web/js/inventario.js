const API = 'http://localhost:8080';

fetch(`${API}/productos`)
    .then(r => r.json())
    .then(productos => {
        const tbody = document.querySelector('tbody');
        tbody.innerHTML = '';
        if (productos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5">No hay productos registrados</td></tr>';
            return;
        }
        productos.forEach(p => {
            let estado, color;
            if (p.stock === 0)      { estado = 'Agotado';    color = 'red'; }
            else if (p.stock <= 3)  { estado = 'Bajo Stock';  color = 'orange'; }
            else                    { estado = 'Disponible';  color = 'green'; }

            tbody.innerHTML += `
            <tr>
                <td>${p.id}</td>
                <td>${p.nombre}</td>
                <td>$${p.precio.toLocaleString('es-CO')}</td>
                <td>${p.stock}</td>
                <td style="color:${color}; font-weight:bold;">${estado}</td>
            </tr>`;
        });
    })
    .catch(() => {
        document.querySelector('tbody').innerHTML =
            '<tr><td colspan="5">Error cargando inventario. ¿Está el servidor corriendo?</td></tr>';
    });