-- ============================================
--  ANIMALS (2 animals for trace tests)
-- ============================================
INSERT INTO animal(id, registration_number, weight, arrival_date, origin)
VALUES
    ('1f1c7e6f-6cae-4fe0-9d83-3f7d4d7daa01', 'DK-0001', 500.0, CURRENT_DATE, 'Farm A'),   -- main animal
    ('5b8e8c9f-8f9c-4c96-8249-ccd2548508e1', 'DK-0002', 540.0, CURRENT_DATE, 'Farm B')    -- second animal
    ON CONFLICT DO NOTHING;


-- ============================================
--  TRAY (one tray for ribs)
-- ============================================
INSERT INTO tray(id, type, max_weight)
VALUES
    ('4d43b999-c97e-4cf9-bcf7-854e25042d58', 'rib', 50.0)   -- rib tray
    ON CONFLICT DO NOTHING;


-- ============================================
--  PARTS (2 parts from animal DK-0001)
-- ============================================
INSERT INTO part(id, weight, type, animal_id, tray_id)
VALUES
    ('8b446b26-68f2-4f6c-aa29-ac0360f912cb', 5.0, 'rib',
     '1f1c7e6f-6cae-4fe0-9d83-3f7d4d7daa01',    -- DK-0001
     '4d43b999-c97e-4cf9-bcf7-854e25042d58'),   -- rib tray

    ('b88cf59d-cb82-40e3-b649-426749b9c821', 4.5, 'rib',
     '1f1c7e6f-6cae-4fe0-9d83-3f7d4d7daa01',    -- DK-0001
     '4d43b999-c97e-4cf9-bcf7-854e25042d58')    -- rib tray
    ON CONFLICT DO NOTHING;


-- ============================================
--  PRODUCT (contains the two rib parts)
-- ============================================
INSERT INTO product(id, kind)
VALUES
    ('a6ba5d74-bbb1-4ac2-80f2-e24d0a08703b', 'Pack of ribs')   -- product for test
    ON CONFLICT DO NOTHING;


-- ============================================
--  PRODUCT -> PART RELATION
-- ============================================
INSERT INTO product_part(product_id, part_id)
VALUES
    ('a6ba5d74-bbb1-4ac2-80f2-e24d0a08703b', '8b446b26-68f2-4f6c-aa29-ac0360f912cb'), -- link part 1
    ('a6ba5d74-bbb1-4ac2-80f2-e24d0a08703b', 'b88cf59d-cb82-40e3-b649-426749b9c821')  -- link part 2
    ON CONFLICT DO NOTHING;
