INSERT INTO file_mimes (id, file_mime)
VALUES (48, 'aplication/octet-stream');
INSERT INTO file_types_file_mimes (file_type_model_id, file_mime_id)
VALUES (2, 48);
UPDATE hibernate_sequence
SET next_val = 49;