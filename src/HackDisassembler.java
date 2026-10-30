import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// Desensamblador de Hack: pasa de Prog.hack a Prog.asm
// uso: java HackDisassembler [-d] Prog.hack [Salida.asm]
//   -d  pone al lado de cada linea su direccion en la ROM (para seguir los saltos)
//   si no se pone Salida.asm se crea Prog.asm en la misma carpeta
//
// nota: en el binario no quedan los nombres de las variables ni etiquetas,
// entonces las intrucciones A salen como @numero
public class HackDisassembler {

    public static void main(String[] args) {
        boolean addresses = false;
        String input = null;
        String output = null;
        for (String arg : args) {
            if (arg.equals("-d")) {
                addresses = true;
            } else if (input == null) {
                input = arg;
            } else if (output == null) {
                output = arg;
            }
        }
        if (input == null) {
            System.err.println("Uso: java HackDisassembler [-d] Prog.hack [Salida.asm]");
            System.exit(1);
        }
        if (output == null) {
            if (!input.toLowerCase().endsWith(".hack")) {
                System.err.println("Error: se esperaba un archivo .hack");
                System.exit(1);
            }
            output = input.substring(0, input.length() - 5) + ".asm";
        }
        try {
            disassembleFile(input, output, addresses);
            System.out.println(input + " -> " + output);
        } catch (Exception e) {
            System.err.println("Error en " + input + ": " + e.getMessage());
            System.exit(1);
        }
    }

    public static void disassembleFile(String input, String output, boolean addresses)
            throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(input));
        BufferedWriter out = new BufferedWriter(new FileWriter(output));
        try {
            out.write("// Desensamblado de " + new File(input).getName() + " con HackDisassembler\n");
            String line;
            int lineNumber = 0;
            int romAddress = 0;
            while ((line = in.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (!line.matches("[01]{16}")) {
                    throw new IllegalArgumentException(
                            "linea " + lineNumber + " no es una instruccion de 16 bits: " + line);
                }
                String asm = disassemble(line);
                if (addresses) {
                    asm = String.format("%-16s// %d", asm, romAddress);
                }
                out.write(asm);
                out.write("\n");
                romAddress++;
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public static String disassemble(String bits) {
        // si empieza en 0 es instruccion A, el resto es el numero
        if (bits.charAt(0) == '0') {
            return "@" + Integer.parseInt(bits.substring(1), 2);
        }
        // si no es C: 111 a cccccc ddd jjj
        String comp = Code.compMnemonic(bits.substring(3, 10));
        String dest = Code.destMnemonic(bits.substring(10, 13));
        String jump = Code.jumpMnemonic(bits.substring(13, 16));
        if (comp == null) {
            return "// instruccion C no valida: " + bits;
        }
        StringBuilder sb = new StringBuilder();
        if (!dest.isEmpty()) {
            sb.append(dest).append('=');
        }
        sb.append(comp);
        if (!jump.isEmpty()) {
            sb.append(';').append(jump);
        }
        return sb.toString();
    }
}
