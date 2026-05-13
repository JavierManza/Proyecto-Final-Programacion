-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS biblioteca_municipal;
USE biblioteca_municipal;

-- 1. Tabla raíz de usuarios (OBLIGATORIA)
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    rol ENUM('BIBLIOTECARIO', 'SOCIO') NOT NULL DEFAULT 'SOCIO'
);

-- 2. Tabla hija: Socios (Joined Table Inheritance)
CREATE TABLE socios (
    usuario_id INT PRIMARY KEY,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    fecha_alta DATE DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_socio_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 3. Tabla hija: Bibliotecarios (Joined Table Inheritance)
CREATE TABLE bibliotecarios (
    usuario_id INT PRIMARY KEY,
    num_empleado VARCHAR(20) UNIQUE,
    turno ENUM('MAÑANA', 'TARDE', 'NOCHE'),
    CONSTRAINT fk_biblio_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 4. Tabla entidad principal: Libros
CREATE TABLE libros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    sinopsis TEXT,
    portada_url VARCHAR(255),
    ejemplares_totales INT DEFAULT 1,
    ejemplares_disponibles INT DEFAULT 1
);

-- 5. Tabla N:M: Prestamos (Relación Usuarios-Libros)
CREATE TABLE prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    socio_id INT NOT NULL,
    libro_id INT NOT NULL,
    fecha_prestamo DATE NOT NULL DEFAULT (CURRENT_DATE),
    fecha_devolucion_prevista DATE NOT NULL,
    fecha_devolucion_real DATE,
    estado ENUM('ACTIVO', 'DEVUELTO', 'RETRASADO') DEFAULT 'ACTIVO',
    CONSTRAINT fk_prestamo_socio FOREIGN KEY (socio_id) REFERENCES socios(usuario_id) ON DELETE CASCADE,
    CONSTRAINT fk_prestamo_libro FOREIGN KEY (libro_id) REFERENCES libros(id) ON DELETE CASCADE
);

-- 6. Tabla N:M: Reservas
CREATE TABLE reservas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    socio_id INT NOT NULL,
    libro_id INT NOT NULL,
    fecha_reserva DATE NOT NULL DEFAULT (CURRENT_DATE),
    estado ENUM('PENDIENTE', 'COMPLETADA', 'CANCELADA') DEFAULT 'PENDIENTE',
    CONSTRAINT fk_reserva_socio FOREIGN KEY (socio_id) REFERENCES socios(usuario_id) ON DELETE CASCADE,
    CONSTRAINT fk_reserva_libro FOREIGN KEY (libro_id) REFERENCES libros(id) ON DELETE CASCADE
);

-- Insertar datos de prueba
INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) VALUES 
('bibliotecario1', 'bibliotecario1', 'biblio@correo.es', 'Pedro', 'Picapiedra', '12345678A', 'BIBLIOTECARIO'),
('socio1', 'socio123', 'juan@correo.com', 'Juan', 'Pérez', '87654321B', 'SOCIO');

INSERT INTO bibliotecarios (usuario_id, num_empleado, turno) VALUES (1, 'BIB-001', 'MAÑANA');
INSERT INTO socios (usuario_id, direccion, telefono) VALUES (2, 'Calle Mayor 1', '600123456');

INSERT INTO libros (isbn, titulo, autor, ejemplares_totales, ejemplares_disponibles) VALUES 
('9788424116293', 'El Quijote', 'Miguel de Cervantes', 5, 5),
('9788433920157', 'Cien años de soledad', 'Gabriel García Márquez', 3, 3);
