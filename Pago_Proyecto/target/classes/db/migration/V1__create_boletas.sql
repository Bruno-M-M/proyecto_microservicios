CREATE TABLE IF NOT EXISTS boletas (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    clienteId        BIGINT        NOT NULL,
    clienteNombre    VARCHAR(255)  NOT NULL,
    clienteRun       VARCHAR(20)   NOT NULL,
    clienteCorreo    VARCHAR(255)  NOT NULL,
    clienteDireccion VARCHAR(255)  NOT NULL,
    clienteTelefono  INT           NOT NULL,
    tipoPago         VARCHAR(20)   NOT NULL,
    totalNeto        DOUBLE        NOT NULL,
    iva              DOUBLE        NOT NULL,
    totalConIva      DOUBLE        NOT NULL,
    fechaEmision     DATETIME      NOT NULL,
    estado           VARCHAR(20)   NOT NULL DEFAULT 'EMITIDA',
    pedidosIds       VARCHAR(1000) NOT NULL,
    CONSTRAINT chk_tipo_pago CHECK (tipoPago IN ('EFECTIVO','DEBITO','CREDITO','TRANSFERENCIA')),
    CONSTRAINT chk_estado_boleta CHECK (estado IN ('EMITIDA','ANULADA'))
);
