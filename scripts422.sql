-- Таблица для машин
CREATE TABLE car (
                     id BIGSERIAL PRIMARY KEY,
                     brand VARCHAR(100) NOT NULL,
                     model VARCHAR(100) NOT NULL,
                     price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица для людей
CREATE TABLE person (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(200) NOT NULL,
                        age INTEGER NOT NULL CHECK (age >= 0),
                        has_license BOOLEAN NOT NULL DEFAULT false,
                        car_id BIGINT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_person_car FOREIGN KEY (car_id)
                            REFERENCES car(id) ON DELETE SET NULL
);

-- Индекс для ускорения поиска по машине
CREATE INDEX idx_person_car_id ON person(car_id);

-- Индекс для ускорения поиска по имени
CREATE INDEX idx_person_name ON person(name);