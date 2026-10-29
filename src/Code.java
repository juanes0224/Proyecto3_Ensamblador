import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

// Code: pasa los campos de la intruccion C a binario y al reves
// formato:  1 1 1 a c c c c c c d d d j j j
// las mismas tablas las usa el desensamblador
public class Code {

    // tabla de comp, el valor es a + cccccc (7 bits)
    private static final Map<String, String> COMP = new LinkedHashMap<String, String>();
    // otras formas de escribir lo mismo (ej A+D en vez de D+A)
    private static final Map<String, String> COMP_ALIAS = new HashMap<String, String>();
    // tabla al reves para el desensamblador
    private static final Map<String, String> COMP_REVERSE = new HashMap<String, String>();

    private static final Map<String, String> JUMP = new LinkedHashMap<String, String>();
    private static final Map<String, String> JUMP_REVERSE = new HashMap<String, String>();

    static {
        // cuando a = 0 se usa A
        COMP.put("0",   "0101010");
        COMP.put("1",   "0111111");
        COMP.put("-1",  "0111010");
        COMP.put("D",   "0001100");
        COMP.put("A",   "0110000");
        COMP.put("!D",  "0001101");
        COMP.put("!A",  "0110001");
        COMP.put("-D",  "0001111");
        COMP.put("-A",  "0110011");
        COMP.put("D+1", "0011111");
        COMP.put("A+1", "0110111");
        COMP.put("D-1", "0001110");
        COMP.put("A-1", "0110010");
        COMP.put("D+A", "0000010");
        COMP.put("D-A", "0010011");
        COMP.put("A-D", "0000111");
        COMP.put("D&A", "0000000");
        COMP.put("D|A", "0010101");
        // cuando a = 1 se usa M (osea RAM[A])
        COMP.put("M",   "1110000");
        COMP.put("!M",  "1110001");
        COMP.put("-M",  "1110011");
        COMP.put("M+1", "1110111");
        COMP.put("M-1", "1110010");
        COMP.put("D+M", "1000010");
        COMP.put("D-M", "1010011");
        COMP.put("M-D", "1000111");
        COMP.put("D&M", "1000000");
        COMP.put("D|M", "1010101");

        COMP_ALIAS.put("1+D", "D+1");
        COMP_ALIAS.put("1+A", "A+1");
        COMP_ALIAS.put("1+M", "M+1");
        COMP_ALIAS.put("A+D", "D+A");
        COMP_ALIAS.put("M+D", "D+M");
        COMP_ALIAS.put("A&D", "D&A");
        COMP_ALIAS.put("M&D", "D&M");
        COMP_ALIAS.put("A|D", "D|A");
        COMP_ALIAS.put("M|D", "D|M");

        for (Map.Entry<String, String> e : COMP.entrySet()) {
            COMP_REVERSE.put(e.getValue(), e.getKey());
        }

        JUMP.put("",    "000");
        JUMP.put("JGT", "001");
        JUMP.put("JEQ", "010");
        JUMP.put("JGE", "011");
        JUMP.put("JLT", "100");
        JUMP.put("JNE", "101");
        JUMP.put("JLE", "110");
        JUMP.put("JMP", "111");
        for (Map.Entry<String, String> e : JUMP.entrySet()) {
            JUMP_REVERSE.put(e.getValue(), e.getKey());
        }
    }

    private Code() { }

    // ---------- de texto a binario (ensamblador) ----------

    // dest son 3 bits: A D M. se revisa que letras tiene, asi DM y MD dan lo mismo
    public static String dest(String mnemonic) {
        if (mnemonic == null) {
            mnemonic = "";
        }
        if (!mnemonic.matches("[ADM]*") || hasRepeated(mnemonic)) {
            throw new IllegalArgumentException("dest invalido: " + mnemonic);
        }
        char a = mnemonic.indexOf('A') >= 0 ? '1' : '0';
        char d = mnemonic.indexOf('D') >= 0 ? '1' : '0';
        char m = mnemonic.indexOf('M') >= 0 ? '1' : '0';
        return "" + a + d + m;
    }

    // busca comp en la tabla (primero mira si es un alias)
    public static String comp(String mnemonic) {
        String key = COMP_ALIAS.containsKey(mnemonic) ? COMP_ALIAS.get(mnemonic) : mnemonic;
        String bits = COMP.get(key);
        if (bits == null) {
            throw new IllegalArgumentException("comp invalido: " + mnemonic);
        }
        return bits;
    }

    public static String jump(String mnemonic) {
        String bits = JUMP.get(mnemonic == null ? "" : mnemonic);
        if (bits == null) {
            throw new IllegalArgumentException("jump invalido: " + mnemonic);
        }
        return bits;
    }

    // para que no acepte cosas como "MM"
    private static boolean hasRepeated(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.indexOf(s.charAt(i), i + 1) >= 0) {
                return true;
            }
        }
        return false;
    }

    // ---------- de binario a texto (desensamblador) ----------

    // se escribe en el orden A, M, D (AM, AD, MD, AMD) que es como vienen
    // los archivos de nand2tetris, asi el ensamblador oficial tambien lo lee
    public static String destMnemonic(String bits) {
        boolean a = bits.charAt(0) == '1';
        boolean d = bits.charAt(1) == '1';
        boolean m = bits.charAt(2) == '1';
        StringBuilder sb = new StringBuilder();
        if (a) sb.append('A');
        if (m) sb.append('M');
        if (d) sb.append('D');
        return sb.toString();
    }

    // devuelve null si los bits no son de ninguna operacion
    public static String compMnemonic(String bits) {
        return COMP_REVERSE.get(bits);
    }

    public static String jumpMnemonic(String bits) {
        return JUMP_REVERSE.get(bits);
    }
}
