package main.GoogleAPI.common;

import java.io.IOException;

import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.json.GenericJson;

import main.GoogleAPI.common.AbstractGoogleServiceBatchRequest.ErrorsCheck;

public abstract class BasicBatchCallBack<T> extends JsonBatchCallback<T>{
	
	/*
	 * Fields
	 */
	private ErrorsCheck errorsCheck;
	
	/*
	 * Constructor
	 */
	
	public BasicBatchCallBack() {

	}
	
	public BasicBatchCallBack(ErrorsCheck errorsCheck) {
		this.errorsCheck = errorsCheck;
	}
	
	/*
	 * Abstract Methods
	 */
	public abstract T onSuccessLogic(T entity);
	public abstract void onFailureLogic(GoogleJsonError e);
	
	/*
	 * Methods
	 */
	
	public void setErrorsCheck(ErrorsCheck errorsCheck) {
		this.errorsCheck = errorsCheck;
	}
	
	//On success
	@Override
	public void onSuccess(T entity, HttpHeaders responseHeaders) throws IOException {
		onSuccessLogic(entity);
	}

	//On failure
	@Override
	public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
		onFailureLogic(e);
		
		if(errorsCheck != null) {
			errorsCheck.setHasError(errorsCheck.isHasError() && true);
			errorsCheck.getErrors().add(e);
		}
	}

}
