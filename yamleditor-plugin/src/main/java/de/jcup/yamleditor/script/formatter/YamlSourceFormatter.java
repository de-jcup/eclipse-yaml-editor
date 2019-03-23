package de.jcup.yamleditor.script.formatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlSourceFormatter {
    private static final String START_BLOCK = "---";

    public String format(String source) {
        return format(source, null);
    }

    public String format(String source, YamlSourceFormatterConfig config) {
        if (config == null) {
            config = new DefaultYamlSourceFormatterConfig();
        }
        SnakeYamlConfig snakeConfig = new SnakeYamlConfig(config);

        /* backup meta info */
        CommentsRescueContext context = backupCommentsAndMetaData(source, snakeConfig);

        /* parse + pretty print */
        Yaml yamlParser = new Yaml();
        Iterable<Object> yamlDocuments = yamlParser.loadAll(source);
        String formatted = formatDocuments(yamlDocuments, snakeConfig);

        /* restore meta info */
        String result = dropFirstBlockIfNotDefinedBefore(formatted, snakeConfig);
        if (config.isRestoreCommentsEnabled()) {
            result = restoreFullComments(result, context, snakeConfig);
            result = restoreEndingCommentsBySearchForNewLine(result, context, snakeConfig);
            /* next is only fall back for unrecognized situation for ending comments */
            result = restoreEndingCommentsByLineNumbers(result, context, snakeConfig);
            result = appendOrphanedComments(result, context, snakeConfig);
        }
        return result.trim();

    }

    private String appendOrphanedComments(String result, CommentsRescueContext context, SnakeYamlConfig internalConfig) {
        if (context.commentMarkers.isEmpty()) {
            return result;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(result.trim());
        sb.append("\n");
        sb.append("# ----------------\n");
        sb.append("# Orphan comments:\n");
        sb.append("# ----------------\n");
        for (CommentMarker marker : context.commentMarkers) {
            if (marker.fullLine) {
                sb.append("# Was at begin of line:" + marker.lineNr + " :");
                sb.append(marker.comment);
            } else {
                sb.append("# Was at end of line:" + marker.lineNr + " :");
                sb.append(marker.comment);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private CommentsRescueContext backupCommentsAndMetaData(String source, SnakeYamlConfig internalConfig) {
        CommentsRescueContext context = new CommentsRescueContext(source);

        /* collect some information */
        int lineNr = 0;
        for (String line : context.originSourceLines) {
            lineNr++;
            if (lineNr == 1) {
                if (line.equals("---")) {
                    internalConfig.multiDocFileStartingWithSeparator = true;
                    continue;
                }
            }
            if (line.equals(START_BLOCK)) {
                internalConfig.setExplicitStart(true);
            } else if (line.equals("...")) {
                internalConfig.setExplicitEnd(true);
            }
        }
        return context;
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
    private String dropFirstBlockIfNotDefinedBefore(String formatted, SnakeYamlConfig config) {
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
    private String restoreFullComments(String formatted, CommentsRescueContext context, SnakeYamlConfig config) {
        String[] linesFormatted = formatted.split("\n");
        StringBuilder sb = new StringBuilder();
        int lineNr = 0;
        for (String lineFormatted : linesFormatted) {
            lineNr++;
            while (handleFullLineMarkers(context, lineNr, sb)) {
                lineNr++;
            }
            sb.append(lineFormatted);
            sb.append("\n");
        }
        return sb.toString();
    }

    private String restoreEndingCommentsBySearchForNewLine(String formatted, CommentsRescueContext context, SnakeYamlConfig internalConfig) {
        String[] linesFormatted = formatted.split("\n");
        CommentMarker markerToDrop = null;
        StringBuilder sb = new StringBuilder();

        List<String> reducedComparableLineParts2=new ArrayList<String>();
        for (String lineFormatted : linesFormatted) {

            sb.append(lineFormatted);
            String reducedComparablePart  = context.reduceToComparablePart(lineFormatted);
            int currentReducedIndex= context.calculateReducedComparableIndex(reducedComparablePart, reducedComparableLineParts2);
            reducedComparableLineParts2.add(reducedComparablePart);
            
            for (CommentMarker marker : context.commentMarkers) {
                if (marker.fullLine) {
                    continue;
                }
                if (!reducedComparablePart.equals(marker.reducedComparablePart)) {
                    continue;
                }
                /* check if we are really in the wanted line */ 
                if (marker.reducedComparableIndex!=currentReducedIndex) {
                    continue;
                }
                sb.append(" ");
                sb.append(marker.comment);
                markerToDrop = marker;
                break;
            }
            if (markerToDrop != null) {
                context.commentMarkers.remove(markerToDrop);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String restoreEndingCommentsByLineNumbers(String formatted, CommentsRescueContext context, SnakeYamlConfig internalConfig) {
        String[] linesFormatted = formatted.split("\n");
        StringBuilder sb = new StringBuilder();
        int lineNr = 0;
        for (String lineFormatted : linesFormatted) {
            lineNr++;
            sb.append(lineFormatted);
            restoreEndingCommentsByLineNumber(context, lineNr, sb);
            sb.append("\n");
        }
        return sb.toString();
    }

    private void restoreEndingCommentsByLineNumber(CommentsRescueContext context, int lineNr, StringBuilder sb) {
        CommentMarker markerToDrop = null;
        for (CommentMarker marker : context.commentMarkers) {
            if (marker.fullLine) {
                continue;
            }
            if (marker.lineNr == lineNr) {
                sb.append(" ");
                sb.append(marker.comment);
                markerToDrop = marker;
                break;
            }
        }
        if (markerToDrop != null) {
            context.commentMarkers.remove(markerToDrop);
        }
    }

    private boolean handleFullLineMarkers(CommentsRescueContext context, int lineNr, StringBuilder sb) {
        boolean lineAdded = false;
        CommentMarker markerToDrop = null;
        for (CommentMarker marker : context.commentMarkers) {
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
            context.commentMarkers.remove(markerToDrop);
        }
        return lineAdded;
    }

    private String formatDocuments(Iterable<Object> documents, SnakeYamlConfig config) {

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

    private class CommentsRescueContext {
        private String[] originSourceLines;
        private List<String> reducedComparableLineParts = new ArrayList<String>();
        private List<CommentMarker> commentMarkers = new ArrayList<CommentMarker>();

        public CommentsRescueContext(String originSource) {
            originSourceLines = originSource.split("\n");

            /* collect some information */
            int lineNr = 0;
            for (String line : originSourceLines) {
                lineNr++;
                String potentialComment = line.trim();
                if (potentialComment.startsWith("#")) {
                    handleLineComment(lineNr, line, potentialComment);
                } else {
                    String comment = tryToResolveEndingComment(line);
                    if (comment != null && !comment.isEmpty()) {
                        handleEndComment(lineNr, line, comment);
                    } else {
                        handleLineWithoutComment(line);
                    }

                }
            }

        }

        private void handleLineWithoutComment(String line) {
            reducedComparableLineParts.add(reduceToComparablePart(line));
        }

        private void handleLineComment(int lineNr, String line, String potentialComment) {
            CommentMarker marker = new CommentMarker();
            marker.originLine = line;
            marker.fullLine = true;
            marker.comment = potentialComment;
            marker.lineNr = lineNr;
            commentMarkers.add(marker);
            reducedComparableLineParts.add("");
        }

        private void handleEndComment(int lineNr, String line, String comment) {
            CommentMarker marker = new CommentMarker();
            marker.originLine = line;
            marker.fullLine = false;
            marker.comment = comment;
            marker.lineNr = lineNr;
            marker.reducedComparablePart = reduceToComparablePart(marker);
            marker.reducedComparableIndex = calculateCurrentReducedComparableIndex(marker.reducedComparablePart);
            /* important: next line adds where index is already calculated */
            reducedComparableLineParts.add(marker.reducedComparablePart);

            commentMarkers.add(marker);
        }

        private int calculateCurrentReducedComparableIndex(String reducedPart) {
            return calculateReducedComparableIndex(reducedPart, reducedComparableLineParts);
        }

        private int calculateReducedComparableIndex(String reducedPart, List<String> reducedComparableLineParts2) {
            int index = 0;
            for (String reduced : reducedComparableLineParts2) {
                if (reducedPart.equals(reduced)) {
                    index++;
                }
            }
            return index;
        }

        private String reduceToComparablePart(CommentMarker marker) {
            String line = marker.originLine;
            int endIndex = line.indexOf(marker.comment);
            String justCode = line.substring(0, endIndex).trim();
            return reduceToComparablePart(justCode);
        }

        private String reduceToComparablePart(String justCode) {
            String result = justCode.replaceAll(" ", "");
            result = result.replaceAll("\"", "");
            result = result.replaceAll("'", "");
            return result;
        }

    }

    private class CommentMarker {

        public int reducedComparableIndex;
        public String originLine;
        public String reducedComparablePart;
        public boolean fullLine;
        public String comment;
        public int lineNr;

    }

}
