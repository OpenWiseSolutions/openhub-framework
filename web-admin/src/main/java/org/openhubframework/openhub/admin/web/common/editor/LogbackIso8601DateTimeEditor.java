package org.openhubframework.openhub.admin.web.common.editor;

import java.beans.PropertyEditorSupport;
import java.time.OffsetDateTime;

import org.openhubframework.openhub.admin.services.log.LogParserConstants;


/**
 * Custom property editor for {@link OffsetDateTime} that formats/parses the format
 * {@link LogParserConstants#LOGBACK_ISO8601_OPTIONAL_TIME_FORMATTER}.
 */
public class LogbackIso8601DateTimeEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        return ((OffsetDateTime) getValue()).format(LogParserConstants.LOGBACK_ISO8601_OPTIONAL_TIME_FORMATTER);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(LogParserConstants.LOGBACK_ISO8601_OPTIONAL_TIME_FORMATTER.parse(text));
    }
}
