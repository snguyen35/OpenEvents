package csc.mobility.helper.imagedownloader;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

public class Utils {
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch (Exception ex) {
		}
	}
	
	public static Calendar convertDateToCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
}
