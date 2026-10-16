-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: desarrollo_web
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `documentos`
--

DROP TABLE IF EXISTS `documentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `documentos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fecha_vencimiento` date NOT NULL,
  `tipo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `trabajador_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `marcaciones`
--

DROP TABLE IF EXISTS `marcaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `marcaciones` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fecha_hora` datetime(6) NOT NULL,
  `tipo` enum('ENTRADA','SALIDA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `trabajador_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trabajadores`
--

DROP TABLE IF EXISTS `trabajadores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trabajadores` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `area` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cargo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `documento_identidad` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombres` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- =====================================================================
-- Compras, ventas y tributario (JPA). Los nombres coinciden con las
-- entidades: Hibernate convierte razonSocial -> razon_social, etc.
-- Todo el dinero es DECIMAL(12,2); la tasa es DECIMAL(5,4).
-- =====================================================================

--
-- Table structure for table `proveedores`
--

DROP TABLE IF EXISTS `compras`;
DROP TABLE IF EXISTS `ventas`;
DROP TABLE IF EXISTS `proveedores`;
DROP TABLE IF EXISTS `clientes`;
DROP TABLE IF EXISTS `configuracion_tributaria`;

CREATE TABLE `proveedores` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ruc` varchar(11) COLLATE utf8mb4_unicode_ci NOT NULL,
  `razon_social` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_proveedores_ruc` (`ruc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Table structure for table `clientes`
--

CREATE TABLE `clientes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `documento` varchar(11) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_documento` enum('DNI','RUC') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_clientes_documento` (`documento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Table structure for table `compras`
--

CREATE TABLE `compras` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `proveedor_id` bigint NOT NULL,
  `numero_comprobante` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha` date NOT NULL,
  `monto_sin_igv` decimal(12,2) NOT NULL,
  `igv` decimal(12,2) NOT NULL,
  `monto_total` decimal(12,2) NOT NULL,
  `tasa_aplicada` decimal(5,4) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_compras_proveedor_numero` (`proveedor_id`,`numero_comprobante`),
  KEY `idx_compras_fecha` (`fecha`),
  CONSTRAINT `fk_compras_proveedor` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Table structure for table `ventas`
--

CREATE TABLE `ventas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cliente_id` bigint NOT NULL,
  `tipo_comprobante` enum('FACTURA','BOLETA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_comprobante` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha` date NOT NULL,
  `monto_sin_igv` decimal(12,2) NOT NULL,
  `igv` decimal(12,2) NOT NULL,
  `monto_total` decimal(12,2) NOT NULL,
  `tasa_aplicada` decimal(5,4) NOT NULL,
  `estado` enum('EMITIDA','ANULADA') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ventas_numero` (`numero_comprobante`),
  KEY `idx_ventas_fecha` (`fecha`),
  CONSTRAINT `fk_ventas_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Table structure for table `configuracion_tributaria`
--

CREATE TABLE `configuracion_tributaria` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tasa` decimal(5,4) NOT NULL,
  `vigente` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configuracion_codigo` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Datos de ejemplo
-- Agosto 2026: compra 100.00 (IGV 18.00) y venta emitida 300.00 (IGV 54.00).
-- La factura F001-1 de agosto está ANULADA: no cuenta en el resumen.
-- Resultado esperado de /api/tributario/resumen-mensual?anio=2026&mes=8:
--   crédito 18.00 · débito 54.00 · IGV por pagar 36.00
--

INSERT INTO `configuracion_tributaria` (`id`, `codigo`, `tasa`, `vigente`) VALUES
  (1, 'IGV', 0.1800, b'1');

INSERT INTO `proveedores` (`id`, `ruc`, `razon_social`, `direccion`) VALUES
  (1, '20123456789', 'Distribuidora ACME S.A.C.', 'Av. Arequipa 1234, Lima'),
  (2, '20987654321', 'Suministros del Sur E.I.R.L.', NULL);

INSERT INTO `clientes` (`id`, `documento`, `nombre`, `tipo_documento`) VALUES
  (1, '10203040', 'Carlos Mendoza', 'DNI'),
  (2, '45678912', 'Lucía Paredes', 'DNI'),
  (3, '20555666777', 'Comercial Lima S.A.C.', 'RUC');

INSERT INTO `compras` (`id`, `proveedor_id`, `numero_comprobante`, `fecha`, `monto_sin_igv`, `igv`, `monto_total`, `tasa_aplicada`) VALUES
  (1, 1, 'F001-101', '2026-08-05', 100.00, 18.00, 118.00, 0.1800),
  (2, 2, 'F002-201', '2026-07-20', 250.00, 45.00, 295.00, 0.1800),
  (3, 1, 'F001-102', '2026-09-03', 400.00, 72.00, 472.00, 0.1800);

INSERT INTO `ventas` (`id`, `cliente_id`, `tipo_comprobante`, `numero_comprobante`, `fecha`, `monto_sin_igv`, `igv`, `monto_total`, `tasa_aplicada`, `estado`) VALUES
  (1, 1, 'BOLETA',  'B001-1', '2026-08-10', 300.00, 54.00, 354.00, 0.1800, 'EMITIDA'),
  (2, 3, 'FACTURA', 'F001-1', '2026-08-12', 200.00, 36.00, 236.00, 0.1800, 'ANULADA'),
  (3, 2, 'BOLETA',  'B001-2', '2026-07-15', 150.00, 27.00, 177.00, 0.1800, 'EMITIDA'),
  (4, 3, 'FACTURA', 'F001-2', '2026-09-05', 500.00, 90.00, 590.00, 0.1800, 'EMITIDA');

--
-- Table structure for table `usuarios`
-- Usuarios del login. La contraseña se guarda cifrada con BCrypt;
-- el backend también crea estos 4 usuarios al arrancar si no existen.
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `activo` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuarios_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Claves: admin/Admin123 · rrhh1/Rrhh123 · contabilidad1/Conta123 · administracion1/AdminArea123
INSERT INTO `usuarios` (`id`, `username`, `password`, `rol`, `activo`) VALUES
  (1, 'admin', '$2a$10$ZYB9kNbqqXs0IBWw4mtdIulZE26ji/XP8MgBaQSxEDf53GWKn06ey', 'ADMIN', b'1'),
  (2, 'rrhh1', '$2a$10$unZbgE2T/LfYZ1V69pykAOg6v78JdpH/DyTKg./uvugBgyNu8f1xq', 'RRHH', b'1'),
  (3, 'contabilidad1', '$2a$10$7y9NXgmRE6mtCxASn67m1.X6OJ.BJovENBEgdwEjY/JZQq1cDq8N6', 'CONTABILIDAD', b'1'),
  (4, 'administracion1', '$2a$10$ki1rAIV8iuo0VvhHP3PmvO9Hk0iXZ8sWkwHoI6tWvI2D.I9hW7Bwa', 'ADMINISTRACION', b'1');

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-15 19:16:49
