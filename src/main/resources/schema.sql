-- Создание таблицы для товаров (Item)
CREATE TABLE IF NOT EXISTS item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price INT NOT NULL,
    imgPath VARCHAR(255) NOT NULL,
    count INT NOT NULL
);

-- Вставка данных в таблицу item (товары)
INSERT INTO item (title, description, price, imgPath, count)
VALUES
    ('Кепка', 'Кепка синяя', 1000, 'cap.png', 20),
    ('Шапка', 'Шапка красная', 1200, 'hat.png', 15),
    ('Куртка', 'Куртка зимняя', 9800, 'jacket.png', 12),
    ('Шарф', 'Шарф женский', 1200, 'scarf.png', 12),
    ('Рубашка', 'Рубашка голубая', 2400, 'shirt.png', 15),
    ('Футболка', 'Футболка зелена', 2000, 't-shirt.png', 20),
    ('Майка', 'Майка черная', 1600, 'undershirt.png', 18);

-- Создание таблицы для заказов (Order)
CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
 --   total_sum DECIMAL(10, 2) NOT NULL,     -- Общая сумма заказа
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Дата оформления заказа
);

-- Создание таблицы для товаров в корзине (CartItem)
CREATE TABLE IF NOT EXISTS cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,                -- Идентификатор товара
    quantity INT NOT NULL,                  -- Количество товара в корзине
    order_id BIGINT,                        -- Идентификатор заказа (связь с order)
    FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE,  -- Связь с таблицей item
    FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE  -- Связь с таблицей order
);

-- Вставка данных в таблицу order (заказы)
INSERT INTO `order` (created_at)
VALUES
    ('2023-08-10 10:00:00'),  -- Заказ 1
    ('2023-08-10 14:30:00'),  -- Заказ 2
    ('2023-08-11 09:00:00');  -- Заказ 3

-- Вставка данных в таблицу cart_item (товары в корзине)
-- Для заказа 1
INSERT INTO cart_item (item_id, quantity, order_id)
VALUES
    (1, 2, 1),  -- 2 Кепки для заказа 1
    (3, 1, 1),  -- 1 Куртка для заказа 1
    (5, 1, 1);  -- 1 Рубашка для заказа 1

-- Для заказа 2
INSERT INTO cart_item (item_id, quantity, order_id)
VALUES
    (2, 1, 2),  -- 1 Шапка для заказа 2
    (4, 2, 2),  -- 2 Шарфа для заказа 2
    (6, 3, 2);  -- 3 Футболки для заказа 2

-- Для заказа 3
INSERT INTO cart_item (item_id, quantity, order_id)
VALUES
    (7, 1, 3),  -- 1 Майка для заказа 3
    (1, 1, 3);  -- 1 Кепка для заказа 3