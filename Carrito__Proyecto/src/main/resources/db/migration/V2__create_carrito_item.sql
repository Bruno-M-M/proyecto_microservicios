CREATE TABLE IF NOT EXISTS carrito_item (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    carrito_id      BIGINT       NOT NULL,
    producto_id     BIGINT       NOT NULL,                     nombre_producto VARCHAR(100) NOT NULL DEFAULT '',
    cantidad        INT          NOT NULL DEFAULT 1,
    CONSTRAINT fk_item_carrito FOREIGN KEY (carrito_id)
    REFERENCES carrito(id) ON DELETE CASCADE,
    CONSTRAINT chk_item_cantidad CHECK (cantidad > 0)
    );
