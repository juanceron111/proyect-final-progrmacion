function login() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    if (!email || !password) {
        alert('Por favor completa todos los campos');
        return;
    }

    fetch('http://localhost:8080/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`
    })
        .then(res => res.json())
        .then(data => {
            if (data.status === 'ok') {
                localStorage.setItem('usuario', data.nombre);
                localStorage.setItem('rol', data.rol);

                if (data.rol === 'administrador') {
                    window.location.href = 'admin/admin.html';
                } else if (data.rol === 'VENDEDOR') {
                    window.location.href = 'vendedor/vendedor.html';
                }
            } else {
                alert('Correo o contraseña incorrectos');
            }
        })
        .catch(err => {
            console.error('Error:', err);
            alert('Error conectando al servidor');
        });
}