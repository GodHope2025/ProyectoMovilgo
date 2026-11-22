// MovilGo Perú: interacciones del navegador (mostrar contraseña, requisitos y medios de pago)
document.querySelectorAll('[data-mostrar-clave]').forEach(function (boton) {
  boton.addEventListener('click', function () {
    var campo = document.getElementById(boton.getAttribute('data-mostrar-clave'));
    var visible = campo.type === 'text';
    campo.type = visible ? 'password' : 'text';
    boton.querySelector('i').className = visible ? 'bi bi-eye' : 'bi bi-eye-slash';
    boton.setAttribute('aria-label', visible ? 'Mostrar contraseña' : 'Ocultar contraseña');
  });
});

var clave = document.getElementById('clave');
var reglas = document.querySelectorAll('[data-regla]');
if (clave && reglas.length) {
  var pruebas = {
    largo: function (v) { return v.length >= 8 && v.length <= 64 && !/\s/.test(v); },
    mayuscula: function (v) { return /[A-Z]/.test(v); },
    minuscula: function (v) { return /[a-z]/.test(v); },
    numero: function (v) { return /\d/.test(v); },
    simbolo: function (v) { return /[^A-Za-z0-9\s]/.test(v); }
  };
  var confirmar = document.getElementById('confirmarClave');
  var aviso = document.getElementById('avisoCoinciden');
  var revisar = function () {
    reglas.forEach(function (li) {
      var cumple = pruebas[li.getAttribute('data-regla')](clave.value);
      li.classList.toggle('ok', cumple);
      li.querySelector('i').className = cumple ? 'bi bi-check-circle-fill me-1' : 'bi bi-circle me-1';
    });
    if (confirmar && aviso) {
      aviso.hidden = !confirmar.value || confirmar.value === clave.value;
    }
  };
  clave.addEventListener('input', revisar);
  if (confirmar) { confirmar.addEventListener('input', revisar); }
  revisar();
}

document.querySelectorAll('input[name="metodo"]').forEach(function (radio) {
  var mostrar = function () {
    document.querySelectorAll('[data-detalle-metodo]').forEach(function (d) {
      d.hidden = d.getAttribute('data-detalle-metodo') !== document.querySelector('input[name="metodo"]:checked').value;
    });
  };
  radio.addEventListener('change', mostrar);
  mostrar();
});

// Animaciones de la portada: los bloques aparecen al hacer scroll y las cifras cuentan hacia arriba
var bloques = document.querySelectorAll('.mg-aparecer');
if ('IntersectionObserver' in window && bloques.length) {
  var observador = new IntersectionObserver(function (entradas) {
    entradas.forEach(function (e) {
      if (e.isIntersecting) { e.target.classList.add('visible'); observador.unobserve(e.target); }
    });
  }, { threshold: 0.15 });
  bloques.forEach(function (b, i) { b.style.transitionDelay = (i % 4) * 0.08 + 's'; observador.observe(b); });
} else {
  bloques.forEach(function (b) { b.classList.add('visible'); });
}
document.querySelectorAll('[data-contar]').forEach(function (el) {
  var meta = parseInt(el.getAttribute('data-contar'), 10);
  var pre = el.getAttribute('data-prefijo') || '';
  var suf = el.getAttribute('data-sufijo') || '';
  if (isNaN(meta) || window.matchMedia('(prefers-reduced-motion: reduce)').matches) { return; }
  var inicio = null;
  var paso = function (t) {
    if (!inicio) { inicio = t; }
    var avance = Math.min((t - inicio) / 1200, 1);
    el.textContent = pre + Math.round(meta * avance) + suf;
    if (avance < 1) { requestAnimationFrame(paso); }
  };
  requestAnimationFrame(paso);
});

// Catálogo: filtrar la flota por tipo de vehículo sin recargar la página
var chips = document.querySelectorAll('[data-filtro]');
var contador = document.getElementById('contadorFlota');
var filtrar = function (tipo) {
  var visibles = 0;
  document.querySelectorAll('[data-categoria]').forEach(function (c) {
    var mostrar = !tipo || c.getAttribute('data-categoria').indexOf(tipo) >= 0;
    c.hidden = !mostrar;
    if (mostrar) { visibles++; }
  });
  if (contador) { contador.textContent = visibles + (visibles === 1 ? ' vehículo' : ' vehículos'); }
};
chips.forEach(function (chip) {
  chip.addEventListener('click', function () {
    chips.forEach(function (c) { c.classList.remove('activo'); });
    chip.classList.add('activo');
    filtrar(chip.getAttribute('data-filtro'));
  });
});
if (chips.length) { filtrar(''); }
