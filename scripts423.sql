-- Получить всю информацию
SELECT s.name AS student_name,
       s.age AS student_age,
       f.name AS faculty_name
FROM student s
         LEFT JOIN faculty f ON s.faculty_id = f.id
ORDER BY s.name;

-- Получить людей с аватарками
SELECT s.name AS student_name,
       s.age AS student_age,
       f.name AS faculty_name,
       a.media_type AS avatar_type,
       a.file_size AS avatar_size
FROM student s
         INNER JOIN avatar a ON s.id = a.student_id
         LEFT JOIN faculty f ON s.faculty_id = f.id
ORDER BY s.name;

-- Получить только с аватарками
SELECT DISTINCT s.name AS student_name
FROM student s
         INNER JOIN avatar a ON s.id = a.student_id
ORDER BY s.name;