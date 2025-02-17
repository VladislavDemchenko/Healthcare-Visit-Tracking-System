CREATE DATABASE healthcare;
USE healthcare;

CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    timezone VARCHAR(50) NOT NULL
);

CREATE TABLE visits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_date_time DATETIME NOT NULL,
    end_date_time DATETIME NOT NULL,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    INDEX idx_doctor_datetime (doctor_id, start_date_time, end_date_time),
    INDEX idx_patient_doctor (patient_id, doctor_id)
);

-- Insert test doctors
INSERT INTO doctors (first_name, last_name, timezone) VALUES
('John', 'Smith', 'America/New_York'),
('Maria', 'Garcia', 'America/Los_Angeles'),
('David', 'Kim', 'Asia/Tokyo');

-- Insert test patients
INSERT INTO patients (first_name, last_name) VALUES
('Alice', 'Johnson'),
('Bob', 'Williams'),
('Carol', 'Davis'),
('Daniel', 'Brown');

-- Insert test visits
INSERT INTO visits (start_date_time, end_date_time, patient_id, doctor_id) VALUES
('2024-02-16 09:00:00', '2024-02-16 10:00:00', 1, 1),
('2024-02-16 11:00:00', '2024-02-16 12:00:00', 2, 1),
('2024-02-16 14:00:00', '2024-02-16 15:00:00', 1, 2),
('2024-02-16 16:00:00', '2024-02-16 17:00:00', 3, 2),
('2024-02-16 09:00:00', '2024-02-16 10:00:00', 4, 3),
('2024-02-16 11:00:00', '2024-02-16 12:00:00', 1, 3);