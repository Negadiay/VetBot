CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    telegram_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    confirmation_code VARCHAR(50),
    code_expires_at TIMESTAMPTZ
);

CREATE TABLE service (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    room VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL
);

CREATE TABLE booking (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    service_id BIGINT NOT NULL REFERENCES service(id),
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    pet_name VARCHAR(255) NOT NULL,
    pet_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE booking_draft (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES app_user(id),
    service_id BIGINT REFERENCES service(id),
    appointment_date DATE,
    appointment_time TIME,
    pet_name VARCHAR(255),
    pet_type VARCHAR(50)
);