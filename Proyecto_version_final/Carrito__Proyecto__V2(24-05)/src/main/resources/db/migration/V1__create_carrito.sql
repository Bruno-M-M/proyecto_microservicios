CREATE TABLE IF NOT EXISTS carrito (
                                       id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       cliente_id       BIGINT      NOT NULL,
                                       fecha_creacion   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       fechaConfirmacion DATETIME   NULL,
                                       estado           VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT chk_estado CHECK (estado IN ('PENDIENTE','PAGADO','CANCELADO'))
);
