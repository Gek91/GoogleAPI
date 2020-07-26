package main.GoogleAPI.api;

import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Acl;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import main.GoogleAPI.api.data.CalendarAclRoleEnum;
import main.GoogleAPI.api.data.CalendarAclScope;
import main.GoogleAPI.api.data.CalendarMinAccessRoleEnum;

public interface CalendarGoogleApi {

	/*
	 * Calendar
	 */
	public Calendar getCalendar(String executionGoogleUser, String calendarId, String fields);
	
	public Calendar createCalendar(String executionGoogleUser, String summary);
	
	public Calendar updateCalendar(String executionGoogleUser, String calendarId, String summary);
	
	public void deleteCalendar(String executionGoogleUser, String calendarId);
	
	public Acl getCalendarAcl(String executionGoogleUser, String calendarId, String pageToken, String fields);
	
	public AclRule addCalendarAcl(String executionGoogleUser, String calendarId, CalendarAclRoleEnum aclRole, CalendarAclScope aclScope, String scopeValue);
	
	public AclRule updateCalendarAcl(String executionGoogleUser, String calendarId, String ruleId, CalendarAclRoleEnum aclRole, CalendarAclScope aclScope, String scopeValue);
	
	/*
	 * Calendar List
	 */
	public CalendarList getCalendarList(String executionGoogleUser, CalendarMinAccessRoleEnum minAccessRole, String pageToken);
	
	public CalendarListEntry addCalendarToCalendarList(String executionGoogleUser, String calendarId);
	
	public void removeCalendarFromCalendarList(String executionGoogleUser, String calendarId);
	
	/*
	 * Events
	 */
	//TODO too many arguments
	public Events getCalendarEvents(String executionGoogleUser, String calendarId, String fullText, DateTime timeMin, DateTime timeMax, List<String> sharedExtendedProperty, List<String> privateExtendedProperty, Boolean singleEvents, Boolean showDeleted, String fields, String pageToken);
		
	public Event getEvent(String executionGoogleUser, String calendarId, String eventId, String fields);
	
	public Events getEventIstances(String executionGoogleUser, String calendarId, String eventId);
	
	//TODO too many arguments
	public Event createEvent(String executionGoogleUser, String calendarId, String summary, EventDateTime start, EventDateTime end, List<EventAttendee> attendees, List<String> recurrences);
	
	//TODO too many arguments
	public Event updateEvent(String executionGoogleUser, String calendarId, String eventId, String summary, EventDateTime start, EventDateTime end, List<EventAttendee> attendees, List<String> recurrences);
	
	public void deleteEvent(String executionGoogleUser, String calendarId, String eventId);
}
