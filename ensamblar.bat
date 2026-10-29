@echo off
rem Uso: ensamblar.bat Prog.asm    ->  genera Prog.hack al lado
java -cp "%~dp0bin" HackAssembler %*
