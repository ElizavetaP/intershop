-- Вставка тестовых пользователей
-- Пароли: admin = "admin", user = "user" (BCrypt хеши)
INSERT INTO users (username, password, email, role, enabled)
VALUES
    ('admin', '$2a$10$tDKcw0euHFiF.098uiXreez7tlDeJJAjE7ThU9XKu8DMt7uTJ5zV2', 'admin@intershop.com', 'ADMIN', true),
    ('user', '$2a$10$qpnSOWHT5j8POV1VGMvqaeB3LYtEzx/xQ.nT/43xZCkf6ZweqI7hG', 'user@intershop.com', 'USER', true);

-- Вставка данных в таблицу item (товары)
INSERT INTO item (title, description, price, img_path)
VALUES
    ('Кепка', 'Кепка синяя', 1000, 'cap.png'),
    ('Шапка', 'Шапка красная', 1200, 'hat.png'),
    ('Куртка', 'Куртка зимняя', 9800, 'jacket.png'),
    ('Шарф', 'Шарф женский', 1200, 'scarf.png'),
    ('Рубашка', 'Рубашка голубая', 2400, 'shirt.png'),
    ('Футболка', 'Футболка зеленая', 2000, 't-shirt.png'),
    ('Майка', 'Майка черная', 1600, 'undershirt.png');

-- Вставка данных в таблицу order (заказы)
INSERT INTO orders (created_at)
VALUES
    ('2023-08-10 10:00:00'),  -- Заказ 1
    ('2023-08-10 14:30:00'),  -- Заказ 2
    ('2023-08-11 09:00:00');  -- Заказ 3

-- Вставка данных в таблицу cart_item (товары в корзине)
-- Для заказа 1
INSERT INTO cart_item (item_id, quantity, orders_id, price)
VALUES
    (1, 2, 1, 1000),  -- 2 Кепки для заказа 1
    (3, 1, 1, 9800),  -- 1 Куртка для заказа 1
    (5, 1, 1, 2400);  -- 1 Рубашка для заказа 1

-- Для заказа 2
INSERT INTO cart_item (item_id, quantity, orders_id, price)
VALUES
    (2, 1, 2, 1200),  -- 1 Шапка для заказа 2
    (4, 2, 2, 1200),  -- 2 Шарфа для заказа 2
    (6, 3, 2, 2000);  -- 3 Футболки для заказа 2

-- Для заказа 3
INSERT INTO cart_item (item_id, quantity, orders_id, price)
VALUES
    (7, 1, 3, 1600),  -- 1 Майка для заказа 3
    (1, 1, 3, 1000);  -- 1 Кепка для заказа 3