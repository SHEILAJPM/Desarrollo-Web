/* AUTENTICACIÓN Y PERMISOS POR ROL
   Se carga en el <head> de cada página, antes que app.js. */

const AUTH_API_URL = "http://localhost:8080";
const CLAVE_SESION = "sesion";

/* Qué roles pueden entrar a cada página
   admin: todo · rrhh: Personal · contabilidad: Ventas y Pagos
   administracion: Ventas y Proveedores */
const PERMISOS_PAGINA = {
    "personal.html": ["ADMIN", "RRHH"],
    "compras.html": ["ADMIN"],
    "ventas.html": ["ADMIN", "ADMINISTRACION", "CONTABILIDAD"],
    "pagos.html": ["ADMIN", "CONTABILIDAD"],
    "proveedores.html": ["ADMIN", "ADMINISTRACION"],
    "usuarios.html": ["ADMIN"]
};

/* Roles que pueden registrar/modificar en páginas donde otros solo consultan */
const PERMISOS_ESCRITURA = {
    "ventas.html": ["ADMIN", "ADMINISTRACION"]
};

/* Formularios de registro que se ocultan a quien solo puede consultar */
const FORMULARIOS_ESCRITURA = ["#formCompra", "#formVenta", "#formProveedor"];

const NOMBRES_ROL = {
    ADMIN: "Administrador",
    RRHH: "Recursos Humanos",
    CONTABILIDAD: "Contabilidad",
    ADMINISTRACION: "Administración"
};


/* SESIÓN */

function obtenerSesion() {
    try {
        return JSON.parse(sessionStorage.getItem(CLAVE_SESION));
    } catch (error) {
        return null;
    }
}

function guardarSesion(sesion) {
    sessionStorage.setItem(CLAVE_SESION, JSON.stringify(sesion));
}

function cerrarSesion() {
    sessionStorage.removeItem(CLAVE_SESION);
    window.location.replace("login.html");
}

function paginaActual() {
    const nombre = window.location.pathname.split("/").pop();
    return nombre || "index.html";
}

function puedeVerPagina(pagina, rol) {
    const roles = PERMISOS_PAGINA[pagina];
    return !roles || roles.includes(rol);
}

function puedeEscribir(pagina, rol) {
    const roles = PERMISOS_ESCRITURA[pagina];
    return !roles || roles.includes(rol);
}


/* FETCH CON CREDENCIALES
   Toda llamada al backend lleva Authorization: Basic automáticamente. */

const fetchOriginal = window.fetch.bind(window);

window.fetch = async function (recurso, opciones = {}) {
    const url = typeof recurso === "string" ? recurso : recurso.url;

    if (!url.startsWith(AUTH_API_URL)) {
        return fetchOriginal(recurso, opciones);
    }

    const sesion = obtenerSesion();
    const headers = new Headers(opciones.headers || {});

    // Evita la ventana nativa de usuario/clave del navegador
    headers.set("X-Requested-With", "XMLHttpRequest");

    if (sesion && !headers.has("Authorization")) {
        headers.set("Authorization", `Basic ${sesion.token}`);
    }

    const respuesta = await fetchOriginal(recurso, { ...opciones, headers });

    if (respuesta.status === 401 && paginaActual() !== "login.html") {
        cerrarSesion();
    }

    return respuesta;
};


/* PROTECCIÓN DE PÁGINAS */

(function protegerPagina() {
    const pagina = paginaActual();

    if (pagina === "login.html") {
        return;
    }

    const sesion = obtenerSesion();

    if (!sesion) {
        window.location.replace("login.html");
        return;
    }

    if (!puedeVerPagina(pagina, sesion.rol)) {
        window.location.replace("index.html?denegado=1");
    }
})();


/* INTERFAZ SEGÚN EL ROL */

document.addEventListener("DOMContentLoaded", function () {
    const pagina = paginaActual();
    const sesion = obtenerSesion();

    if (pagina === "login.html" || !sesion) {
        return;
    }

    // Enlaces del menú a los que el rol no tiene acceso
    document.querySelectorAll("nav a").forEach(function (enlace) {
        const destino = enlace.getAttribute("href");

        if (!puedeVerPagina(destino, sesion.rol)) {
            enlace.remove();
        }
    });

    // Tarjetas de módulos en el inicio
    document.querySelectorAll(".tarjeta a[href$='.html']").forEach(function (enlace) {
        if (!puedeVerPagina(enlace.getAttribute("href"), sesion.rol)) {
            enlace.closest(".tarjeta").remove();
        }
    });

    // Usuario conectado y botón de salida
    const nav = document.querySelector("nav");

    if (nav) {
        const usuario = document.createElement("div");
        usuario.className = "nav-usuario";
        usuario.innerHTML = `
            <span>${sesion.usuario} · ${NOMBRES_ROL[sesion.rol] || sesion.rol}</span>
            <button type="button" id="btnCerrarSesion">Cerrar sesión</button>
        `;
        nav.appendChild(usuario);

        document
            .querySelector("#btnCerrarSesion")
            .addEventListener("click", cerrarSesion);
    }

    // Modo solo lectura: se ocultan formularios de registro y botones de edición
    if (!puedeEscribir(pagina, sesion.rol)) {
        document.body.classList.add("solo-lectura");

        FORMULARIOS_ESCRITURA.forEach(function (selector) {
            const formulario = document.querySelector(selector);

            if (formulario) {
                const aviso = document.createElement("p");
                aviso.className = "aviso-permiso";
                aviso.textContent =
                    "Tu rol solo tiene permiso de consulta en este módulo.";
                formulario.replaceWith(aviso);
            }
        });
    }

    // Aviso cuando se intentó entrar a una página no permitida
    if (new URLSearchParams(window.location.search).has("denegado")) {
        const main = document.querySelector("main");

        if (main) {
            const aviso = document.createElement("p");
            aviso.className = "aviso-permiso";
            aviso.textContent = "No tienes permiso para acceder a ese módulo.";
            main.prepend(aviso);
        }
    }
});
