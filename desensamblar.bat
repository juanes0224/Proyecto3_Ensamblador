@echo off
rem Uso: desensamblar.bat [-d] Prog.hack [Salida.asm]   ->  por defecto genera Prog.asm al lado
java -cp "%~dp0bin" HackDisassembler %*
