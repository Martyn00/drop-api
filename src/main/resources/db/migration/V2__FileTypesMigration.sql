INSERT INTO file_types (id, is_active, type_name, uuid)
values (1, 1, 'directory', 'salut');
INSERT INTO file_types (id, is_active, type_name, uuid)
values (2, 1, 'text', 'genereated_auto');
UPDATE hibernate_sequence
SET next_val = 3;