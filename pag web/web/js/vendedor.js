const API = 'http://localhost:8080';

function cargarUsuarios() {
    fetch(`${API}/usuarios`)
        .then(r => r.json())
        .then(usuarios => {
            const tbody = document.getElementById('tabla-usuarios-admin');
            tbody.innerHTML = '';
            usuarios.forEach(u => {
                tbody.innerHTML += `
                <tr>
                    <td>${u.id}</td>
                    <td>${u.nombre}</td>
                    <td>${u.email}</td>
                    <td>${u.rol}</td>
                    <td>
                        <button onclick="eliminarUsuario('${u.id}')">Eliminar</button>
                    </td>
                </tr>`;
            });
        })
        .catch(() => alert('Error cargando usuarios'));
}

function guardarUsuario() {
    const nombre = document.getElementById('userNombre').value.trim();
    const email  = document.getElementById('userEmail').value.trim();
    const pass   = document.getElementById('userPass').value.trim();
    const rol    = document.getElementById('userRol').value;

    if (!nombre || !email || !pass) {
        alert('Completa todos los campos');
        return;
    }

    const id = 'U' + Date.now();
    fetch(`${API}/usuario`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `id=${id}&nombre=${encodeURIComponent(nombre)}&email=${encodeURIComponent(email)}&password=${encodeURIComponent(pass)}&rol=${rol}`
    })
        .then(r => r.json())
        .then(data => {
            if (data.status === 'ok') {
                alert('Usuario registrado correctamente');
                document.getElementById('userNombre').value = '';
                document.getElementById('userEmail').value = '';
                document.getElementById('userPass').value = '';
                cargarUsuarios();
            } else {
                alert('Error: ' + (data.mensaje || 'No se pudo registrar'));
            }
        })
        .catch(() => alert('Error conectando al servidor'));
}

function eliminarUsuario(id) {
    if (!confirm('¿Seguro que deseas eliminar este usuario?')) return;
    fetch(`${API}/usuario/eliminar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `id=${encodeURIComponent(id)}`
    })
        .then(r => r.json())
        .then(data => {
            alert(data.status === 'ok' ? 'Usuario eliminado' : 'Error al eliminar');
            cargarUsuarios();
        })
        .catch(() => alert('Error conectando al servidor'));
}

cargarUsuarios();