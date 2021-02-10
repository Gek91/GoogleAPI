package main.GoogleAPI.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.json.GenericJson;

public abstract class AbstractGoogleServiceBatch<T extends AbstractGoogleJsonClient> {

	private transient Logger logger = LoggerFactory.getLogger(this.getClass());

	protected Logger getLogger() {
		return logger;
	}
	
	/*
	 * Private Fields
	 */
	private T service;
	private int operationsInBatch;
	private ErrorsCheck errorsCheck;

	/*
	 * Protected Fields
	 */
	protected String executionGoogleUser;
	protected List<BatchOperation<?>> operations; 
	
	/*
	 * Constructors
	 */
	public AbstractGoogleServiceBatch(String executionGoogleUser, T service, int operationInBatch) {
		
		this.service = service;
		this.executionGoogleUser = executionGoogleUser;
		operations = new ArrayList<>();
		this.operationsInBatch = operationInBatch;
		this.errorsCheck = new ErrorsCheck();
	}
	
	
	public AbstractGoogleServiceBatch(String executionGoogleUser, T service) {
		
		this(executionGoogleUser, service, 50);
	}

	/*
	 * Public methods
	 */
	
	public <Z extends GenericJson> void queueOperation(AbstractGoogleJsonClientRequest<Z> request, BasicBatchCallBack<Z> callback) {
		
		this.operations.add(new BatchOperation<Z>(request, callback));
		
		callback.setErrorsCheck(this.errorsCheck);
	}
	
	public void queueVoidOperation(AbstractGoogleJsonClientRequest<Void> request) {
		
		BasicBatchCallBack<Void> callback = new BatchVoidCallback();
		
		this.operations.add(new BatchOperation<Void>(request, callback));
		
		callback.setErrorsCheck(this.errorsCheck);

	}
	
	public List<GoogleJsonError> getErrors() {
		return errorsCheck.getErrors();
	}
	
	public boolean executionOperationsInBatch() {
		return executionOperationsInBatch(null);
	}
	
	public boolean executionOperationsInBatch(Long batchExecutionsMillisecondsDelay) {
				
		if(operations == null || operations.isEmpty()) {
			return false;
		}
		
		try {
			
			getLogger().info("Google API - Executing operations in batch...");
						
			List<List<BatchOperation<?>>> partitions = ListUtils.partition(operations, this.operationsInBatch);

			int i = 1;
			for(List<BatchOperation<?>> partition : partitions) {
			
				BatchRequest batchRequest = this.service.batch();

				for(BatchOperation<?> operation : partition) {
					if(operation != null && operation.getCallback() != null) {
						queueOp(operation, batchRequest);
					}
				}
				
				getLogger().info("Google API - Executing {}° batch request", (i)+"");
				batchRequest.execute();
				i++;
				
				intraExecutionDelay(batchExecutionsMillisecondsDelay);
			}			
			
			getLogger().info("Google API - Batch operations executed.");
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return errorsCheck.isHasError();
	}
	
	private void intraExecutionDelay(Long batchExecutionsMillisecondsDelay) throws InterruptedException {
		if(batchExecutionsMillisecondsDelay != null) {
				Thread.sleep(batchExecutionsMillisecondsDelay);
		}
	}
	
	private <K> void queueOp(BatchOperation<K> operation, BatchRequest request) throws IOException {
		operation.getRequest().queue(request, operation.getCallback());
	}
	
	/*
	 * Inner Class Operation
	 */
	public static class BatchOperation<T> {
		
		private AbstractGoogleJsonClientRequest<T> request;
		private BasicBatchCallBack<T> callback;
		
		public BatchOperation(AbstractGoogleJsonClientRequest<T> request, BasicBatchCallBack<T> callback) {
			this.request = request;
			this.callback = callback;
		}

		
		public AbstractGoogleJsonClientRequest<T> getRequest() {
			return request;
		}

		public BasicBatchCallBack<T> getCallback() {
			return callback;
		}
	}
	
	
	/*
	 * Inner class errors
	 */
	public static class ErrorsCheck {
		
		private boolean hasError;
		private List<GoogleJsonError> errors;
		
		public ErrorsCheck() {
			this.errors = new ArrayList<>();
		}
		
		public boolean isHasError() {
			return hasError;
		}
		public void setHasError(boolean hasError) {
			this.hasError = hasError;
		}
		public List<GoogleJsonError> getErrors() {
			return errors;
		}
		public void setErrors(List<GoogleJsonError> errors) {
			this.errors = errors;
		}
	}
	
	/*
	 * Void callback
	 */

	class BatchVoidCallback extends BasicBatchCallBack<Void> {

		@Override
		public Void onSuccessLogic(Void entity) {
			return null;
		}

		@Override
		public void onFailureLogic(GoogleJsonError e) {
			getLogger().error("Calendar APIs - Error in batch call.", e);
		}
		
	}
}



