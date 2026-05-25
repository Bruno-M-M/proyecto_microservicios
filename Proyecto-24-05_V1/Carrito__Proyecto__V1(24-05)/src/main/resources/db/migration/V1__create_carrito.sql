CREATE TABLE IF NOT EXISTS carrito (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT       NOT NULL,
    producto_id BIGINT      NOT NULL,
    nombre_producto VARCHAR(100) NOT NULL DEFAULT '',
    cantidad   INT          NOT NULL DEFAULT 1,
    fechaConfirmacion DATETIME NULL,
    estado     VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT chk_estado CHECK (estado IN ('PENDIENTE','PAGADO','CANCELADO')),
    CONSTRAINT chk_cantidad CHECK (cantidad > 0)
);
