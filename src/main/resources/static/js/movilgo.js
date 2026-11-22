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
