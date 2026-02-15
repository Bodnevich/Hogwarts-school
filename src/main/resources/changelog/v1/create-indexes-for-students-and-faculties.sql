sql
-- liquibase formatted sql

-- changes作者: batmanov
-- comment: Создание индексов для студентов и факультетов

-- Индекс для поиска по имени студента
CREATE INDEX IF NOT EXISTS idx_student_name ON student(name);

-- Индексы для поиска по названию и цвету факультета
CREATE INDEX IF NOT EXISTS idx_faculty_name ON faculty(name);
CREATE INDEX IF NOT EXISTS idx_faculty_color ON faculty(color);
