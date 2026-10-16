# Compras · Ventas · Tributario — guía de pruebas

Módulos refactorizados del curso **Desarrollo Web Integrado (UTP)**: proveedores, clientes, compras, ventas y tributario.
Base de datos de trabajo: **H2 en disco** (`backend/data/desarrollo_web`). Los tests usan H2 en memoria.

## 1. Levantar el backend

```bash
cd backend
./mvnw clean test        # 110 tests, no necesita ninguna base de datos externa
./mvnw spring-boot:run   # API en http://localhost:8080
```

Consola H2: <http://localhost:8080/h2-console>

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:file:./data/desarrollo_web;MODE=MySQL;DATABASE_TO_LOWER=TRUE;AUTO_SERVER=TRUE` |
| Usuario | `sa` |
| Clave | *(vacía)* |

Las tablas se crean solas (`ddl-auto=update`). Para cargar los datos de ejemplo, ejecuta en la consola el contenido de
`database/desarrollo_web_h2.sql`. Ese script borra y recrea solo las 5 tablas de este módulo; no toca las de personal.

## 2. Postman

1. Importa `Compras-Ventas-Tributario.postman_collection.json` y `Local.postman_environment.json`.
2. Selecciona el environment **Local**.
3. Abre el **Runner** y ejecuta la colección completa, en orden.

- La autenticación **Basic** está definida a nivel de colección, con las variables `usuario`/`clave` (admin / Admin123).
  Las peticiones de 401 y 403 la sobrescriben.
- Los scripts *post-response* guardan `proveedor_ruc`, `cliente_id`, `compra_id` y `venta_id`.
  Cada petición tiene al menos 2 `pm.test`.
- El RUC y el DNI se generan en cada corrida y los comprobantes usan `{{$timestamp}}`, así que el Runner se puede repetir.

| Caso | Petición |
|---|---|
| 201 + Location | Registrar proveedor, cliente, compra y venta |
| 400 | Compra con RUC y monto inválidos (`errores` por campo) · venta F para un DNI · mes 13 |
| 401 | `6 Seguridad / Sin credenciales` |
| 403 | `rrhh1 entra a compras` · `contabilidad1 cambia tasa IGV` · `administracion1 anula venta` |
| 404 | Compra, cliente o proveedor inexistente |
| 409 | Compra o venta con comprobante duplicado · anular dos veces · borrar un cliente con ventas |
| 415 | Compra enviada como `text/plain` |
| 500 + ROLLBACK | `Registrar compra con simularError` |

Equivalente por consola: `npx newman run Compras-Ventas-Tributario.postman_collection.json -e Local.postman_environment.json`

## 3. cURL en las tres terminales

Los JSON de ejemplo están en `postman/ejemplos/`. Ejecuta los comandos desde la carpeta `postman`.

### CMD (Windows) — continuación con `^`, comillas internas escapadas con `\"`

```bat
curl -i -u admin:Admin123 -X POST http://localhost:8080/api/proveedores ^
  -H "Content-Type: application/json" ^
  -d "{\"ruc\":\"20123456789\",\"razonSocial\":\"Distribuidora ACME S.A.C.\"}"

curl -i -u admin:Admin123 -X POST http://localhost:8080/api/compras ^
  -H "Content-Type: application/json" ^
  -d @ejemplos\compra.json

curl -i -u contabilidad1:Conta123 "http://localhost:8080/api/tributario/resumen?desde=2026-08-01&hasta=2026-08-31"
```

### PowerShell — usar `curl.exe` (no el alias `curl`), continuación con `` ` ``, cuerpo con `-d "@archivo.json"`

```powershell
curl.exe -i -u admin:Admin123 -X POST http://localhost:8080/api/clientes `
  -H "Content-Type: application/json" `
  -d '{\"documento\":\"10203040\",\"nombre\":\"Carlos Mendoza\"}'

curl.exe -i -u admin:Admin123 -X POST http://localhost:8080/api/ventas `
  -H "Content-Type: application/json" `
  -d "@ejemplos/venta.json"

curl.exe -i -u contabilidad1:Conta123 -X PATCH http://localhost:8080/api/ventas/1/anular
```

### Linux / macOS / Git Bash — continuación con `\`

```bash
curl -i -u admin:Admin123 -X POST http://localhost:8080/api/compras \
  -H "Content-Type: application/json" \
  -d '{"ruc":"20123456789","numeroComprobante":"F001-501","montoSinIgv":100.00,"fecha":"2026-08-10"}'

curl -i -u admin:Admin123 "http://localhost:8080/api/tributario/calcular?monto=118&incluyeIgv=true"

curl -i http://localhost:8080/api/compras            # 401 en JSON
curl -i -u rrhh1:Rrhh123 http://localhost:8080/api/compras   # 403 en JSON
```

## 4. Demostración de rollback

`CompraService.registrar` guarda la compra con `saveAndFlush`, de modo que el INSERT ya llega a la BD. Con `?simularError=true`
lanza una `RuntimeException` justo después. Como el método es `@Transactional`, Spring hace **ROLLBACK** y la fila desaparece.

```bash
# 1. Antes (en la consola H2)
SELECT COUNT(*) FROM compras;

# 2. Provocar el error (responde 500 con mensaje "ERROR SIMULADO...")
curl -i -u admin:Admin123 -X POST "http://localhost:8080/api/compras?simularError=true" \
  -H "Content-Type: application/json" \
  -d '{"ruc":"20123456789","numeroComprobante":"F009-1","montoSinIgv":999.00,"fecha":"2026-08-20"}'

# 3. Después: el mismo número que en el paso 1, y ninguna fila F009-1
SELECT COUNT(*) FROM compras;
SELECT * FROM compras WHERE numero_comprobante = 'F009-1';   -- 0 filas
```

La excepción se lanza **antes** de registrar el pago. `PagoService` sigue guardando en memoria y no participa de la
transacción, así que su registro no se desharía.

## 5. Matriz de seguridad (automatizada en `SecurityMatrizTest`)

| # | Usuario | Petición | Esperado |
|---|---|---|---|
| 1 | *(sin credenciales)* | GET /api/compras | 401 JSON |
| 2 | admin con clave incorrecta | GET /api/compras | 401 JSON |
| 3 | rrhh1 | GET /api/compras | 403 JSON |
| 4 | contabilidad1 | GET /api/compras | 200 |
| 5 | contabilidad1 | POST /api/compras | 403 |
| 6 | administracion1 | POST /api/proveedores | 201 |
| 7 | administracion1 | GET /api/ventas | 200 |
| 8 | administracion1 | PATCH /api/ventas/{id}/anular | 403 |
| 9 | contabilidad1 | PATCH /api/ventas/{id}/anular | 200 |
| 10 | administracion1 | GET /api/tributario/resumen | 403 |
| 11 | contabilidad1 | PUT /api/tributario/configuracion/IGV | 403 |
| 12 | admin | PUT /api/tributario/configuracion/IGV | 200 |

## 6. Ejemplo verificado a mano (agosto 2026)

| Concepto | Cálculo | Resultado |
|---|---|---|
| Compra S/ 100.00 | 100.00 × 0.18 | Crédito fiscal **18.00** |
| Venta S/ 300.00 | 300.00 × 0.18 | Débito fiscal **54.00** |
| IGV por pagar | 54.00 − 18.00 | **36.00** · saldo a favor 0.00 |

Con los datos de `database/desarrollo_web_h2.sql`: `GET /api/tributario/resumen-mensual?anio=2026&mes=8` (como
contabilidad1). La factura F001-1 de agosto está ANULADA y no suma.
