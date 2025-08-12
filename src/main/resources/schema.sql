-- Создание таблицы для товаров (Item)
CREATE TABLE IF NOT EXISTS item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price INT NOT NULL,
    img_path VARCHAR(255) NOT NULL,
    count INT NOT NULL
);


-- Создание таблицы для заказов (Orders)
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
 --   total_sum DECIMAL(10, 2) NOT NULL,     -- Общая сумма заказа
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Дата оформления заказа
);

-- Создание таблицы для товаров в корзине (CartItem)
CREATE TABLE IF NOT EXISTS cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,                -- Идентификатор товара
    quantity INT NOT NULL,                  -- Количество товара в корзине
    orders_id BIGINT,                        -- Идентификатор заказа (связь с orders)
    FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE,  -- Связь с таблицей item
    FOREIGN KEY (orders_id) REFERENCES orders(id) ON DELETE CASCADE  -- Связь с таблицей orders
);