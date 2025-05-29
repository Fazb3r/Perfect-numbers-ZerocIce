@echo off
echo === COMPILACION PASO A PASO ===
echo.

echo 1. Verificando estructura de directorios...
if not exist "numbers-ZerocIce.ice" (
    echo ERROR: No se encuentra el archivo numbers-ZerocIce.ice
    echo Debe estar en la raiz del proyecto numbers-ZerocIce/
    pause
    exit /b 1
)

echo 2. Limpiando builds anteriores...
call gradlew clean

echo.
echo 3. Intentando compilar solo el archivo Slice...
call gradlew compileSliceJava

echo.
echo 4. Verificando si se generaron las clases...
if exist "cliente\build\generated\src\main\java\NumbersApp" (
    echo [OK] Clases generadas correctamente
    dir "cliente\build\generated\src\main\java\NumbersApp"
) else (
    echo [ERROR] No se generaron las clases ICE
    echo Usando versiones simplificadas temporalmente...
)

echo.
echo 5. Compilando codigo Java...
call gradlew compileJava

echo.
echo 6. Construyendo JARs...
call gradlew build

if %ERRORLEVEL% EQU 0 (
    echo.
    echo === COMPILACION EXITOSA ===
    echo.
    echo Para ejecutar:
    echo 1. Maestro:  gradlew :maestro:run
    echo 2. Trabajador: gradlew :trabajadores:run --args="1"
    echo 3. Cliente:  gradlew :cliente:run
) else (
    echo.
    echo === ERROR EN COMPILACION ===
    echo Revise los mensajes de error anteriores
)

echo.
pause