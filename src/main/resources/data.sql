INSERT INTO Usuario(id, email, password, rol, activo)
SELECT null, 'test@unlam.edu.ar', '$2a$10$rn1uKUVzVPklSLlF//VB6u5RaEbI5P7pkxPklYcxuWFURCWmq0jPW', 'ADMIN', true
    WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email = 'test@unlam.edu.ar');
