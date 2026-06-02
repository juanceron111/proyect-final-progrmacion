const API = 'http://localhost:8080';

fetch(`${API}/productos`)
    .then(r => r.json())
    .then(productos => {
        document.getElementById('stat-total-productos').textContent = productos.length;
    })
    .catch(() => document.getElementById('stat-total-productos').textContent = 'Error');

fetch(`${API}/usuarios`)
    .then(r => r.json())
    .then(usuarios => {
        const vendedores = usuarios.filter(u => u.rol === 'VENDEDOR').length;
        document.getElementById('stat-usuarios').textContent = vendedores;
    })
    .catch(() => document.getElementById('stat-usuarios').textContent = 'Error');

fetch(`${API}/ventas`)
    .then(r => r.json())
    .then(ventas => {
        const mes = new Date().toISOString().slice(0, 7);
        const ingresos = ventas
            .filter(v => v.fecha && v.fecha.startsWith(mes))
            .reduce((sum, v) => sum + v.total, 0);
        document.getElementById('stat-ingresos-mes').textContent =
            '$' + ingresos.toLocaleString('es-CO');
    })
    .catch(() => document.getElementById('stat-ingresos-mes').textContent = 'Error');