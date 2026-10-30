# Proyecto 3 - Ensamblador y Desensamblador Hack

Organización y Arquitectura de Computadores - Clase 18 (Translating Programs), capítulo 6 de Nand2Tetris.

## Integrantes
- Juan Esteban Grisales Restrepo
- Jerónimo Gutiérrez Gutiérrez

## Qué hace
- **HackAssembler**: pasa un `Prog.asm` a `Prog.hack`.
- **HackDisassembler**: pasa un `Prog.hack` a `Prog.asm`.

## Carpetas
```text
src/            Parser, Code, SymbolTable, HackAssembler, HackDisassembler
bin/            clases compiladas (Java 8)
DOCUMENTACION/  Documentacion_Proyecto3.docx
tests/
  asm/          programas de prueba y los .hack que saca nuestro ensamblador
  referencia/   .hack sacados con el ensamblador oficial
  desensamblado/ salida del desensamblador
```

## Cómo se corre
```bat
compilar.bat
ensamblar.bat tests\asm\Max.asm
desensamblar.bat -d tests\asm\Max.hack salida.asm
probar.bat
```
- `-d` pone la dirección ROM de cada instrucción como comentario.
- Para compilar hace falta `javac`. Para correr basta con Java 8.

## Cómo funciona
**Parser**: lee el `.asm`, quita comentarios y espacios y dice si cada línea es instrucción A (`@xxx`), C (`dest=comp;jump`) o etiqueta (`(LABEL)`).

**Code**: tablas de `dest`, `comp` y `jump` según el formato `111a cccc ccdd djjj`. El desensamblador usa las mismas tablas al revés.

**SymbolTable**: arranca con `R0`-`R15`, `SCREEN`, `KBD`, `SP`, `LCL`, `ARG`, `THIS` y `THAT`.

**Ensamblador (dos pasadas)**:
1. Primera pasada: guarda cada `(LABEL)` con la dirección de la siguiente instrucción.
2. Segunda pasada: traduce todo. Si un `@símbolo` no está en la tabla es una variable nueva y se le asigna una dirección desde la 16.

**Desensamblador**: si el bit 15 es 0 es instrucción A y sale `@número`. Si es 1, se separan `comp`, `dest` y `jump` y se buscan en las tablas. Como el binario no guarda nombres, las etiquetas y variables salen como números (`@LOOP` queda `@4`).

## Pruebas
`probar.bat` hace dos cosas con Add, Max, MaxL, Rect, RectL, Pong, PongL y Sum1ToN:
1. Compara nuestro `.hack` con el del ensamblador oficial.
2. Desensambla, vuelve a ensamblar y revisa que dé el mismo binario.

Las 16 pruebas pasan. Para verlo corriendo se carga el `.hack` en el CPU Emulator: en Max se ponen valores en R0 y R1 y se revisa R2, en Rect se pone un valor en R0 y se mira la pantalla, y Pong se juega con las flechas.
