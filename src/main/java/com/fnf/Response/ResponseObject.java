package com.fnf.Response;

public class ResponseObject {
	
	private StatusObject status;
	private ErrorObject error;

	public StatusObject getStatus() {
		return status;
	}

	public void setStatus(StatusObject status) {
		this.status = status;
	}

	public ErrorObject getError() {
		return error;
	}

	public void setError(ErrorObject error) {
		this.error = error;
	}
	


}
