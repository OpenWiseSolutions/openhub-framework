#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.admin.web.common.editor;

import java.beans.PropertyEditorSupport;

import org.joda.time.DateTime;

import ${package}.admin.services.log.LogParserConstants;

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
