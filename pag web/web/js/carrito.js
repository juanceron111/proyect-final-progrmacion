// Mostrar productos del carrito
function mostrarCarrito() {
    const carrito = JSON.parse(localStorage.getItem('carrito') || '[]');
    const contenedor = document.getElementById('carrito');

    if (carrito.length === 0) {
        contenedor.innerHTML = '<p>Tu carrito está vacío.</p>';
        return;
    }

    let total = 0;
    let html = '';

    carrito.forEach((p, index) => {
        const subtotal = p.precio * p.cantidad;
        total += subtotal;
        html += `
            <div style="border-bottom: 1px solid #eee; padding: 10px 0;">
                <strong>${p.nombre}</strong><br>
                Cantidad: ${p.cantidad} x $${p.precio.toLocaleString()}
                = $${subtotal.toLocaleString()}
                <button onclick="eliminarDelCarrito(${index})"
                    style="background:transparent; color:red; 
                           border:none; cursor:pointer; font-size:12px;">
                    Eliminar
                </button>
            </div>`;
    });

    html += `<div style="margin-top:15px; font-size:18px;">
                <strong>Total: $${total.toLocaleString()}</strong>
             </div>`;

    contenedor.innerHTML = html;
}

function eliminarDelCarrito(index) {
    let carrito = JSON.parse(localStorage.getItem('carrito') || '[]');
    carrito.splice(index, 1);
    localStorage.setItem('carrito', JSON.stringify(carrito));
    mostrarCarrito();
}

function comprar() {
    const carrito = JSON.parse(localStorage.getItem('carrito') || '[]');
    if (carrito.length === 0) {
        alert('Tu carrito está vacío');
        return;
    }

    const usuario = localStorage.getItem('usuario') || 'invitado';
    const total = carrito.reduce((sum, p) => sum + p.precio * p.cantidad, 0);

    fetch('http://localhost:8080/venta', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `idUsuario=${encodeURIComponent(usuario)}&total=${total}&tipo=online`
    })
        .then(res => res.json())
        .then(data => {
            if (data.status === 'ok') {
                alert('Compra realizada con éxito! ID: ' + data.idVenta);
                localStorage.removeItem('carrito');
                window.location.href = 'index.html';
            } else {
                alert('Error al procesar la compra');
            }
        })
        .catch(err => {
            console.error('Error:', err);
            alert('Error conectando al servidor');
        });
}

// Cargar carrito al abrir la página
mostrarCarrito();