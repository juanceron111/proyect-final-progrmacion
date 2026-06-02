const API = 'http://localhost:8080';
let productosEnVenta = [];
let todosLosProductos = [];

// Cargar productos al iniciar para poder buscarlos
fetch(`${API}/productos`)
    .then(r => r.json())
    .then(p => { todosLosProductos = p; })
    .catch(() => alert('Error cargando productos del servidor'));

function agregarProductoListado() {
    const idProd   = document.getElementById('inputBuscarProducto').value.trim();
    const cantidad = parseInt(document.getElementById('inputCantidad').value);

    if (!idProd || !cantidad || cantidad < 1) {
        alert('Ingresa un ID de producto y una cantidad válida');
        return;
    }

    const p = todosLosProductos.find(x => x.id === idProd);
    if (!p) {
        alert('Producto no encontrado. Verifica el ID.');
        return;
    }
    if (cantidad > p.stock) {
        alert('Stock insuficiente. Disponible: ' + p.stock);
        return;
    }

    const existe = productosEnVenta.find(x => x.id === idProd);
    if (existe) {
        existe.cantidad += cantidad;
    } else {
        productosEnVenta.push({ id: p.id, nombre: p.nombre, precio: p.precio, cantidad });
    }

    document.getElementById('inputBuscarProducto').value = '';
    document.getElementById('inputCantidad').value = '';
    actualizarTotal();
}

function actualizarTotal() {
    const total = productosEnVenta.reduce((s, p) => s + p.precio * p.cantidad, 0);
    document.getElementById('totalVentaUI').textContent =
        'Total: $' + total.toLocaleString('es-CO') + ' COP';
}

function confirmarVenta() {
    if (productosEnVenta.length === 0) {
        alert('Agrega al menos un producto a la venta');
        return;
    }

    const total    = productosEnVenta.reduce((s, p) => s + p.precio * p.cantidad, 0);
    const vendedor = localStorage.getItem('usuario') || 'vendedor';
    const tipo     = document.getElementById('metodoPago').value;

    fetch(`${API}/venta`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `idUsuario=${encodeURIComponent(vendedor)}&total=${total}&tipo=${encodeURIComponent(tipo)}`
    })
        .then(r => r.json())
        .then(data => {
            if (data.status === 'ok') {
                alert('¡Venta registrada con éxito!\nID: ' + data.idVenta + '\nTotal: $' + total.toLocaleString('es-CO'));
                productosEnVenta = [];
                actualizarTotal();
            } else {
                alert('Error al registrar la venta');
            }
        })
        .catch(() => alert('Error conectando al servidor'));
}