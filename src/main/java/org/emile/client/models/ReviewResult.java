package org.emile.client.models;

public class ReviewResult {
	
	private String PID;
	private String model;
	private String filename;
	private String validation;
	private String remark;
	
	public void set(String result) {	
		
		String[] values = result.split("[|]");
		setModel(values[0]);
		setValidation(values[1]);
		setPID(values[2]);
		setFilename(values[3]);
		setRemark(new String());
	}
	
	/**
	 * @return the pID
	 */
	public String getPID() {
		return PID;
	}
	/**
	 * @param pID the pID to set
	 */
	public void setPID(String pID) {
		PID = pID;
	}
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return the validation
	 */
	public String getValidation() {
		return validation;
	}
	/**
	 * @param validation the validation to set
	 */
	public void setValidation(String validation) {
		this.validation = validation;
	}
	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	
}
