const API = 'http://localhost:8080';

function cargarProductos() {
    fetch(`${API}/productos`)
        .then(r => r.json())
        .then(productos => {
            const tbody = document.getElementById('tabla-productos-admin');
            tbody.innerHTML = '';
            productos.forEach(p => {
                tbody.innerHTML += `
                <tr>
                    <td>${p.id}</td>
                    <td>${p.nombre}</td>
                    <td>$${p.precio.toLocaleString('es-CO')}</td>
                    <td>${p.stock}</td>
                    <td>
                        <button onclick="eliminarProducto('${p.id}')">Eliminar</button>
                    </td>
                </tr>`;
            });
        })
        .catch(() => alert('Error cargando productos. ¿Está el servidor corriendo?'));
}

function guardarProducto() {
    const nombre = document.getElementById('prodNombre').value.trim();
    const precio = document.getElementById('prodPrecio').value.trim();
    const stock  = document.getElementById('prodStock').value.trim();

    if (!nombre || !precio || !stock) {
        alert('Completa todos los campos');
        return;
    }

    const id = 'P' + Date.now();
    fetch(`${API}/producto`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `id=${id}&nombre=${encodeURIComponent(nombre)}&marca=Sin+Marca&precio=${precio}&stock=${stock}&categoria=General`
    })
        .then(r => r.json())
        .then(data => {
            if (data.status === 'ok') {
                alert('Producto guardado correctamente');
                document.getElementById('prodNombre').value = '';
                document.getElementById('prodPrecio').value = '';
                document.getElementById('prodStock').value = '';
                cargarProductos();
            } else {
                alert('Error al guardar el producto');
            }
        })
        .catch(() => alert('Error conectando al servidor'));
}

function eliminarProducto(id) {
    if (!confirm('¿Seguro que deseas eliminar este producto?')) return;
    fetch(`${API}/producto/eliminar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `id=${encodeURIComponent(id)}`
    })
        .then(r => r.json())
        .then(data => {
            alert(data.status === 'ok' ? 'Producto eliminado' : 'Error al eliminar');
            cargarProductos();
        })
        .catch(() => alert('Error conectando al servidor'));
}

cargarProductos();