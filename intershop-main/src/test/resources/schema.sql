-- Создание таблицы для пользователей (Users)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы для товаров (Item)
CREATE TABLE IF NOT EXISTS item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price INT NOT NULL,
    img_path VARCHAR(255) NOT NULL
);


-- Создание таблицы для заказов (Orders)
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,              -- Пользователь, создавший заказ
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Дата оформления заказа
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

-- Индекс для быстрого поиска заказов пользователя
CREATE INDEX IF NOT EXISTS idx_orders_username ON orders(username);

-- Создание таблицы для товаров в корзине (CartItem)
CREATE TABLE IF NOT EXISTS cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,                -- Идентификатор товара
    quantity INT NOT NULL,                  -- Количество товара в корзине
    orders_id BIGINT,                        -- Идентификатор заказа (связь с orders)
    username VARCHAR(50),
    price INT NOT NULL,                    --Цена на момент покупки
    FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE,
    FOREIGN KEY (orders_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_cart_item_username ON cart_item(username);
CREATE INDEX IF NOT EXISTS idx_cart_item_orders_id ON cart_item(orders_id);