# Proyecto 3 - Ensamblador y Desensamblador Hack

Organización y Arquitectura de Computadores - Clase 18, capítulo 6 de Nand2Tetris.

## Estudiantes
- Jerónimo Gutierrez
- Juan Esteban Grisales

## Qué hace
- `HackAssembler`: pasa de `Prog.asm` a `Prog.hack`
- `HackDisassembler`: pasa de `Prog.hack` a `Prog.asm`

## Carpetas
```text
src/             Parser, Code, SymbolTable, HackAssembler, HackDisassembler
bin/             clases compiladas
tests/asm/       programas de prueba
tests/referencia/     .hack del ensamblador oficial
tests/desensamblado/  salida del desensamblador
```

## Cómo se usa
```bat
compilar.bat
ensamblar.bat tests\asm\Max.asm
desensamblar.bat -d tests\asm\Max.hack salida.asm
probar.bat
```
Con `-d` cada línea del desensamblado lleva su dirección ROM.

## Cómo funciona
- **Parser**: limpia comentarios y espacios y clasifica cada línea en A (`@xxx`), C (`dest=comp;jump`) o L (`(LABEL)`).
- **Code**: tablas de `dest`, `comp` y `jump` para el formato `111a cccc ccdd djjj`. El desensamblador las usa al revés.
- **SymbolTable**: arranca con `R0`-`R15`, `SCREEN`, `KBD`, `SP`, `LCL`, `ARG`, `THIS`, `THAT`.
- **Ensamblador**: dos pasadas. En la primera se guardan las etiquetas con su dirección ROM y en la segunda se traduce. Las variables nuevas se asignan desde la 16.
- **Desensamblador**: bit 15 en 0 es `@número`, en 1 se separan `comp`, `dest` y `jump`. Las etiquetas y variables salen como números porque el binario no guarda los nombres.

## Pruebas
`probar.bat` corre Add, Max, MaxL, Rect, RectL, Pong, PongL y Sum1ToN:
1. Compara nuestro `.hack` con el del ensamblador oficial.
2. Desensambla, vuelve a ensamblar y revisa que dé el mismo binario.

Pasan las 16. También se pueden cargar los `.hack` en el CPU Emulator: Max (R0, R1 → R2), Rect (R0 = alto) y Pong.
