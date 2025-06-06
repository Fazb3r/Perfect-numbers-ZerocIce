
# 💻 Búsqueda Distribuida de Números Perfectos – Java

Este proyecto implementa un sistema distribuido basado en **ICE** (Internet Communications Engine) para la búsqueda de **números perfectos** en un rango dado utilizando el modelo **Cliente-Maestro-Trabajadores**.

---

## 📁 Estructura del proyecto

```
PerfectNumbersDistributed/
│
├── maestro/
│   └── Maestro.java
│
├── trabajador/
│   └── Trabajador.java
│
├── cliente/
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

### 1. Compilar las interfaces Slice

Primero, debes compilar los archivos **Slice** generados por ICE. Dirígete al directorio donde se encuentra el archivo **`numbers-ZerocIce.ice`** y ejecuta:

```bash
slice2java -I. numbers-ZerocIce.ice --output-dir ./src/main/java
```

Esto generará las clases necesarias para las interfaces ICE en el directorio `./src/main/java`.

---

### 2. Compilar los archivos Java

Desde la raíz del proyecto, compila los archivos Java:

```bash
javac -d bin maestro/*.java trabajador/*.java cliente/*.java
```

---

### 3. Ejecutar el servidor (Maestro)

En una terminal nueva, ejecuta el **Maestro** con el siguiente comando:

```bash
java -cp bin Maestro
```

Deberías ver:

```
Maestro iniciado, esperando conexiones de trabajadores y clientes...
```

---

### 4. Ejecutar un cliente

En una terminal diferente (puedes abrir varias terminales para simular varios clientes), ejecuta el **Cliente**:

```bash
java -cp bin Cliente
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
