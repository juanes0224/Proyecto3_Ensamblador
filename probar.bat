@echo off
rem pruebas del proyecto 3:
rem   1) ensambla los .asm de tests\asm y los compara con tests\referencia
rem      (esos los saque con el ensamblador oficial de nand2tetris)
rem   2) desensambla cada .hack, lo vuelve a ensamblar y mira
rem      que de el mismo binario
setlocal enabledelayedexpansion
pushd "%~dp0"
set FALLAS=0
if not exist tests\desensamblado mkdir tests\desensamblado

echo ===== 1. Ensamblador vs. ensamblador oficial =====
for %%F in (tests\asm\*.asm) do (
  java -cp bin HackAssembler "%%F" >nul
  fc /W "tests\asm\%%~nF.hack" "tests\referencia\%%~nF.hack" >nul
  if errorlevel 1 ( echo   FALLA  %%~nF & set /a FALLAS+=1 ) else ( echo   OK     %%~nF )
)

echo ===== 2. Desensamblador (ida y vuelta) =====
for %%F in (tests\referencia\*.hack) do (
  java -cp bin HackDisassembler "%%F" "tests\desensamblado\%%~nF.asm" >nul
  java -cp bin HackAssembler "tests\desensamblado\%%~nF.asm" >nul
  fc /W "tests\desensamblado\%%~nF.hack" "%%F" >nul
  if errorlevel 1 ( echo   FALLA  %%~nF & set /a FALLAS+=1 ) else ( echo   OK     %%~nF )
)

echo.
if !FALLAS!==0 ( echo Todas las pruebas pasaron. ) else ( echo Pruebas fallidas: !FALLAS! )
popd
exit /b !FALLAS!
