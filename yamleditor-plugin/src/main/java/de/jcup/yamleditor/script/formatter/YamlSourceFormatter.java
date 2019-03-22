package de.jcup.yamleditor.script.formatter;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlSourceFormatter {
    public String format(String source) {
        return format(source, null);
    }

    private class CommentMarker {

        public boolean fullLine;
        public String comment;
        public int lineNr;

    }

    public String format(String source, YamlSourceFormatterConfig config) {
        List<CommentMarker> commentMarkers = new ArrayList<CommentMarker>();

        SnakeYamlSourceFormatterConfig internalConfig = new SnakeYamlSourceFormatterConfig(config);
        String[] lines = source.split("\n");
        /* collect some information */
        int lineNr = 0;
        for (String line : lines) {
            lineNr++;
            if (lineNr==1) {
                if (line.equals("---")){
                    internalConfig.multiDocFileStartingWithSeparator=true;
                    continue;
                }
            }
            String potentialComment = line.trim();
            if (potentialComment.startsWith("#")) {
                CommentMarker marker = new CommentMarker();
                marker.fullLine = true;
                marker.comment = potentialComment;
                marker.lineNr = lineNr;
                commentMarkers.add(marker);
            }else
            if (line.equals("---")) {
                internalConfig.explicitStart(true);
            }else
            if (line.equals("...")) {
                internalConfig.explicitEnd(true);
            }
        }

        /* parse + pretty print */
        Yaml yamlParser = new Yaml();
        Iterable<Object> yamlDocuments = yamlParser.loadAll(source);
        String formatted = formatDocuments(yamlDocuments, internalConfig);
        
        /* snake yaml removes all comments - seems to be inside spec of 1.1/1.2 but we want to keep it...*/
        String[] linesFormatted = formatted.split("\n");
        StringBuilder sb = new StringBuilder();
        lineNr=0;
        for (String lineFormatted : linesFormatted) {
            lineNr++;
            if (lineNr==1) {
                if (lineFormatted.contentEquals("---")) {
                    if (! internalConfig.multiDocFileStartingWithSeparator) {
                        /* when not defined before we forget the first one - kubernetes files normally do not
                         * start with this. If a use wants it and defines it, it will be kept, but not automatically
                         * added per default*/
                        continue;
                    }
                    
                }
            }
            handleFullLineMarkers(commentMarkers, lineNr, sb);
            sb.append(lineFormatted);
            sb.append("\n");
        }

        return sb.toString().trim();

    }

    private void handleFullLineMarkers(List<CommentMarker> commentMarkers, int lineNr, StringBuilder sb) {
        CommentMarker markerToDrop=null;
        for (CommentMarker marker: commentMarkers) {
            if (marker.lineNr==lineNr) {
                if (marker.fullLine) {
                    sb.append(marker.comment);
                    markerToDrop=marker;
                    break;
                }
            }
        }
        if (markerToDrop!=null) {
            commentMarkers.remove(markerToDrop);
        }
    }

    private String formatDocuments(Iterable<Object> documents, SnakeYamlSourceFormatterConfig config) {

        DumperOptions options = new DumperOptions();
        options.setExplicitStart(config.isExplicitStart());
        options.setExplicitEnd(config.isExplicitEnd());
        options.setIndent(config.getIndent());
        options.setWidth(config.getLineLength());
        options.setDefaultFlowStyle(config.getFlowStyle());
        options.setPrettyFlow(config.isPrettyFlow());
        options.setDefaultScalarStyle(config.getScalarStyle());
        Yaml yaml = new Yaml(options);

        StringBuilder sb = new StringBuilder();

        for (Iterator<Object> it = documents.iterator(); it.hasNext();) {
            Object document = it.next();
            String formattedDocument = yaml.dump(document).trim();
            sb.append(formattedDocument);
            if (it.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
