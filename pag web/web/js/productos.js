// Cargar todos los productos en productos.html
fetch('http://localhost:8080/productos')
    .then(res => res.json())
    .then(productos => {
        const contenedor = document.getElementById('lista-productos');
        if (productos.length === 0) {
            contenedor.innerHTML = '<p>No hay productos disponibles.</p>';
            return;
        }
        productos.forEach(p => {
            contenedor.innerHTML += `
                <div class="producto-card">
                    <div style="padding: 15px;">
                        <h3>${p.nombre}</h3>
                        <p><strong>Marca:</strong> ${p.marca}</p>
                        <p><strong>Precio:</strong> $${p.precio.toLocaleString()}</p>
                        <p><strong>Stock:</strong> ${p.stock} unidades</p>
                        <button onclick="agregarAlCarrito('${p.id}', '${p.nombre}', ${p.precio})">
                            Agregar al carrito
                        </button>
                    </div>
                </div>`;
        });
    })
    .catch(err => {
        console.error('Error:', err);
        document.getElementById('lista-productos').innerHTML =
            '<p>Error cargando productos. ¿Está corriendo el servidor Java?</p>';
    });

function agregarAlCarrito(id, nombre, precio) {
    let carrito = JSON.parse(localStorage.getItem('carrito') || '[]');
    const existe = carrito.find(p => p.id === id);
    if (existe) {
        existe.cantidad++;
    } else {
        carrito.push({ id, nombre, precio, cantidad: 1 });
    }
    localStorage.setItem('carrito', JSON.stringify(carrito));
    alert(nombre + ' agregado al carrito ✓');
}