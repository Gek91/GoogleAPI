package GoogleAPI.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Acl;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import GoogleAPI.CalendarGoogleApi;
import GoogleAPI.util.AbstractBaseGoogleApi;
import GoogleAPI.util.AbstractBaseGoogleAuthentication;

public class CalendarGoogleApiImpl extends AbstractBaseGoogleApi implements CalendarGoogleApi {

	private static final List<String> SCOPES = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(CalendarScopes.CALENDAR);
		}
	};
	
	@Override
	protected Collection<String> getScopes() {
		return SCOPES;
	}

	@Override
	protected AbstractGoogleJsonClient buildGoogleService(HttpTransport httpTransport, JsonFactory jacksonFactory, HttpRequestInitializer requestInitializer) {
		return new Calendar(httpTransport, jacksonFactory, requestInitializer);
	}
		
	private Calendar getCalendarGoogleService(String executionGoogleUser) {
		return (Calendar) getGoogleService(executionGoogleUser);
	}
	
	//constructor
	public CalendarGoogleApiImpl(AbstractBaseGoogleAuthentication authenticationService) {
		super(authenticationService);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	@Override
	public Acl getCalendarAcl(String executionGoogleUser, String calendarEmail, String nextPageToken, String fields) {
		
		Acl result = null;
		
		try {
			
			getLogger().info("Calendar APIs - START getCalendarAcl | calendarId:{} nextPageToken:{}.", calendarEmail, nextPageToken);

			result = getCalendarGoogleService(executionGoogleUser)
					.acl()
					.list(calendarEmail)
					.setFields(fields)
					.setPageToken(nextPageToken)
					.execute();
			
			getLogger().info("Calendar APIs - END getCalendarAcl.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in getCalendarAcl.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getCalendarAcl.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public Events getCalendarEvents(String executionGoogleUser, String calendarEmail, Date timeMin, Date timeMax, String fields, String nextPageToken) {
		
		Events result = null;
		
		try {
			
			getLogger().info("Calendar APIs - START getCalendarEvents | calendarId:{}.", calendarEmail);
			
			Calendar.Events.List list = getCalendarGoogleService(executionGoogleUser)
					.events()
					.list(calendarEmail)
					.setFields(fields)
					.setSingleEvents(true)
					.setOrderBy("startTime")
					.setPageToken(nextPageToken);
			
			if(timeMin != null) {
				DateTime time = new DateTime(timeMin);
				list.setTimeMin(time);
				
				if(timeMax != null) {
					time = new DateTime(timeMax);
					list.setTimeMax(time);
				}
			}
			
			result = list.execute();
			
			getLogger().info("Calendar APIs - END getCalendarEvents");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in getCalendarEvents.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getCalendarEvents.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public Event getEvent(String executionGoogleUser, String calendarEmail, String eventId, String fields) {
		
		Event result = null;
		
		try {
			
			getLogger().info("Calendar APIs - START executionGoogleUser | calendarId:{} eventId {}.", calendarEmail, eventId);

			result = getCalendarGoogleService(executionGoogleUser)
					.events()
					.get(calendarEmail, eventId)
					.setFields(fields)
					.execute();
			
			getLogger().info("Calendar APIs - END executionGoogleUser.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in executionGoogleUser.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in executionGoogleUser.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public void deleteEvent(String executionGoogleUser, String calendarEmail, String eventId) {
		
		try {
			
			getLogger().info("Calendar APIs - START deleteEvent | calendarId:{} eventId {}.", calendarEmail, eventId);
			
			getCalendarGoogleService(executionGoogleUser)
				.events()
				.delete(calendarEmail, eventId);
			
			getLogger().info("Calendar APIs - END deleteEvent.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in executionGoogleUser.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in executionGoogleUser.", e);
			throw new RuntimeException(e);
		}
	}

}
