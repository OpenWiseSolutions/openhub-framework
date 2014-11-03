--
-- DB script for SA schema (specific for H2 database)
--

CREATE SCHEMA SA;


drop ALIAS if exists TO_DATE;
CREATE ALIAS TO_DATE as '
import java.text.*;
@CODE
java.util.Date toDate(String s, String dateFormat) throws Exception {
  return new SimpleDateFormat(dateFormat).parse(s);
}
';

drop alias if exists TO_CHAR;
create alias TO_CHAR as '
import java.text.SimpleDateFormat;
import java.util.Date;
@CODE
String toChar(String date, String pattern) throws Exception {
pattern = pattern.replaceAll("YY","yy");
pattern = pattern.replaceAll("DD","dd");
pattern = pattern.replaceAll("HH24|hh24","HH");
pattern = pattern.replaceAll("HH?!24|hh?!24","KK");
pattern = pattern.replaceAll("MON|mon","MMM");
pattern = pattern.replaceAll("MI|mi","mm");
pattern = pattern.replaceAll("SS|ss","ss");
pattern = pattern.replaceAll("AM|PM","aa");
SimpleDateFormat sm = new SimpleDateFormat(pattern);
java.util.Date dt;
  if(date.length() > 10)dt = java.sql.Timestamp.valueOf(date);
else
 dt = java.sql.Date.valueOf(date);
return sm.format(dt);

}
';

