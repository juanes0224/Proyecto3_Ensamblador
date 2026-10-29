import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// Ensamblador de Hack: pasa de Prog.asm a Prog.hack
// uso: java HackAssembler Prog.asm
//
// se hace en dos pasadas:
//  1ra pasada: se buscan las etiquetas (LOOP) y se guarda en que linea quedan
//  2da pasada: se traduce todo, las variables nuevas empiezan en la 16
public class HackAssembler {

    private static final int FIRST_VARIABLE_ADDRESS = 16;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Uso: java HackAssembler Prog.asm [Prog2.asm ...]");
            System.exit(1);
        }
        int errors = 0;
        // se puede pasar mas de un archivo
        for (String input : args) {
            try {
                String output = assembleFile(input);
                System.out.println(input + " -> " + output);
            } catch (Exception e) {
                System.err.println("Error en " + input + ": " + e.getMessage());
                errors++;
            }
        }
        System.exit(errors == 0 ? 0 : 1);
    }

    // ensambla un archivo y deja el .hack en la misma carpeta
    public static String assembleFile(String input) throws IOException {
        if (!input.toLowerCase().endsWith(".asm")) {
            throw new IllegalArgumentException("se esperaba un archivo .asm");
        }
        String output = input.substring(0, input.length() - 4) + ".hack";

        Parser parser = new Parser(input);
        SymbolTable symbols = new SymbolTable();

        // primera pasada: solo etiquetas
        // las etiquetas no cuentan como instruccion, por eso no suben el contador
        int romAddress = 0;
        while (parser.hasMoreLines()) {
            parser.advance();
            if (parser.instructionType() == Parser.InstructionType.L_INSTRUCTION) {
                symbols.addEntry(parser.symbol(), romAddress);
            } else {
                romAddress++;
            }
        }

        // segunda pasada: traducir
        parser.reset();
        int nextVariable = FIRST_VARIABLE_ADDRESS;
        BufferedWriter out = new BufferedWriter(new FileWriter(output));
        try {
            while (parser.hasMoreLines()) {
                parser.advance();
                String binary;
                switch (parser.instructionType()) {
                    case A_INSTRUCTION: {
                        String symbol = parser.symbol();
                        int value;
                        if (isNumber(symbol)) {
                            value = Integer.parseInt(symbol);
                            // con 15 bits lo maximo es 32767
                            if (value > 32767) {
                                throw new IllegalArgumentException(
                                        "constante fuera de rango (0..32767): @" + symbol);
                            }
                        } else {
                            // si no esta en la tabla es una variable nueva
                            if (!symbols.contains(symbol)) {
                                symbols.addEntry(symbol, nextVariable++);
                            }
                            value = symbols.getAddress(symbol);
                        }
                        binary = toBinary16(value);
                        break;
                    }
                    case C_INSTRUCTION:
                        try {
                            // ojo: el orden es comp, dest, jump
                            binary = "111" + Code.comp(parser.comp())
                                    + Code.dest(parser.dest())
                                    + Code.jump(parser.jump());
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException(
                                    e.getMessage() + " en '" + parser.currentInstruction() + "'");
                        }
                        break;
                    default: // las etiquetas no se escriben
                        continue;
                }
                out.write(binary);
                out.write("\n");
            }
        } finally {
            out.close();
        }
        return output;
    }

    private static boolean isNumber(String s) {
        return s.matches("\\d+");
    }

    // numero a binario de 16 bits rellenando con ceros a la izquierda
    static String toBinary16(int value) {
        String bits = Integer.toBinaryString(value & 0xFFFF);
        StringBuilder sb = new StringBuilder();
        for (int i = bits.length(); i < 16; i++) {
            sb.append('0');
        }
        return sb.append(bits).toString();
    }
}
