@echo off
rem compila lo de src y lo deja en bin (se necesita javac)
rem se compila para java 8 porque es el que esta instalado
setlocal
pushd "%~dp0"
if not exist bin mkdir bin
javac --release 8 -encoding UTF-8 -d bin src\*.java
if errorlevel 1 (
  echo Error de compilacion. Si javac no esta en el PATH, usa por ejemplo:
  echo   "C:\Program Files\JetBrains\CLion 2026.1.4\jbr\bin\javac.exe" --release 8 -encoding UTF-8 -d bin src\*.java
) else (
  echo Compilado en bin\
)
popd
