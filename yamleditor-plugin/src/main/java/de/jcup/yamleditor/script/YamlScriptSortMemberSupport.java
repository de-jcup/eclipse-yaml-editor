package de.jcup.yamleditor.script;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlScriptSortMemberSupport {

    private Yaml yamlParser;

    public YamlScriptSortMemberSupport() {
        DumperOptions options = new DumperOptions();
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setSplitLines(true);
        yamlParser = new Yaml(options);
    }

    public String sortAscending(String input) {
        if (input == null) {
            return "";
        }
        String[] documents = input.split("---");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < documents.length; i++) {
            String document = documents[i];
            String sortedDocument = sortAscendingSingleDocument(document);
            sb.append(sortedDocument);
            if (i< documents.length-1) {
                sb.append("---\n");
            }

        }
        return sb.toString();
    }

    public String sortAscendingSingleDocument(String input) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) yamlParser.loadAs(input, Map.class);
        if (map==null) {
            return "";
        }
        StringWriter writer = new StringWriter();
        yamlParser.dump(sort(map), writer);

        return writer.toString();
    }

    private Map<String, Object> sort(Map<String, Object> map) {
        TreeMap<String, Object> map2 = new TreeMap<String, Object>(map);
        Set<String> list = new LinkedHashSet<>(map2.keySet());
        for (String key : list) {
            Object data = map.get(key);
            if (data instanceof Map) {
                /* mapping in YAML is always with strings. so we can safe cast...*/
                @SuppressWarnings("unchecked")
                Map<String, Object> data2 = sort((Map<String, Object>) data);
                map2.put(key, data2);
            }
        }
        return map2;
    }

    public boolean isHavingCommentsInside(String yamlContent) {
        if (yamlContent==null) {
            return false;
        }
        // Unfortunately snake YAML parser does not provide an easy check method...
        // so we must implement this here
        
        int begin = 0;
        int index = getIndex(yamlContent,begin);
        if (index==-1) {
            return false;
        }
        
        char[] chars = yamlContent.toCharArray();
        while (index!=-1) {
            if (! isLineInsideString(index,chars)) {
                return true;
            }
            begin=index+1;
            index=getIndex(yamlContent,begin);
            
        }
        return false;
    }

    private boolean isLineInsideString(int index, char[] yamlContent) {
        /* very simple approach - just counting number of " or ' - for a modulo operation. Maybe
         * not a 100% assurance, but should be enough for hints
         */
        int posBefore= index-1;
        int singleQuotes=0;
        int doubleQuotes=0;
        while (posBefore>0) {
            char c = yamlContent[posBefore];
            if (c=='\n') {
                break;
            }
            if (c=='\'') {
                singleQuotes++;
            }
            if (c=='"') {
                doubleQuotes++;
            }
            posBefore--;
        }
        int moduloSingle = singleQuotes %2;
        int moduloDouble = doubleQuotes %2;
        
        return moduloDouble!=0 || moduloSingle!=0;
    }

    private int getIndex(String yamlContent, int begin) {
        if (yamlContent==null) {
            return -1;
        }
        if (begin>=yamlContent.length()) {
            return -1;
        }
        return yamlContent .indexOf("#",begin);
    }

}
