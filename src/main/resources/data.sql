-- ANIMALS
INSERT INTO animal (registration_number, weight, arrival_date, origin)
VALUES
    ('DK-0001', 500.0, CURRENT_DATE, 'Farm A'),
    ('DK-0002', 540.0, CURRENT_DATE, 'Farm B')
ON CONFLICT (registration_number) DO NOTHING;


-- TRAY
INSERT INTO tray (type, max_weight)
VALUES ('rib', 50.0);

-- PARTS
INSERT INTO part (weight, type, animal_id, tray_id)
VALUES
    (5.0, 'rib',
     (SELECT id FROM animal WHERE registration_number = 'DK-0001'),
     (SELECT id FROM tray WHERE type = 'rib')
    ),
    (4.5, 'rib',
     (SELECT id FROM animal WHERE registration_number = 'DK-0001'),
     (SELECT id FROM tray WHERE type = 'rib')
    );

-- PRODUCT
INSERT INTO product (kind)
VALUES ('Pack of ribs');

-- PRODUCT_PART links
INSERT INTO product_part (product_id, part_id)
VALUES
    (
        (SELECT id FROM product WHERE kind = 'Pack of ribs'),
        (SELECT id FROM part ORDER BY id LIMIT 1)
    ),
    (
        (SELECT id FROM product WHERE kind = 'Pack of ribs'),
        (SELECT id FROM part ORDER BY id OFFSET 1 LIMIT 1)
    )
ON CONFLICT DO NOTHING;
