package de.jcup.yamleditor.script.formatter;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlSourceFormatter {
    private static final String START_BLOCK = "---";

    public String format(String source) {
        return format(source, null);
    }

    private class CommentMarker {

        public boolean fullLine;
        public String comment;
        public int lineNr;

    }

    public String format(String source, YamlSourceFormatterConfig config) {
        SnakeYamlSourceFormatterConfig internalConfig = new SnakeYamlSourceFormatterConfig(config);

        /* backup meta info */
        List<CommentMarker> commentMarkers = backupCommentsAndMetaData(source, internalConfig);

        /* parse + pretty print */
        Yaml yamlParser = new Yaml();
        Iterable<Object> yamlDocuments = yamlParser.loadAll(source);
        String formatted = formatDocuments(yamlDocuments, internalConfig);

        /* restore meta info */
        String result = dropFirstBlockIfNotDefinedBefore(formatted, internalConfig);
        result = restoreFullComments(result, commentMarkers, internalConfig);
        result = restoreEndingComments(result, commentMarkers, internalConfig);
        result = appendOrphanedComments(result, commentMarkers, internalConfig);
        return result.trim();

    }

    private String appendOrphanedComments(String result, List<CommentMarker> commentMarkers, SnakeYamlSourceFormatterConfig internalConfig) {
        if (commentMarkers.isEmpty()) {
            return result;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(result.trim());
        for (CommentMarker marker : commentMarkers) {
            sb.append("\n");
            sb.append("# ----------------");
            sb.append("# Orphan comments:");
            sb.append("# ----------------");
            if (marker.fullLine) {
                sb.append(marker.comment);
            } else {
                sb.append("# Formerly at end of line:" + marker.lineNr + " :");
                sb.append(marker.comment);
            }
        }
        return null;
    }

    private List<CommentMarker> backupCommentsAndMetaData(String source, SnakeYamlSourceFormatterConfig internalConfig) {
        String[] lines = source.split("\n");
        List<CommentMarker> commentMarkers = new ArrayList<CommentMarker>();

        /* collect some information */
        int lineNr = 0;
        for (String line : lines) {
            lineNr++;
            if (lineNr == 1) {
                if (line.equals("---")) {
                    internalConfig.multiDocFileStartingWithSeparator = true;
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
            } else if (line.equals(START_BLOCK)) {
                internalConfig.setExplicitStart(true);
            } else if (line.equals("...")) {
                internalConfig.setExplicitEnd(true);
            } else {
                String comment = tryToResolveEndingComment(line);
                if (comment != null && !comment.isEmpty()) {
                    CommentMarker marker = new CommentMarker();
                    marker.fullLine = false;
                    marker.comment = comment;
                    marker.lineNr = lineNr;
                    commentMarkers.add(marker);
                }

            }
        }
        return commentMarkers;
    }

    private String tryToResolveEndingComment(String line) {
        boolean insideSingleString = false;
        boolean insideDoubleString = false;
        boolean insideTickString = false;

        char[] chars = line.toCharArray();
        for (int index = 0; index < chars.length; index++) {
            char c = chars[index];
            if (c == '\'') {
                insideSingleString = !insideSingleString;
            } else if (c == '"') {
                insideSingleString = !insideSingleString;
            } else if (c == '`') {
                insideSingleString = !insideSingleString;
            } else if (c == '#') {
                if (insideDoubleString || insideSingleString || insideTickString) {
                    /* inside string - so ignore */
                    continue;
                }
                return line.substring(index);
            }
        }

        return null;
    }

    /*
     * snake yaml removes all comments - seems to be inside spec of 1.1/1.2 but we
     * want to keep it...
     */
    private String dropFirstBlockIfNotDefinedBefore(String formatted, SnakeYamlSourceFormatterConfig config) {
        if (config.multiDocFileStartingWithSeparator) {
            return formatted;
        }
        String[] linesFormatted = formatted.split("\n");
        StringBuilder sb = new StringBuilder();
        int lineNr = 0;
        for (String lineFormatted : linesFormatted) {
            lineNr++;
            if (lineNr == 1) {
                if (lineFormatted.contentEquals(START_BLOCK)) {
                    continue;
                }
            }
            sb.append(lineFormatted);
            sb.append("\n");
        }
        return sb.toString();

    }

    /*
     * snake yaml removes all comments - seems to be inside spec of 1.1/1.2 but we
     * want to keep it...
     */
    private String restoreFullComments(String formatted, List<CommentMarker> commentMarkers, SnakeYamlSourceFormatterConfig config) {
        String[] linesFormatted = formatted.split("\n");
        StringBuilder sb = new StringBuilder();
        int lineNr = 0;
        for (String lineFormatted : linesFormatted) {
            lineNr++;
            while (handleFullLineMarkers(commentMarkers, lineNr, sb)) {
                lineNr++;
            }
            sb.append(lineFormatted);
            sb.append("\n");
        }
        return sb.toString();
    }

    private String restoreEndingComments(String formatted, List<CommentMarker> commentMarkers, SnakeYamlSourceFormatterConfig internalConfig) {
        String[] linesFormatted = formatted.split("\n");
        StringBuilder sb = new StringBuilder();
        int lineNr = 0;
        for (String lineFormatted : linesFormatted) {
            lineNr++;
            sb.append(lineFormatted);
            handleEndLineMarkers(commentMarkers, lineNr, sb);
            sb.append("\n");
        }
        return sb.toString();
    }

    private void handleEndLineMarkers(List<CommentMarker> commentMarkers, int lineNr, StringBuilder sb) {
        CommentMarker markerToDrop = null;
        for (CommentMarker marker : commentMarkers) {
            if (marker.lineNr == lineNr) {
                if (!marker.fullLine) {
                    sb.append(" ");
                    sb.append(marker.comment);
                    markerToDrop = marker;
                    break;
                }
            }
        }
        if (markerToDrop != null) {
            commentMarkers.remove(markerToDrop);
        }
    }

    private boolean handleFullLineMarkers(List<CommentMarker> commentMarkers, int lineNr, StringBuilder sb) {
        boolean lineAdded = false;
        CommentMarker markerToDrop = null;
        for (CommentMarker marker : commentMarkers) {
            if (marker.lineNr == lineNr) {
                if (marker.fullLine) {
                    sb.append(marker.comment);
                    sb.append("\n");
                    lineAdded = true;
                    markerToDrop = marker;
                    break;
                }
            }
        }
        if (markerToDrop != null) {
            commentMarkers.remove(markerToDrop);
        }
        return lineAdded;
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
