-- Create the "comp5590-a2" database
DROP DATABASE IF EXISTS comp5590_a2;
CREATE DATABASE IF NOT EXISTS comp5590_a2;
USE comp5590_a2;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS access_logs;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS migrations;

SET FOREIGN_KEY_CHECKS = 1;

-- Table for users
CREATE TABLE IF NOT EXISTS users (
    uid INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('patient', 'doctor', 'admin') NOT NULL
);

-- Table for doctors
CREATE TABLE IF NOT EXISTS doctors (
    did INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    background TEXT,
    uid INT,
    FOREIGN KEY (uid) REFERENCES users(uid)
);

-- Table for patients
CREATE TABLE IF NOT EXISTS patients (
    pid INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    uid INT,
    FOREIGN KEY (uid) REFERENCES users(uid)
);

-- Table for access logs
CREATE TABLE IF NOT EXISTS access_logs (
    lid INT AUTO_INCREMENT PRIMARY KEY,
    uid INT,
    action VARCHAR(255) NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (uid) REFERENCES users(uid)
);

-- Table for messages
CREATE TABLE IF NOT EXISTS messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT,
    receiver_id INT,
    message TEXT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(uid),
    FOREIGN KEY (receiver_id) REFERENCES users(uid)
);

-- Table for visits
CREATE TABLE IF NOT EXISTS visits (
    visit_id INT AUTO_INCREMENT PRIMARY KEY,
    pid INT,
    did INT,
    visit_date DATETIME NOT NULL,
    status ENUM('scheduled', 'cancelled', 'completed') DEFAULT 'scheduled',
    FOREIGN KEY (pid) REFERENCES patients(pid),
    FOREIGN KEY (did) REFERENCES doctors(did)
);

-- Table for migrations
CREATE TABLE IF NOT EXISTS migrations ( 
    id INT NOT NULL PRIMARY KEY, 
    timestamp TIMESTAMP NOT NULL 
);