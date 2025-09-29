-- Создание таблицы для пользователей (Users)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,      -- Уникальное имя пользователя
    password VARCHAR(100) NOT NULL,            -- BCrypt хеш пароля
    email VARCHAR(255) NOT NULL UNIQUE,        -- Уникальный email
    role VARCHAR(20) NOT NULL DEFAULT 'USER',  -- Роль пользователя (USER, ADMIN)
    enabled BOOLEAN NOT NULL DEFAULT TRUE,     -- Активен ли аккаунт
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Дата создания
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
 --   total_sum DECIMAL(10, 2) NOT NULL,     -- Общая сумма заказа
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Дата оформления заказа
);

-- Создание таблицы для товаров в корзине (CartItem)
CREATE TABLE IF NOT EXISTS cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,                -- Идентификатор товара
    quantity INT NOT NULL,                  -- Количество товара в корзине
    orders_id BIGINT,                        -- Идентификатор заказа (связь с orders)
    price INT NOT NULL,                    --Цена на момент покупки
    FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE,  -- Связь с таблицей item
    FOREIGN KEY (orders_id) REFERENCES orders(id) ON DELETE CASCADE  -- Связь с таблицей orders
);