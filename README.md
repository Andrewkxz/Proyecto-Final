# AURA | Arquitectura para la Vida

Un proyecto web desarrollado para la materia Estructuras de Datos de la Universidad del Quindío. El sistema utiliza Grafos para modelar y visualizar las rutas lógicas de conectividad y redes dentro de los inmuebles.

---

## Características y Componentes

* **Catálogo de Exploración:** Visualización de apartamentos, casas y locales comerciales.
* **Módulo de Autenticación:** Control de acceso con opciones de Inicio de Sesión, Registro y Modo Invitado.
* **Simulación de Infraestructura:** Interfaz interactiva para personalizar acabados y servicios.
* **Modelado con Grafos:** Implementación de estructuras de datos para trazar la ruta óptima de la infraestructura de comunicación según las opciones elegidas.

---

## Tecnologías

* **Backend:** Java 17 y Spring Boot 3.
* **Frontend:** HTML, Thymeleaf y CSS.
* **Íconos y Fuentes:** Google Material Symbols y Plus Jakarta Sans.

---

## Cómo ejecutar el proyecto

1. Abre una terminal y ve a la carpeta del módulo:
   ```powershell
   cd c:\Users\juanj\IdeaProjects\Proyecto-Final\proptech
   ```
2. Compila y empaqueta la aplicación con Maven:
   ```powershell
   mvn clean package -DskipTests
   ```
   > Nota: si no tienes el wrapper completo en `proptech/.mvn`, usa la instalación de Maven del sistema.
3. Ejecuta la aplicación generada:
   ```powershell
   java -jar target\proptech-0.0.1-SNAPSHOT.jar
   ```
4. Alternativa para ejecutar desde Maven directamente:
   ```powershell
   mvn spring-boot:run
   ```
5. Abre el navegador en la URL donde se inicie la aplicación, normalmente:
   ```text
   http://localhost:8080
   ```

---

## Hecho por:

* **Jaider Andrés Melo Rodríguez**
* **Juliana Andrea Bustamante Niño**
* **Juan José Carvajal**
