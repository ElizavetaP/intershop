-- Очистка таблиц перед вставкой тестовых данных
DELETE FROM cart_item;
DELETE FROM orders;
DELETE FROM item;

-- Сброс автоинкремента
ALTER TABLE cart_item ALTER COLUMN id RESTART WITH 1;
ALTER TABLE orders ALTER COLUMN id RESTART WITH 1;
ALTER TABLE item ALTER COLUMN id RESTART WITH 1;

-- Тестовые товары
INSERT INTO item (title, description, price, img_path) VALUES
('Кепка синяя', 'Описание кепки синего цвета для тестов', 1000, 'cap.png'),
('Шапка красная', 'Описание шапки красного цвета для тестов', 1200, 'hat.png'),
('Куртка зимняя', 'Теплая куртка для зимы тестовая', 9800, 'jacket.png'),
('Футболка зеленая', 'Зеленая футболка для тестирования', 2000, 't-shirt.png'),
('Шарф женский', 'Женский шарф для тестов', 1200, 'scarf.png');

-- Тестовые заказы
INSERT INTO orders (created_at) VALUES
('2023-08-10 10:00:00'),
('2023-08-10 14:30:00');

-- Элементы корзины для заказов (используем подзапросы для получения правильных ID)
INSERT INTO cart_item (item_id, quantity, orders_id, price) 
SELECT 
    (SELECT id FROM item WHERE title = 'Кепка синяя' LIMIT 1),
    2,
    (SELECT id FROM orders WHERE created_at = '2023-08-10 10:00:00' LIMIT 1),
    1000;

INSERT INTO cart_item (item_id, quantity, orders_id, price) 
SELECT 
    (SELECT id FROM item WHERE title = 'Куртка зимняя' LIMIT 1),
    1,
    (SELECT id FROM orders WHERE created_at = '2023-08-10 10:00:00' LIMIT 1),
    9800;

INSERT INTO cart_item (item_id, quantity, orders_id, price) 
SELECT 
    (SELECT id FROM item WHERE title = 'Шапка красная' LIMIT 1),
    1,
    (SELECT id FROM orders WHERE created_at = '2023-08-10 14:30:00' LIMIT 1),
    1200;

INSERT INTO cart_item (item_id, quantity, orders_id, price) 
SELECT 
    (SELECT id FROM item WHERE title = 'Шарф женский' LIMIT 1),
    2,
    (SELECT id FROM orders WHERE created_at = '2023-08-10 14:30:00' LIMIT 1),
    1200;

-- Товары в корзине (без заказа)
INSERT INTO cart_item (item_id, quantity, orders_id, price) 
SELECT 
    (SELECT id FROM item WHERE title = 'Кепка синяя' LIMIT 1),
    3,
    NULL,
    1000;

INSERT INTO cart_item (item_id, quantity, orders_id, price) 
SELECT 
    (SELECT id FROM item WHERE title = 'Футболка зеленая' LIMIT 1),
    1,
    NULL,
    2000;