package de.jcup.yamleditor.script;

import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlScriptSortMemberSupport {
    
    private Yaml yamlParser;

    public YamlScriptSortMemberSupport(){
        DumperOptions options = new DumperOptions();
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setSplitLines(true);
        yamlParser = new Yaml(options);
    }


    public String sortFirstMembers(String input) {
        @SuppressWarnings("unchecked")
        Map<String,Object> map= (Map<String, Object>) yamlParser.loadAs(input,Map.class);
        TreeMap<String,Object> map2 = new TreeMap<String, Object>(map); 
        StringWriter writer = new StringWriter();
        yamlParser.dump(map2, writer);
        
        return writer.toString();
    }


}
