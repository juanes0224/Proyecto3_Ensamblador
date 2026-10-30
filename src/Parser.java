import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Parser: lee el .asm y separa cada instruccion en sus partes
public class Parser {

    public enum InstructionType { A_INSTRUCTION, C_INSTRUCTION, L_INSTRUCTION }

    private final List<String> lines = new ArrayList<String>();
    private int index = -1;
    private String current = null;

    // abre el archivo y guarda las lineas que sirven (sin comentarios ni vacias)
    public Parser(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String clean = clean(line);
                if (!clean.isEmpty()) {
                    lines.add(clean);
                }
            }
        } finally {
            reader.close();
        }
    }

    private static String clean(String line) {
        int comment = line.indexOf("//");
        if (comment >= 0) {
            line = line.substring(0, comment);
        }
        return line.replaceAll("\\s", "");
    }

    public boolean hasMoreLines() {
        return index + 1 < lines.size();
    }

    public void advance() {
        index++;
        current = lines.get(index);
    }

    // vuelve al inicio, se usa para la segunda pasada
    public void reset() {
        index = -1;
        current = null;
    }

    // @algo -> A, (algo) -> L, lo demas es C
    public InstructionType instructionType() {
        if (current.startsWith("@")) {
            return InstructionType.A_INSTRUCTION;
        }
        if (current.startsWith("(") && current.endsWith(")")) {
            return InstructionType.L_INSTRUCTION;
        }
        return InstructionType.C_INSTRUCTION;
    }

    // devuelve lo que esta despues del @ o entre los parentesis
    public String symbol() {
        if (instructionType() == InstructionType.A_INSTRUCTION) {
            return current.substring(1);
        }
        return current.substring(1, current.length() - 1);
    }

    // lo que esta antes del = (si no hay = devuelve vacio)
    public String dest() {
        int eq = current.indexOf('=');
        return eq >= 0 ? current.substring(0, eq) : "";
    }

    public String comp() {
        int eq = current.indexOf('=');
        int semi = current.indexOf(';');
        int start = eq >= 0 ? eq + 1 : 0;
        int end = semi >= 0 ? semi : current.length();
        return current.substring(start, end);
    }

    // lo que esta despues del ; (si no hay salto devuelve vacio)
    public String jump() {
        int semi = current.indexOf(';');
        return semi >= 0 ? current.substring(semi + 1) : "";
    }

    public String currentInstruction() {
        return current;
    }
}
