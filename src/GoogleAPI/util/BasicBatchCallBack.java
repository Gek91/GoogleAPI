package GoogleAPI.util;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;

public class BasicBatchCallBack<T> extends JsonBatchCallback<T>{

	/*
	 * Logger
	 */
	private transient Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Logger getLogger() {
		return logger;
	}
	
	/*
	 * Fields
	 */
	private Collection<T> entities;
	
	/*
	 * Constructors
	 */
	public BasicBatchCallBack() { }
	
	public BasicBatchCallBack(Collection<T> entities) {
		this.entities = entities;
	}
	
	/*
	 * Methods
	 */
	//On success
	@Override
	public void onSuccess(T entity, HttpHeaders responseHeaders) throws IOException {
		
		if(entities != null) {
			entities.add(entity);
		}
	}

	//On failure
	@Override
	public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
		getLogger().error("GDrive APIs - Error Message in Batch operation : " + e.getMessage());
	}
	
	public Collection<T> getEntities() {
		return this.entities;
	}

}
