package main.GoogleAPI.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Acl;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.AclRule.Scope;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import main.GoogleAPI.api.CalendarGoogleApi;
import main.GoogleAPI.api.data.CalendarAclRoleEnum;
import main.GoogleAPI.api.data.CalendarAclScope;
import main.GoogleAPI.api.data.CalendarMinAccessRoleEnum;
import main.GoogleAPI.base.AbstractBaseGoogleApi;
import main.GoogleAPI.base.AbstractBaseGoogleAuthentication;
import main.GoogleAPI.base.AbstractGoogleServiceBatch;
import main.GoogleAPI.base.BasicBatchCallBack;

public class CalendarGoogleApiImpl extends AbstractBaseGoogleApi<Calendar> implements CalendarGoogleApi {

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
	protected Calendar buildGoogleService(HttpTransport httpTransport, JsonFactory jacksonFactory, HttpRequestInitializer requestInitializer) {
		return new Calendar(httpTransport, jacksonFactory, requestInitializer);
	}
		
	private Calendar getCalendarGoogleService(String executionGoogleUser) {
		return getGoogleService(executionGoogleUser);
	}
	
	//constructor
	public CalendarGoogleApiImpl(AbstractBaseGoogleAuthentication authenticationService) {
		super(authenticationService);
	}
	
	@Override
	public CalendarBatchBuilder getBatchBuilder(String executionGoogleUser) {
	
		return new CalendarBatchBuilder(executionGoogleUser, getCalendarGoogleService(executionGoogleUser));
	}
	
	@Override
	public CalendarBatchBuilder getBatchBuilder(String executionGoogleUser, int operationsInBatch) {
		
		return new CalendarBatchBuilder(executionGoogleUser, getCalendarGoogleService(executionGoogleUser), operationsInBatch);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Methods
	
	@Override
	public com.google.api.services.calendar.model.Calendar getCalendar(String executionGoogleUser, String calendarId, String fields) {
		
		com.google.api.services.calendar.model.Calendar result = null;
		
		try {
			getLogger().info("Calendar APIs - START getCalendar | calendarId:{}.", calendarId);

			result = getCalendarOperation(executionGoogleUser, calendarId, fields).execute();
			
			getLogger().info("Calendar APIs - END getCalendar.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in getCalendar.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getCalendar.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public com.google.api.services.calendar.model.Calendar createCalendar(String executionGoogleUser, String summary) {
		
		com.google.api.services.calendar.model.Calendar result = null;
		
		try {
			getLogger().info("Calendar APIs - START createCalendar.");

			result = createCalendarOperation(executionGoogleUser, summary).execute();
			
			getLogger().info("Calendar APIs - END createCalendar.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in createCalendar.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in createCalendar.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public com.google.api.services.calendar.model.Calendar updateCalendar(String executionGoogleUser, String calendarId, String summary) {
		
		com.google.api.services.calendar.model.Calendar result = null;
		
		try {
			getLogger().info("Calendar APIs - START updateCalendar | id:{}.", calendarId);

			result = updateCalendarOperation(executionGoogleUser, calendarId, summary).execute();
			
			getLogger().info("Calendar APIs - END updateCalendar | id:{}.", calendarId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in updateCalendar.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in updateCalendar.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public void deleteCalendar(String executionGoogleUser, String calendarId) {
		
		try {
			getLogger().info("Calendar APIs - START deleteCalendar | id:{}.", calendarId);

			deleteCalendarOperation(executionGoogleUser, calendarId).execute();
			
			getLogger().info("Calendar APIs - END deleteCalendar | id:{}.", calendarId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in deleteCalendar.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in deleteCalendar.", e);
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public Acl getCalendarAcl(String executionGoogleUser, String calendarId, String nextPageToken, String fields) {
		
		Acl result = null;
		
		try {
			
			getLogger().info("Calendar APIs - START getCalendarAcl | calendarId:{} nextPageToken:{}.", calendarId, nextPageToken);

			result = getCalendarAclOperation(executionGoogleUser, calendarId, nextPageToken, fields).execute();
			
			getLogger().info("Calendar APIs - END getCalendarAcl | calendarId:{}.", calendarId);
			
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
	public AclRule addCalendarAcl(String executionGoogleUser, String calendarId, CalendarAclRoleEnum aclRole, CalendarAclScope aclScope, String scopeValue) {

		AclRule result = null;
		
		try {
			getLogger().info("Calendar APIs - START addCalendarAcl | id:{}.", calendarId);

			result = addCalendarAclOperation(executionGoogleUser, calendarId, aclRole, aclScope, scopeValue).execute();
			
			getLogger().info("Calendar APIs - END addCalendarAcl | id:{}.", calendarId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in addCalendarAcl.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in addCalendarAcl.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public AclRule updateCalendarAcl(String executionGoogleUser, String calendarId, String ruleId,	CalendarAclRoleEnum aclRole, CalendarAclScope aclScope, String scopeValue) {

		AclRule result = null;
		
		try {
			getLogger().info("Calendar APIs - START updateCalendarAcl | id:{} - ruleId:{}.", calendarId, ruleId);

			result = updateCalendarAclOperation(executionGoogleUser, calendarId, ruleId, aclRole, aclScope, scopeValue).execute();
			
			getLogger().info("Calendar APIs - END updateCalendarAcl | id:{} - ruleId:{}.", calendarId, ruleId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in updateCalendarAcl.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in updateCalendarAcl.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public CalendarList getCalendarList(String executionGoogleUser, CalendarMinAccessRoleEnum minAccessRole, String pageToken) {
		
		CalendarList result = null;
		
		try {
			getLogger().info("Calendar APIs - START updateCalendarAcl.");

			result = getCalendarListOperation(executionGoogleUser, minAccessRole, pageToken).execute();
			
			getLogger().info("Calendar APIs - END updateCalendarAcl.");
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in getCalendarList.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getCalendarList.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public CalendarListEntry addCalendarToCalendarList(String executionGoogleUser, String calendarId) {
		
		CalendarListEntry result = null;
		
		try {
			getLogger().info("Calendar APIs - START addCalendarToCalendarList | id:{}.", calendarId);

			result = addCalendarToCalendarListOperation(executionGoogleUser, calendarId).execute();
			
			getLogger().info("Calendar APIs - END addCalendarToCalendarList | id:{}.", calendarId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in addCalendarToCalendarList.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in addCalendarToCalendarList.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public void removeCalendarFromCalendarList(String executionGoogleUser, String calendarId) {
		
		try {
			getLogger().info("Calendar APIs - START removeCalendarFromCalendarList | id:{}.", calendarId);

			removeCalendarFromCalendarListOperation(executionGoogleUser, calendarId).execute();
			
			getLogger().info("Calendar APIs - END removeCalendarFromCalendarList | id:{}.", calendarId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in removeCalendarFromCalendarList.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in removeCalendarFromCalendarList.", e);
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public Events getCalendarEvents(String executionGoogleUser, String calendarId, String fullText, DateTime timeMin, DateTime timeMax, List<String> sharedExtendedProperty, List<String> privateExtendedProperty, Boolean singleEvents, Boolean showDeleted, String fields, String pageToken) {
		
		Events result = null;
		
		try {
			
			getLogger().info("Calendar APIs - START getCalendarEvents | calendarId:{}.", calendarId);

			result = getCalendarEventsOperation(executionGoogleUser, calendarId, fullText, timeMin, timeMax, sharedExtendedProperty, privateExtendedProperty, singleEvents, showDeleted, fields, pageToken).execute();
			
			getLogger().info("Calendar APIs - END getCalendarEvents | calendarId:{}.", calendarId);
			
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
	public Event getEvent(String executionGoogleUser, String calendarId, String eventId, String fields) {
		
		Event result = null;
		
		try {
			
			getLogger().info("Calendar APIs - START getEvent | calendarId:{} - eventId {}.", calendarId, eventId);

			result = getEventOperation(executionGoogleUser, calendarId, eventId, fields).execute();
			
			getLogger().info("Calendar APIs - END getEvent | calendarId:{} - eventId {}.", calendarId, eventId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in getEvent.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getEvent.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public Events getEventIstances(String executionGoogleUser, String calendarId, String eventId) {
		
		Events result = null;
		
		try {
			
			getLogger().info("Calendar APIs - START getEventIstances | calendarId:{} - eventId {}.", calendarId, eventId);

			result = getEventIstancesOperation(executionGoogleUser, calendarId, eventId).execute();
			
			getLogger().info("Calendar APIs - END getEventIstances | calendarId:{} - eventId {}.", calendarId, eventId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in getEventIstances.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getEventIstances.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public Event createEvent(String executionGoogleUser, String calendarId, String summary, EventDateTime start, EventDateTime end, List<EventAttendee> attendees, List<String> recurrences) {

		Event result = null;
		
		try {
			
			getLogger().info("Calendar APIs - START createEvent | calendarId:{}.", calendarId);

			result = createEventOperation(executionGoogleUser, calendarId, summary, start, end, attendees, recurrences).execute();
			
			getLogger().info("Calendar APIs - END createEvent | calendarId:{}.", calendarId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in createEvent.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in createEvent.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public Event updateEvent(String executionGoogleUser, String calendarId, String eventId, String summary,	EventDateTime start, EventDateTime end, List<EventAttendee> attendees, List<String> recurrences) {

		Event result = null;
		
		try {
			
			getLogger().info("Calendar APIs - START updateEvent | calendarId:{} | eventId:{}.", calendarId, eventId);

			result = updateEventOperation(executionGoogleUser, calendarId, eventId, summary, start, end, attendees, recurrences).execute();
			
			getLogger().info("Calendar APIs - END updateEvent | calendarId:{} | eventId:{}.", calendarId, eventId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in updateEvent.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in updateEvent.", e);
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public void deleteEvent(String executionGoogleUser, String calendarId, String eventId) {
		
		try {
			
			getLogger().info("Calendar APIs - START deleteEvent | calendarId:{} - eventId {}.", calendarId, eventId);
			
			deleteEventOperation(executionGoogleUser, calendarId, eventId).execute();
			
			getLogger().info("Calendar APIs - END deleteEvent calendarId:{} - eventId {}.", calendarId, eventId);
			
		} catch(GoogleJsonResponseException e) {
			getLogger().error("Calendar APIs - Google service error in deleteEvent.");
			handleServiceException(e);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in deleteEvent.", e);
			throw new RuntimeException(e);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Operation
	
	private com.google.api.services.calendar.Calendar.Calendars.Get getCalendarOperation(String executionGoogleUser, String calendarId, String fields) {
		
		try {
			return getCalendarGoogleService(executionGoogleUser).calendars().get(calendarId);
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getCalendarOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.Calendars.Insert createCalendarOperation(String executionGoogleUser, String summary) {
		
		try {
			com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
			calendar.setSummary(summary);
			
			return getCalendarGoogleService(executionGoogleUser).calendars().insert(calendar);
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in createCalendarOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.Calendars.Update updateCalendarOperation(String executionGoogleUser, String calendarId, String summary) {
		
		try {
			com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
			calendar.setSummary(summary);
			
			return getCalendarGoogleService(executionGoogleUser).calendars().update(calendarId, calendar);
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in updateCalendar.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.Calendars.Delete deleteCalendarOperation(String executionGoogleUser, String calendarId) {
		
		try {
			return getCalendarGoogleService(executionGoogleUser).calendars().delete(calendarId);
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in deleteCalendarOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.Acl.List getCalendarAclOperation(String executionGoogleUser, String calendarId, String nextPageToken, String fields) {
		
		try {
			return getCalendarGoogleService(executionGoogleUser)
				.acl()
				.list(calendarId)
				.setFields(fields)
				.setPageToken(nextPageToken);
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in deleteCalendarOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.Acl.Insert addCalendarAclOperation(String executionGoogleUser, String calendarId, CalendarAclRoleEnum aclRole, CalendarAclScope aclScope, String scopeValue) {
		
		try {
			
			AclRule rule = new AclRule();
			rule.setRole(aclRole.getValue());
			Scope scope = new Scope();
			scope.setType(aclScope.getValue());
			scope.setValue(scopeValue);
			rule.setScope(scope);
			
			return getCalendarGoogleService(executionGoogleUser).acl().insert(calendarId, rule);
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in addCalendarAclOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.Acl.Update updateCalendarAclOperation(String executionGoogleUser, String calendarId, String ruleId, CalendarAclRoleEnum aclRole, CalendarAclScope aclScope, String scopeValue) {
		
		try {
			
			AclRule rule = new AclRule();
			rule.setRole(aclRole.getValue());
			Scope scope = new Scope();
			scope.setType(aclScope.getValue());
			scope.setValue(scopeValue);
			rule.setScope(scope);
			
			return getCalendarGoogleService(executionGoogleUser).acl().update(calendarId, ruleId, rule);
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in updateCalendarAclOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.CalendarList.List getCalendarListOperation(String executionGoogleUser, CalendarMinAccessRoleEnum minAccessRole, String pageToken) {
		
		try {
			
			return getCalendarGoogleService(executionGoogleUser).calendarList().list()
					.setMinAccessRole(minAccessRole != null ? minAccessRole.getValue() : null)
					.setPageToken(pageToken);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getCalendarGoogleService.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.CalendarList.Insert addCalendarToCalendarListOperation(String executionGoogleUser, String calendarId) {
		
		try {
			
			CalendarListEntry entry = new CalendarListEntry();
			entry.setId(calendarId);
			
			return getCalendarGoogleService(executionGoogleUser).calendarList().insert(entry);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getCalendarGoogleService.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.CalendarList.Delete removeCalendarFromCalendarListOperation(String executionGoogleUser, String calendarId) {
		
		try {
			
			return getCalendarGoogleService(executionGoogleUser).calendarList().delete(calendarId);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getCalendarGoogleService.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.Events.List getCalendarEventsOperation(String executionGoogleUser, String calendarId, String fullText, DateTime timeMin, DateTime timeMax, List<String> sharedExtendedProperty, List<String> privateExtendedProperty, Boolean singleEvents, Boolean showDeleted, String fields, String pageToken) {
		
		try {
			
			return getCalendarGoogleService(executionGoogleUser).events()
					.list(calendarId)
					.setQ(fullText)
					.setTimeMin(timeMin)
					.setTimeMax(timeMax)
					.setSharedExtendedProperty(sharedExtendedProperty)
					.setPrivateExtendedProperty(privateExtendedProperty)
					.setSingleEvents(singleEvents)
					.setShowDeleted(showDeleted)
					.setFields(fields)
					.setPageToken(pageToken);
					
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getCalendarEventsOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.Events.Get getEventOperation(String executionGoogleUser, String calendarId, String eventId, String fields) {
	
		try {

			return getCalendarGoogleService(executionGoogleUser)
					.events()
					.get(calendarId, eventId)
					.setFields(fields);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getEventOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.Events.Instances getEventIstancesOperation(String executionGoogleUser, String calendarId, String eventId) {
		
		try {

			return getCalendarGoogleService(executionGoogleUser).events().instances(calendarId, eventId);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in getEventIstancesOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.Events.Insert createEventOperation(String executionGoogleUser, String calendarId, String summary, EventDateTime start, EventDateTime end, List<EventAttendee> attendees, List<String> recurrences) {
	
		try {
			
			Event event = new Event();
			event.setSummary(summary);
			event.setStart(start);
			event.setEnd(end);
			event.setAttendees(attendees);
			event.setRecurrence(recurrences);

			return getCalendarGoogleService(executionGoogleUser).events().insert(calendarId, event);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in createEventOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.Events.Update updateEventOperation(String executionGoogleUser, String calendarId, String eventId, String summary, EventDateTime start, EventDateTime end, List<EventAttendee> attendees, List<String> recurrences) {
		
		try {
			
			Event event = new Event();
			event.setSummary(summary);
			event.setStart(start);
			event.setEnd(end);
			event.setAttendees(attendees);
			event.setRecurrence(recurrences);

			return getCalendarGoogleService(executionGoogleUser).events().update(calendarId, eventId, event);
			
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in updateEventOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	private com.google.api.services.calendar.Calendar.Events.Delete deleteEventOperation(String executionGoogleUser, String calendarId, String eventId) {
		
		try {
			return getCalendarGoogleService(executionGoogleUser)
				.events()
				.delete(calendarId, eventId);
		}  catch(Exception e) {
			getLogger().error("Calendar APIs - Critical error in deleteEventOperation.", e);
			throw new RuntimeException(e);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Batch builder
	
	public class CalendarBatchBuilder extends AbstractGoogleServiceBatch<Calendar> {
			
		public CalendarBatchBuilder(String executionGoogleUser, Calendar service) {
			super(executionGoogleUser, service);
		}
		
		public CalendarBatchBuilder(String executionGoogleUser, Calendar service, int operationsInBatch) {
			super(executionGoogleUser, service, operationsInBatch);
		}
		
		public CalendarBatchBuilder queueGetCalendarOperation(String calendarId, String fields) {
			
			queueOperation(getCalendarOperation(this.executionGoogleUser, calendarId, fields), new BatchCalendarCallback());
				
			return this;
		}
		
		public CalendarBatchBuilder queueCreateCalendarOperation(String summary) {
			
			queueOperation(createCalendarOperation(this.executionGoogleUser, summary), new BatchCalendarCallback());
			
			return this;
		}
		
		public CalendarBatchBuilder queueUpdateCalendarOperation(String calendarId, String summary) {
			
			queueOperation(updateCalendarOperation(this.executionGoogleUser, calendarId, summary), new BatchCalendarCallback());
			
			return this;
		}
		
		public CalendarBatchBuilder queueDeleteCalendarOperation(String calendarId) {
			
			queueVoidOperation(deleteCalendarOperation(this.executionGoogleUser, calendarId));
			
			return this;
		}
		
		public CalendarBatchBuilder queueGetCalendarAclOperation(String calendarId, String nextPageToken, String fields) {
			
			queueOperation(getCalendarAclOperation(executionGoogleUser, calendarId, nextPageToken, fields), new BatchAclCallback());
			
			return this;
		}
		
		public CalendarBatchBuilder queueAddCalendarAclOperation(String calendarId, CalendarAclRoleEnum aclRole, CalendarAclScope aclScope, String scopeValue) {
			
			queueOperation(addCalendarAclOperation(this.executionGoogleUser, calendarId, aclRole, aclScope, scopeValue), new BatchAclRuleCallback());
			
			return this;
		}
		
		public CalendarBatchBuilder queueUpdateCalendarAclOperation(String calendarId, String ruleId, CalendarAclRoleEnum aclRole, CalendarAclScope aclScope, String scopeValue) {
			
			queueOperation(updateCalendarAclOperation(this.executionGoogleUser, calendarId, ruleId, aclRole, aclScope, scopeValue), new BatchAclRuleCallback());
			
			return this;
		}
		
		public CalendarBatchBuilder queueGetCalendarListOperation(CalendarMinAccessRoleEnum minAccessRole, String pageToken) {
			
			queueOperation(getCalendarListOperation(this.executionGoogleUser, minAccessRole, pageToken), new BatchCalendarListCallback());
			
			return this;
		}
		
		public CalendarBatchBuilder queueAddCalendarToCalendarListOperation(String calendarId) {
			
			queueOperation(addCalendarToCalendarListOperation(this.executionGoogleUser, calendarId), new BatchCalendarListEntryCallback());
			
			return this;
		}
		
		public CalendarBatchBuilder queueRemoveCalendarFromCalendarListOperation(String calendarId) {
			
			queueVoidOperation(removeCalendarFromCalendarListOperation(this.executionGoogleUser, calendarId));
			
			return this;
		}
		
		public CalendarBatchBuilder queueGetCalendarEventsOperation(String calendarId, String fullText, DateTime timeMin, DateTime timeMax, List<String> sharedExtendedProperty, List<String> privateExtendedProperty, Boolean singleEvents, Boolean showDeleted, String fields, String pageToken) {
		
			queueOperation(getCalendarEventsOperation(pageToken, calendarId, fullText, timeMin, timeMax, sharedExtendedProperty, privateExtendedProperty, singleEvents, showDeleted, fields, pageToken), new BatchEventsCallback());
			
			return this;
		}
		
		public CalendarBatchBuilder queueGetEventOperation(String calendarId, String eventId, String fields) {
			
			queueOperation(getEventOperation(this.executionGoogleUser, calendarId, eventId, fields), new BatchEventCallback());
			
			return this;
		}
		
		public CalendarBatchBuilder queueGetEventIstancesOperation(String calendarId, String eventId) {

			queueOperation(getEventIstancesOperation(eventId, calendarId, eventId), new BatchEventsCallback());
			
			return this;
		}
		
		public CalendarBatchBuilder queueCreateEventOperation(String calendarId, String eventId, String summary, EventDateTime start, EventDateTime end, List<EventAttendee> attendees, List<String> recurrences) {
			
			queueOperation(createEventOperation(eventId, calendarId, summary, start, end, attendees, recurrences), new BatchEventCallback());
			
			return this;
		}
		
		public CalendarBatchBuilder queueUpdateEventOperation(String calendarId, String eventId, String summary, EventDateTime start, EventDateTime end, List<EventAttendee> attendees, List<String> recurrences) {
			queueOperation(updateEventOperation(summary, calendarId, eventId, summary, start, end, attendees, recurrences), new BatchEventCallback());
			
			return this;
		}
		
		public CalendarBatchBuilder queueDeleteEventOperation(String calendarId, String eventId) {
			
			queueVoidOperation(deleteEventOperation(this.executionGoogleUser, calendarId, eventId));
			
			return this;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Callback
	
	class BatchCalendarCallback extends BasicBatchCallBack<com.google.api.services.calendar.model.Calendar> {

		private com.google.api.services.calendar.model.Calendar result;
		
		public com.google.api.services.calendar.model.Calendar getResult() {
			return result;
		}

		@Override
		public com.google.api.services.calendar.model.Calendar onSuccessLogic(com.google.api.services.calendar.model.Calendar entity) {
			
			this.result = entity;
			
			return entity;
		}

		@Override
		public void onFailureLogic(GoogleJsonError e) {
			getLogger().error("Calendar APIs - Error in batch call.", e);
		}		
	}
	
	class BatchAclRuleCallback extends BasicBatchCallBack<AclRule> {

		private AclRule result;

		public AclRule getResult() {
			return result;
		}
		
		@Override
		public AclRule onSuccessLogic(AclRule entity) {
			
			this.result = entity;
			
			return entity;
		}

		@Override
		public void onFailureLogic(GoogleJsonError e) {
			getLogger().error("Calendar APIs - Error in batch call.", e);
		}
	}
	
	class BatchAclCallback extends BasicBatchCallBack<Acl> {

		private Acl result;

		public Acl getResult() {
			return result;
		}
		
		@Override
		public Acl onSuccessLogic(Acl entity) {
			
			this.result = entity;
			
			return entity;
		}

		@Override
		public void onFailureLogic(GoogleJsonError e) {
			getLogger().error("Calendar APIs - Error in batch call.", e);
		}
	}
	
	class BatchEventCallback extends BasicBatchCallBack<Event> {

		private Event result;

		public Event getResult() {
			return result;
		}
		
		@Override
		public Event onSuccessLogic(Event entity) {
			
			this.result = entity;
			
			return entity;
		}

		@Override
		public void onFailureLogic(GoogleJsonError e) {
			getLogger().error("Calendar APIs - Error in batch call.", e);
		}
	}
	
	class BatchEventsCallback extends BasicBatchCallBack<Events> {

		private Events result;

		public Events getResult() {
			return result;
		}
		
		@Override
		public Events onSuccessLogic(Events entity) {
			
			this.result = entity;
			
			return entity;
		}

		@Override
		public void onFailureLogic(GoogleJsonError e) {
			getLogger().error("Calendar APIs - Error in batch call.", e);
		}
	}
	
	class BatchCalendarListCallback extends BasicBatchCallBack<CalendarList> {

		private CalendarList result;

		public CalendarList getResult() {
			return result;
		}
		
		@Override
		public CalendarList onSuccessLogic(CalendarList entity) {
			
			this.result = entity;
			
			return entity;
		}

		@Override
		public void onFailureLogic(GoogleJsonError e) {
			getLogger().error("Calendar APIs - Error in batch call.", e);
		}
	}
	
	class BatchCalendarListEntryCallback extends BasicBatchCallBack<CalendarListEntry> {

		private CalendarListEntry result;

		public CalendarListEntry getResult() {
			return result;
		}
		
		@Override
		public CalendarListEntry onSuccessLogic(CalendarListEntry entity) {
			
			this.result = entity;
			
			return entity;
		}

		@Override
		public void onFailureLogic(GoogleJsonError e) {
			getLogger().error("Calendar APIs - Error in batch call.", e);
		}
	}
	
}
