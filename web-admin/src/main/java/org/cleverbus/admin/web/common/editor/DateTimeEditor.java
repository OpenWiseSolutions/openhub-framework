package org.cleverbus.admin.web.common.editor;

import java.beans.PropertyEditorSupport;

import org.cleverbus.admin.services.log.LogParserConstants;

import org.joda.time.DateTime;

/**
 * Custom property editor for {@link org.joda.time.DateTime} of Joda-Time format conversion.
 */
public class DateTimeEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        return LogParserConstants.LOGBACK_ISO8601_OPTIONAL_TIME_FORMAT.print((DateTime) getValue());
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(LogParserConstants.LOGBACK_ISO8601_OPTIONAL_TIME_FORMAT.parseDateTime(text));
    }
}
