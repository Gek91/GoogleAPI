package GoogleAPI.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
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
	protected AbstractGoogleJsonClient buildGoogleService(HttpTransport httpTransport, JacksonFactory jacksonFactory,
			Credential credential) {
		return new Calendar(httpTransport, jacksonFactory, credential);
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
			
			getLogger().info("Calendar APIs - retrieve calendar Acl for calendar {} and nextPageToken {}..", calendarEmail, nextPageToken);

			result = getCalendarGoogleService(executionGoogleUser).acl()
						.list(calendarEmail)
						.setFields(fields)
						.setPageToken(nextPageToken)
						.execute();
			
			getLogger().info("Calendar APIs - end retrieving calendar Acl for calenda {}.", calendarEmail);

			
		}  catch(Exception e) {
			getLogger().error("Error retrieving resource acl", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	public Events getCalendarEvents(String executionGoogleUser, String calendarEmail, Date timeMin, Date timeMax, String fields, String nextPageToken) {
		
		Events result = null;
		
		try {
			
			getLogger().info("Calendar APIs - retrieve calendar {} events...", calendarEmail);
			
			Calendar.Events.List list = getCalendarGoogleService(executionGoogleUser).events()
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
			
			getLogger().info("Calendar APIs - end retrieving calendar {} events.", calendarEmail);
			
		}  catch(Exception e) {
			getLogger().error("Error retrieving calendar events", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public Event getEvent(String executionGoogleUser, String calendarEmail, String eventId, String fields) {
		
		Event result = null;
		
		try {
			
			getLogger().info("Calendar APIs - retrieving calendar event {}...", eventId);

			result = getCalendarGoogleService(executionGoogleUser).events()
				.get(calendarEmail, eventId)
				.setFields(fields)
				.execute();
			
			getLogger().info("Calendar APIs - end retrieving calendar event {}.", eventId);
		} catch(GoogleJsonResponseException  e) {
			
			if(e.getStatusCode() == 404) {
				getLogger().error("Calendar event not found", e);
			} else if (e.getStatusCode() == 503) {
				getLogger().error("Calendar or event permission error", e);
			}
			
			getLogger().error("Error retrieving calendar event", e);
			throw new RuntimeException(e);
			
		}  catch(Exception e) {
			getLogger().error("Error retrieving calendar event", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public void deleteEvent(String executionGoogleUser, String calendarEmail, String eventId) {
		
		try {
			
			getLogger().info("Calendar APIs - delete calendar event {}...", eventId);
			
			getCalendarGoogleService(executionGoogleUser).events()
				.delete(calendarEmail, eventId);
			
			getLogger().info("Calendar APIs - end deleting calendar event {}.", eventId);
		}  catch(GoogleJsonResponseException  e) {
			
			if(e.getStatusCode() == 404) {
				getLogger().error("Calendar event not found", e);
			} else if (e.getStatusCode() == 503) {
				getLogger().error("Calendar or event permission error", e);
			}
			
			getLogger().error("Error retrieving calendar event", e);
			throw new RuntimeException(e); 
			
		}  catch(Exception e) {
			getLogger().error("Error deleting calendar event", e);
			throw new RuntimeException(e);
		}
	}


	class BatchEventsGoogleCallback extends JsonBatchCallback<Events> {
		
		private Events result;
				
		public Events getResult() {
			return result;
		}

		public void setResult(Events result) {
			this.result = result;
		}

		@Override
		  public void onSuccess(Events obj, HttpHeaders responseHeaders) { 
			
			this.result = obj;
		}
		
		@Override
	    public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
			getLogger().error("GDrive APIs - Error Message in Batch operation : " + e.getMessage());
	    }
	}

}
