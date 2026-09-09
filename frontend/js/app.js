console.log("app.js cargado");

const API_URL = "http://localhost:8080";


/* PERSONAL - REGISTRAR TRABAJADOR*/

const formularioTrabajador =
    document.querySelector("#formTrabajador");

if (formularioTrabajador) {

    formularioTrabajador.addEventListener("submit", async function (event) {

        event.preventDefault();

        const trabajador = {
            nombres: document.querySelector("#nombres").value,
            documentoIdentidad: document.querySelector("#documentoIdentidad").value,
            cargo: document.querySelector("#cargo").value,
            area: document.querySelector("#areaTrabajador").value,
            estado: "Activo"
        };

        try {

            const respuesta = await fetch(
                `${API_URL}/api/personal`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(trabajador)
                }
            );

            if (!respuesta.ok) {
                throw new Error(
                    "No se pudo registrar el trabajador"
                );
            }

            const trabajadorCreado =
                await respuesta.json();

            alert("Trabajador registrado correctamente");

            formularioTrabajador.reset();

            cargarTrabajadores();

            console.log(
                "Trabajador creado:",
                trabajadorCreado
            );

        } catch (error) {

            console.error(error);

            alert(
                "No se pudo registrar el trabajador."
            );
        }
    });
}


/*PERSONAL - LISTAR TRABAJADORES*/

async function cargarTrabajadores() {

    const lista =
        document.querySelector("#listaTrabajadores");

    if (!lista) {
        return;
    }

    try {

        const respuesta =
            await fetch(`${API_URL}/api/personal`);

        if (!respuesta.ok) {
            throw new Error(
                "Error al obtener los trabajadores"
            );
        }

        const trabajadores =
            await respuesta.json();

        lista.innerHTML = "";

        if (trabajadores.length === 0) {

            lista.innerHTML = `
                <tr>
                    <td colspan="5">
                        No hay trabajadores registrados.
                    </td>
                </tr>
            `;

            return;
        }

        trabajadores.forEach(function (trabajador) {

            const fila =
                document.createElement("tr");

            fila.innerHTML = `
                <td>${trabajador.id}</td>
                <td>${trabajador.nombres}</td>
                <td>${trabajador.area}</td>
                <td>${trabajador.cargo}</td>
                <td>${trabajador.estado}</td>
            `;

            lista.appendChild(fila);
        });

    } catch (error) {

        console.error(error);

        lista.innerHTML = `
            <tr>
                <td colspan="5">
                    No se pudieron cargar los trabajadores.
                </td>
            </tr>
        `;
    }
}


/*  ASISTENCIA*/

const formulario =
    document.querySelector("#formAsistencia");

if (formulario) {

    formulario.addEventListener("submit", async function (event) {

        event.preventDefault();

        const trabajadorId =
            document.querySelector("#trabajadorId").value;

        const resultado =
            document.querySelector("#resultadoAsistencia");

        try {

            const respuesta = await fetch(
                `${API_URL}/api/asistencia/trabajador/${trabajadorId}`
            );

            if (!respuesta.ok) {
                throw new Error(
                    "Error al consultar la asistencia"
                );
            }

            const asistencias =
                await respuesta.json();

            console.log(
                "Asistencias:",
                asistencias
            );

            if (asistencias.length === 0) {

                resultado.innerHTML = `
                    <article>
                        <p>
                            No hay registros de asistencia
                            para este trabajador.
                        </p>
                    </article>
                `;

                return;
            }

            let contenido = `
                <article>

                    <h3>Registros de asistencia</h3>

                    <table>

                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tipo</th>
                                <th>Fecha y hora</th>
                                <th>Latitud</th>
                                <th>Longitud</th>
                            </tr>
                        </thead>

                        <tbody>
            `;

            asistencias.forEach(function (asistencia) {

                contenido += `
                    <tr>
                        <td>${asistencia.id}</td>
                        <td>${asistencia.tipo}</td>
                        <td>
                            ${new Date(
                    asistencia.fechaHora
                ).toLocaleString("es-PE")}
                        </td>
                        <td>${asistencia.latitud}</td>
                        <td>${asistencia.longitud}</td>
                    </tr>
                `;
            });

            contenido += `
                        </tbody>

                    </table>

                </article>
            `;

            resultado.innerHTML = contenido;

        } catch (error) {

            console.error(error);

            resultado.innerHTML = `
                <article>
                    <p>
                        No se pudo consultar
                        la asistencia.
                    </p>
                </article>
            `;
        }
    });
}


/* DOCUMENTOS - REGISTRAR*/

const formularioDocumento =
    document.querySelector("#formDocumento");

if (formularioDocumento) {

    formularioDocumento.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();

            const documento = {

                documentoIdentidad:
                    document.querySelector(
                        "#documentoIdentidadDocumento"
                    ).value,

                tipo:
                    document.querySelector(
                        "#tipoDocumento"
                    ).value,

                fechaVencimiento:
                    document.querySelector(
                        "#fechaVencimientoDocumento"
                    ).value
            };

            try {

                const respuesta = await fetch(
                    `${API_URL}/api/documentos`,
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body:
                            JSON.stringify(documento)
                    }
                );

                if (!respuesta.ok) {

                    const mensaje =
                        await respuesta.text();

                    console.error(
                        "Error de API:",
                        mensaje
                    );

                    throw new Error(
                        "No se pudo registrar el documento"
                    );
                }

                const documentoCreado =
                    await respuesta.json();

                console.log(
                    "Documento creado:",
                    documentoCreado
                );

                alert(
                    "Documento registrado correctamente"
                );

                formularioDocumento.reset();

                cargarDocumentos();
                cargarDocumentosPorVencer();

            } catch (error) {

                console.error(error);

                alert(
                    "No se pudo registrar el documento."
                );
            }
        }
    );
}


/* =========================================
   DOCUMENTOS - LISTAR
   ========================================= */

async function cargarDocumentos() {

    const lista =
        document.querySelector("#listaDocumentos");

    if (!lista) {
        return;
    }

    try {

        const respuesta =
            await fetch(
                `${API_URL}/api/documentos`
            );

        if (!respuesta.ok) {

            throw new Error(
                "Error al obtener los documentos"
            );
        }

        const documentos =
            await respuesta.json();

        lista.innerHTML = "";

        if (documentos.length === 0) {

            lista.innerHTML = `
                <tr>
                    <td colspan="5">
                        No hay documentos registrados.
                    </td>
                </tr>
            `;

            return;
        }

        documentos.forEach(function (documento) {

            const fila =
                document.createElement("tr");

            const fechaVencimiento =
                new Date(
                    documento.fechaVencimiento +
                    "T00:00:00"
                );

            const fechaActual =
                new Date();

            let estado = "Vigente";

            if (fechaVencimiento < fechaActual) {

                estado = "Vencido";

            } else {

                const diferencia =
                    fechaVencimiento - fechaActual;

                const dias =
                    Math.ceil(
                        diferencia /
                        (1000 * 60 * 60 * 24)
                    );

                if (dias <= 30) {
                    estado = "Próximo a vencer";
                }
            }

            const fechaFormateada =
                fechaVencimiento.toLocaleDateString(
                    "es-PE"
                );

            fila.innerHTML = `
                <td>${documento.id}</td>
                <td>${documento.tipo}</td>
                <td>${documento.trabajadorId}</td>
                <td>${fechaFormateada}</td>
                <td>${estado}</td>
            `;

            lista.appendChild(fila);
        });

    } catch (error) {

        console.error(error);

        lista.innerHTML = `
            <tr>
                <td colspan="5">
                    No se pudieron cargar
                    los documentos.
                </td>
            </tr>
        `;
    }
}
/* =========================================
   DOCUMENTOS - PRÓXIMOS A VENCER
   ========================================= */

async function cargarDocumentosPorVencer() {

    const lista =
        document.querySelector("#listaDocumentosPorVencer");

    if (!lista) return;

    try {

        const respuesta = await fetch(
            `${API_URL}/api/documentos/proximos-a-vencer?dias=30`
        );

        if (!respuesta.ok) {
            throw new Error(
                "Error al obtener documentos por vencer"
            );
        }

        const documentos = await respuesta.json();

        lista.innerHTML = "";

        if (documentos.length === 0) {

            lista.innerHTML = `
                <tr>
                    <td colspan="5">
                        No hay documentos próximos a vencer.
                    </td>
                </tr>
            `;

            return;
        }

        documentos.forEach(function (documento) {

            const fila =
                document.createElement("tr");

            const fecha =
                new Date(
                    documento.fechaVencimiento + "T00:00:00"
                );

            fila.innerHTML = `
                <td>${documento.id}</td>
                <td>${documento.tipo}</td>
                <td>${documento.trabajadorId}</td>
                <td>
                    ${fecha.toLocaleDateString("es-PE")}
                </td>
                <td>Próximo a vencer</td>
            `;

            lista.appendChild(fila);
        });

    } catch (error) {

        console.error(error);

        lista.innerHTML = `
            <tr>
                <td colspan="5">
                    No se pudieron cargar los documentos
                    por vencer.
                </td>
            </tr>
        `;
    }
}

/* =========================================
   INICIAR APLICACIÓN
   ========================================= */

cargarTrabajadores();
cargarDocumentos();
cargarDocumentosPorVencer();