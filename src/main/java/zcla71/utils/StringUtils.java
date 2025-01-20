package zcla71.utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;

public class StringUtils {
    public static String removeAcentos(String str) {
        String result = Normalizer.normalize(str, Normalizer.Form.NFD);
        result = result.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return result;
    }

    // TODO Fazer um método único com parâmetros startDelimiter e endDelimiter; "escapar" ao gerar regex
    // TODO Fazer testes unitários
    public static String[] splitConsideringDoubleBrackets(String str) {
        String[] splitted = str.split("\\s+");
        ArrayList<String> result = new ArrayList<>();
        String incluir = null;
        for (String parte : splitted) {
            if (incluir == null) { // Se não está dentro de colchetes duplos
                if (parte.matches("^\\[\\[.*$")) { // Se começa com colchetes duplos...
                    if (parte.matches("^.*\\]\\]$")) { // ...e termina com colchetes duplos, inclui normalmente
                        result.add(parte);
                    } else { // ...e não termina com colchetes duplos, junta pra incluir depois
                        incluir = parte.substring(2);
                    }
                } else { // Se não começa com colchetes duplos, inclui normalmente
                    result.add(parte);
                }
            } else { // Se já está dentro de colchetes duplos, procura o fechamento
                if (parte.matches("^.*\\]\\]$")) { // Se termina com colchetes duplos, fecha e inclui
                    incluir += " " + parte.substring(0, parte.length() - 2);
                    result.add(incluir);
                    incluir = null;
                } else { // Se não termina com colchetes duplos, junta para incluir depois
                    incluir += " " + parte;
                }
            }
        }
        if (incluir != null) { // Se há colchetes duplos não fechados, faz o split ignorando os colchetes duplos
            ArrayList<String> resto = new ArrayList<>(Arrays.asList(incluir.split("\\s+")));
            resto.set(0, "[[" + resto.get(0));
            result.addAll(resto);
        }
        return result.toArray(new String[0]);
    }

    static public String[] splitConsideringQuotes(String str) {
        String[] splitted = str.split("\\s+");
        ArrayList<String> result = new ArrayList<>();
        String incluir = null;
        for (String parte : splitted) {
            if (incluir == null) { // Se não está dentro de aspas
                if (parte.matches("^\\\".*$")) { // Se começa com aspas...
                    if (parte.matches("^.*\\\"$")) { // ...e termina com aspas, inclui normalmente
                        result.add(parte);
                    } else { // ...e não termina com aspas, junta pra incluir depois
                        incluir = parte.substring(1);
                    }
                } else { // Se não começa com aspas, inclui normalmente
                    result.add(parte);
                }
            } else { // Se já está dentro de aspas, procura o fechamento
                if (parte.matches("^.*\\\"$")) { // Se termina com aspas, fecha e inclui
                    incluir += " " + parte.substring(0, parte.length() - 1);
                    result.add(incluir);
                    incluir = null;
                } else { // Se não termina com aspas, junta para incluir depois
                    incluir += " " + parte;
                }
            }
        }
        if (incluir != null) { // Se há aspas não fechadas, faz o split ignorando as aspas
            ArrayList<String> resto = new ArrayList<>(Arrays.asList(incluir.split("\\s+")));
            resto.set(0, "\"" + resto.get(0));
            result.addAll(resto);
        }
        return result.toArray(new String[0]);
    }
}
