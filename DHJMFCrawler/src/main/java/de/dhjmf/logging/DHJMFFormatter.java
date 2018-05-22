package de.dhjmf.logging;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import de.dhjmf.Utils;

/**
 * Class that contains a individual Formatter for the DHJMF crawling service.
 *
 * @author Andreas Lüschow
 * @since 2018-05-22
 */
public class DHJMFFormatter extends Formatter {
    // this method is called for every log record
    public String format(LogRecord rec) {
        StringBuffer buf = new StringBuffer(1000);
        if(rec.getLevel() != Level.FINEST) {
        	buf.append("[" + rec.getLevel() + "] ");
        }
        buf.append(formatMessage(rec) + Utils.EOL);
        return buf.toString();
    }
}