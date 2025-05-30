@echo off
echo === SOLUCION PASO A PASO PARA ZEROC ICE ===
echo.

echo 1. Limpiando builds anteriores...
call gradlew clean

echo.
echo 2. Creando directorios necesarios...
if not exist "build\generated\src\main\java" mkdir build\generated\src\main\java
if not exist "cliente\build\generated\src\main\java" mkdir cliente\build\generated\src\main\java
if not exist "maestro\build\generated\src\main\java" mkdir maestro\build\generated\src\main\java
if not exist "trabajadores\build\generated\src\main\java" mkdir trabajadores\build\generated\src\main\java

echo.
echo 3. Intentando compilacion Slice con Gradle...
call gradlew compileSliceJava

echo.
echo 4. Verificando clases generadas...
if exist "build\generated\src\main\java\NumbersApp" (
    echo [OK] Clases ICE generadas correctamente
    dir "build\generated\src\main\java\NumbersApp"
) else (
    echo [WARNING] Gradle no genero las clases. Intentando compilacion manual...
    
    REM Try manual compilation if slice2java is available
    slice2java --output-dir build/generated/src/main/java numbers-ZerocIce.ice
    
    if exist "build\generated\src\main\java\NumbersApp" (
        echo [OK] Compilacion manual exitosa
    ) else (
        echo [ERROR] No se pudieron generar las clases ICE
        echo Verifique que ZeroC Ice este instalado correctamente
        pause
        exit /b 1
    )
)

echo.
echo 5. Copiando clases generadas a subproyectos...
if exist "build\generated\src\main\java\NumbersApp" (
    xcopy "build\generated\src\main\java\NumbersApp" "cliente\build\generated\src\main\java\NumbersApp\" /E /I /Y
    xcopy "build\generated\src\main\java\NumbersApp" "maestro\build\generated\src\main\java\NumbersApp\" /E /I /Y
    xcopy "build\generated\src\main\java\NumbersApp" "trabajadores\build\generated\src\main\java\NumbersApp\" /E /I /Y
    echo [OK] Clases copiadas a todos los subproyectos
)

echo.
echo 6. Compilando codigo Java...
call gradlew compileJava

echo.
echo 7. Construyendo JARs...
call gradlew build

if %ERRORLEVEL% EQU 0 (
    echo.
    echo === COMPILACION EXITOSA ===
    echo.
    echo Para ejecutar el sistema:
    echo.
    echo Terminal 1 - Maestro:
    echo   gradlew :maestro:run
    echo.
    echo Terminal 2 - Trabajador:
    echo   gradlew :trabajadores:run --args="1"
    echo.
    echo Terminal 3 - Cliente:
    echo   gradlew :cliente:run
    echo.
) else (
    echo.
    echo === ERROR EN COMPILACION ===
    echo Revise los mensajes de error anteriores
    echo.
    echo Problemas comunes:
    echo - ZeroC Ice no instalado correctamente
    echo - JAVA_HOME no configurado
    echo - Problemas de permisos en directorios
)

echo.
pause