package GoogleAPI;

import java.util.Date;

import com.google.api.services.calendar.model.Acl;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public interface CalendarGoogleApi {

	public Acl getCalendarAcl(String executionGoogleUser, String calendarEmail, String nextPageToken, String fields);
	
	public Events getCalendarEvents(String executionGoogleUser, String calendarEmail, Date timeMin, Date timeMax, String fields, String nextPageToken);
	
	public Event getEvent(String executionGoogleUser, String calendarEmail, String eventId, String fields);
	
	public void deleteEvent(String executionGoogleUser, String calendarEmail, String eventId);
}
