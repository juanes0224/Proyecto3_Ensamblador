import java.util.HashMap;
import java.util.Map;

// tabla de simbolos: guarda las etiquetas y variables con su direccion
public class SymbolTable {

    private final Map<String, Integer> table = new HashMap<String, Integer>();

    // de una vez se meten los simbolos predefinidos
    public SymbolTable() {
        for (int i = 0; i <= 15; i++) {
            table.put("R" + i, i);
        }
        table.put("SCREEN", 16384);
        table.put("KBD", 24576);
        table.put("SP", 0);
        table.put("LCL", 1);
        table.put("ARG", 2);
        table.put("THIS", 3);
        table.put("THAT", 4);
    }

    public void addEntry(String symbol, int address) {
        table.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }

    public int getAddress(String symbol) {
        return table.get(symbol);
    }
}
