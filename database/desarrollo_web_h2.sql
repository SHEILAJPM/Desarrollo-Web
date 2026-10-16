-- =====================================================================
-- Compras, ventas y tributario: script portable para H2 (MODE=MySQL) y MySQL 8.
-- Ejecutar en http://localhost:8080/h2-console
--   JDBC URL: jdbc:h2:file:./data/desarrollo_web;MODE=MySQL;DATABASE_TO_LOWER=TRUE;AUTO_SERVER=TRUE
--   Usuario: sa   Clave: (vacía)
-- No toca las tablas de personal (trabajadores, marcaciones, documentos).
-- Dinero en DECIMAL(12,2); tasa en DECIMAL(5,4).
-- =====================================================================

DROP TABLE IF EXISTS compras;
DROP TABLE IF EXISTS ventas;
DROP TABLE IF EXISTS proveedores;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS configuracion_tributaria;

CREATE TABLE proveedores (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  ruc          VARCHAR(11)  NOT NULL,
  razon_social VARCHAR(150) NOT NULL,
  direccion    VARCHAR(200),
  PRIMARY KEY (id),
  CONSTRAINT uk_proveedores_ruc UNIQUE (ruc)
);

CREATE TABLE clientes (
  id             BIGINT       NOT NULL AUTO_INCREMENT,
  documento      VARCHAR(11)  NOT NULL,
  nombre         VARCHAR(150) NOT NULL,
  tipo_documento ENUM('DNI','RUC') NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_clientes_documento UNIQUE (documento)
);

CREATE TABLE compras (
  id                 BIGINT        NOT NULL AUTO_INCREMENT,
  proveedor_id       BIGINT        NOT NULL,
  numero_comprobante VARCHAR(20)   NOT NULL,
  fecha              DATE          NOT NULL,
  monto_sin_igv      DECIMAL(12,2) NOT NULL,
  igv                DECIMAL(12,2) NOT NULL,
  monto_total        DECIMAL(12,2) NOT NULL,
  tasa_aplicada      DECIMAL(5,4)  NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_compras_proveedor_numero UNIQUE (proveedor_id, numero_comprobante),
  CONSTRAINT fk_compras_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores (id)
);
CREATE INDEX idx_compras_fecha ON compras (fecha);

CREATE TABLE ventas (
  id                 BIGINT        NOT NULL AUTO_INCREMENT,
  cliente_id         BIGINT        NOT NULL,
  tipo_comprobante   ENUM('FACTURA','BOLETA') NOT NULL,
  numero_comprobante VARCHAR(20)   NOT NULL,
  fecha              DATE          NOT NULL,
  monto_sin_igv      DECIMAL(12,2) NOT NULL,
  igv                DECIMAL(12,2) NOT NULL,
  monto_total        DECIMAL(12,2) NOT NULL,
  tasa_aplicada      DECIMAL(5,4)  NOT NULL,
  estado             ENUM('EMITIDA','ANULADA') NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_ventas_numero UNIQUE (numero_comprobante),
  CONSTRAINT fk_ventas_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);
CREATE INDEX idx_ventas_fecha ON ventas (fecha);

CREATE TABLE configuracion_tributaria (
  id      BIGINT       NOT NULL AUTO_INCREMENT,
  codigo  VARCHAR(10)  NOT NULL,
  tasa    DECIMAL(5,4) NOT NULL,
  vigente BOOLEAN      NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_configuracion_codigo UNIQUE (codigo)
);

-- ---------------------------------------------------------------------
-- Datos de ejemplo. Agosto 2026: compra 100.00 (IGV 18.00) y venta
-- emitida 300.00 (IGV 54.00); la factura F001-1 de agosto está ANULADA.
-- /api/tributario/resumen-mensual?anio=2026&mes=8 debe dar:
--   crédito 18.00 · débito 54.00 · IGV por pagar 36.00
-- Los id se omiten para que AUTO_INCREMENT siga su secuencia.
-- ---------------------------------------------------------------------

INSERT INTO configuracion_tributaria (codigo, tasa, vigente) VALUES
  ('IGV', 0.1800, TRUE);

INSERT INTO proveedores (ruc, razon_social, direccion) VALUES
  ('20123456789', 'Distribuidora ACME S.A.C.', 'Av. Arequipa 1234, Lima'),
  ('20987654321', 'Suministros del Sur E.I.R.L.', NULL);

INSERT INTO clientes (documento, nombre, tipo_documento) VALUES
  ('10203040', 'Carlos Mendoza', 'DNI'),
  ('45678912', 'Lucía Paredes', 'DNI'),
  ('20555666777', 'Comercial Lima S.A.C.', 'RUC');

INSERT INTO compras (proveedor_id, numero_comprobante, fecha, monto_sin_igv, igv, monto_total, tasa_aplicada) VALUES
  ((SELECT id FROM proveedores WHERE ruc = '20123456789'), 'F001-101', '2026-08-05', 100.00, 18.00, 118.00, 0.1800),
  ((SELECT id FROM proveedores WHERE ruc = '20987654321'), 'F002-201', '2026-07-20', 250.00, 45.00, 295.00, 0.1800),
  ((SELECT id FROM proveedores WHERE ruc = '20123456789'), 'F001-102', '2026-09-03', 400.00, 72.00, 472.00, 0.1800);

INSERT INTO ventas (cliente_id, tipo_comprobante, numero_comprobante, fecha, monto_sin_igv, igv, monto_total, tasa_aplicada, estado) VALUES
  ((SELECT id FROM clientes WHERE documento = '10203040'),    'BOLETA',  'B001-1', '2026-08-10', 300.00, 54.00, 354.00, 0.1800, 'EMITIDA'),
  ((SELECT id FROM clientes WHERE documento = '20555666777'), 'FACTURA', 'F001-1', '2026-08-12', 200.00, 36.00, 236.00, 0.1800, 'ANULADA'),
  ((SELECT id FROM clientes WHERE documento = '45678912'),    'BOLETA',  'B001-2', '2026-07-15', 150.00, 27.00, 177.00, 0.1800, 'EMITIDA'),
  ((SELECT id FROM clientes WHERE documento = '20555666777'), 'FACTURA', 'F001-2', '2026-09-05', 500.00, 90.00, 590.00, 0.1800, 'EMITIDA');
