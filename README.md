# Proyecto 3 – Ensamblador y Desensamblador Hack

Organización y Arquitectura de Computadores – Semana 10, Clase 18 (*Translating Programs*).
Basado en el capítulo 6 de Nand2Tetris.

## Estudiantes
- Jerónimo Gutierrez
- Juan Esteban Grisales

## Enunciado (diapositivas 58 y 60)

| Parte | Contrato | Uso |
|---|---|---|
| **A. Ensamblador** | Traducir un programa simbólico Hack (`Prog.asm`) a instrucciones binarias Hack (`Prog.hack`). | `java HackAssembler Prog.asm` |
| **B. Desensamblador** | Traducir instrucciones binarias Hack (`Prog.hack`) a un programa simbólico Hack (`Prog.asm`). | `java HackDisassembler Prog.hack` |

Supuesto del enunciado: los archivos de entrada no tienen errores.

## Estructura

```text
Proyecto3_Ensamblador/
├── src/
│   ├── Parser.java            lee y descompone cada instrucción
│   ├── Code.java              tablas dest/comp/jump (ida y vuelta)
│   ├── SymbolTable.java       tabla de símbolos con los predefinidos
│   ├── HackAssembler.java     programa principal del ensamblador
│   └── HackDisassembler.java  programa principal del desensamblador
├── bin/                       clases compiladas (Java 8)
├── DOCUMENTACION/
│   └── Documentacion_Proyecto3.docx
├── tests/
│   ├── asm/                   programas de prueba (+ .hack generados)
│   ├── referencia/            .hack generados con el ensamblador oficial
│   └── desensamblado/         salida del desensamblador (+ re-ensamblado)
├── compilar.bat
├── ensamblar.bat
├── desensamblar.bat
└── probar.bat
```

## Cómo usarlo

```bat
compilar.bat                          :: compila src\ -> bin\ (necesita javac)
ensamblar.bat tests\asm\Max.asm       :: genera tests\asm\Max.hack
desensamblar.bat tests\asm\Max.hack salida.asm
desensamblar.bat -d tests\asm\Max.hack salida.asm   :: con direcciones ROM como comentario
probar.bat                            :: corre toda la batería de pruebas
```

> Las clases de `bin/` ya están compiladas para Java 8, así que basta con el
> JRE instalado para ensamblar/desensamblar. Para recompilar se necesita un
> JDK; por ejemplo el que trae CLion:
> `"C:\Program Files\JetBrains\CLion 2026.1.4\jbr\bin\javac.exe" --release 8 -encoding UTF-8 -d bin src\*.java`

## Diseño (API propuesta en las diapositivas 42–56)

### Parser
- Al construirse lee el archivo, quita comentarios (`//...`), espacios y líneas vacías.
- `hasMoreLines()`, `advance()`, `reset()` (para empezar la segunda pasada).
- `instructionType()` → `A_INSTRUCTION` (`@xxx`), `C_INSTRUCTION` (`dest=comp;jump`), `L_INSTRUCTION` (`(LABEL)`).
- `symbol()`, `dest()`, `comp()`, `jump()`. Si falta `dest` o `jump` devuelven `""`.

### Code
Traduce cada campo de `1 1 1 a c c c c c c d d d j j j`:
- `dest("DM")` → `011` (acepta las letras en cualquier orden: `DM` = `MD`).
- `comp("A+1")` → `0110111`, `comp("D&M")` → `1000000` (7 bits: `a` + `cccccc`).
- `jump("JNE")` → `101`.

Las mismas tablas sirven en sentido inverso (`compMnemonic`, `destMnemonic`,
`jumpMnemonic`), así el ensamblador y el desensamblador no pueden contradecirse.

### SymbolTable
`addEntry`, `contains`, `getAddress`. Predefinidos: `R0..R15`, `SCREEN=16384`,
`KBD=24576`, `SP=0`, `LCL=1`, `ARG=2`, `THIS=3`, `THAT=4`.

### HackAssembler (dos pasadas)
1. **Inicialización:** crea el `Parser` y la `SymbolTable`.
2. **Primera pasada:** recorre el programa contando instrucciones; cada
   `(LABEL)` se agrega a la tabla con la dirección ROM de la siguiente instrucción.
3. **Segunda pasada:**
   - `@número` → binario de 16 bits.
   - `@símbolo` → si no está en la tabla es una variable nueva y se le asigna
     la siguiente dirección libre desde **16**; luego se traduce su dirección.
   - `dest=comp;jump` → `111` + comp + dest + jump.
   - `(LABEL)` no genera código.

### HackDisassembler
- Bit 15 = `0` → instrucción A: `@` + valor de los 15 bits restantes.
- Bit 15 = `1` → instrucción C: separa `a cccccc` (comp), `ddd` (dest) y `jjj` (jump)
  y los busca en las tablas inversas de `Code`.
- **Limitación inherente:** el binario no guarda nombres, así que etiquetas y
  variables salen como números (`@16`, `@LOOP` → `@4`). El programa obtenido es
  equivalente: al re-ensamblarlo da exactamente el mismo `.hack`.
- Opción `-d`: agrega la dirección ROM de cada instrucción como comentario,
  útil para saber a dónde salta un `@n` seguido de un `;JMP`.

## Pruebas

`probar.bat` ejecuta dos verificaciones sobre `Add`, `Max`, `MaxL`, `Rect`,
`RectL`, `Pong`, `PongL` (programas oficiales del proyecto 6) y `Sum1ToN`
(ejemplo de las diapositivas):

1. **Ensamblador:** el `.hack` generado debe ser idéntico al del ensamblador
   oficial de Nand2Tetris (`tests/referencia/`).
2. **Desensamblador (ida y vuelta):** `.hack` → `.asm` → `.hack` debe dar el mismo binario.

Resultado actual: **16/16 pruebas OK** (incluido `Pong.asm`, ~27 000 instrucciones).

Además, `Sum1ToN.hack` coincide con el binario que aparece en la diapositiva 59.

### Probar en el CPU Emulator (diapositivas 62–67)
1. Ensamblar, p. ej. `ensamblar.bat tests\asm\Max.asm`.
2. Abrir `nand2tetris\tools\CPUEmulator.bat` y cargar `Max.hack`.
3. Poner valores en `R0` y `R1`, ejecutar y revisar `R2`.
   - `Add`: revisar `R0 = 5`.
   - `Rect`: poner un valor en `R0` y mirar la pantalla.
   - `Pong`: velocidad en *fast* y jugar con las flechas.
