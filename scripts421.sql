-- Возраст студента меньше 16
ALTER TABLE student
    ADD CONSTRAINT check_student_age CHECK (age >= 16);

--Уникальные имена и не равны null
ALTER TABLE student
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE student
    ADD CONSTRAINT unique_student_name UNIQUE (name);

--Уникальность цвета и названия факультета
ALTER TABLE faculty
    ADD CONSTRAINT unique_faculty_name_color UNIQUE (name, color);

--По умолчанию 20 лет
ALTER TABLE student
    ALTER COLUMN age SET DEFAULT 20;