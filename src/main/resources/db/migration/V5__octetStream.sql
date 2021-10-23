INSERT INTO file_mimes (id, file_mime)
VALUES (48, 'aplication/octet-stream');
INSERT INTO file_types_file_mimes (file_type_model_id, file_mime_id)
VALUES (2, 48);
INSERT INTO file_mimes (id, file_mime)
VALUES (49, 'image/png');
INSERT INTO file_types_file_mimes (file_type_model_id, file_mime_id)
VALUES (4, 49);
UPDATE hibernate_sequence
SET next_val = 50;