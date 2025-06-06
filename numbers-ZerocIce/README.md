
# 💻 Búsqueda Distribuida de Números Perfectos – Java

Este proyecto implementa un sistema distribuido basado en **ICE** (Internet Communications Engine) para la búsqueda de **números perfectos** en un rango dado utilizando el modelo **Cliente-Maestro-Trabajadores**.

---

## 📁 Estructura del proyecto

```
PerfectNumbersDistributed/
│
├── maestro/src
│   └── Maestro.java
│
├── trabajador/src
│   └── Trabajador.java
│
├── cliente/src
│   └── Cliente.java
│
├── numbers-ZerocIce.ice
├── README.md
└── run.sh / run.bat
```

---

## ⚙️ Requisitos

- Java JDK 8 o superior  
- Editor de código como VSCode o IntelliJ  
- ICE (Internet Communications Engine) instalado  
- Compilador `javac` y ejecutor `java` configurados en tu `PATH`

---
## ▶️ Instrucciones para ejecutar

Para ejecutar el sistema completo distribuido, sigue estos pasos:

### 1. **Compilacion del proyecto**

Compila el proyecto ejecutando el siguiente comando en la terminal:

   ```
   gradle build
   ```

### 2. Ejecutar el servidor (Maestro):
En la primera terminal, ejecuta el **Maestro** con el siguiente comando:
   ```
   gradle :maestro:run
   ```
Deberías ver:
    ```
    Maestro iniciado, esperando conexiones de trabajadores y clientes...
    ```


### 3. **Ejecutar los trabajadores**
Ejecuta los Trabajadores en una segunda terminal(puedes abrir varias terminales para simular varios Trabajadores):

   ```
   gradle :trabajadores:run
   ```

    
### 4. **Ejecutar un cliente**
En una terminal diferente (puedes abrir varias terminales para simular varios clientes), ejecuta el **Cliente**:

   ```
   gradle :cliente:run
   ```

Se te pedirá ingresar el rango para la búsqueda de números perfectos:

```
Ingrese el número inicial del rango: 1
Ingrese el número final del rango: 1000
```

El cliente enviará la solicitud al Maestro y mostrará el resultado de los números perfectos encontrados.


---

## 🧑‍💻 Menú del cliente

Una vez conectado, el cliente mostrará un menú:

```
MENU:
1. Ingresar rango para búsqueda de números perfectos
2. Salir
Elige opción:
```

### Opción 1 – Ingresar rango para búsqueda de números perfectos

- Ingresa el número inicial y final del rango de búsqueda.
- El cliente enviará esta solicitud al **Maestro**, que dividirá el trabajo entre los **trabajadores**.

### Opción 2 – Salir

- Termina la ejecución del cliente.

---
