package com.safnect.wallet.mpc.model.activity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Transient;

public class MarketingActivityConfig {

	@Id
	String id;
	
	Date beginDatetime, endDatetime;
	
	String img, sourceLink;

	@Transient
	Boolean started, winner;
	
	public Boolean getWinner() {
		return winner;
	}

	public void setWinner(Boolean winner) {
		this.winner = winner;
	}

	public String getSourceLink() {
		return sourceLink;
	}

	public void setSourceLink(String sourceLink) {
		this.sourceLink = sourceLink;
	}

	public void setStarted(Boolean started) {
		this.started = started;
	}

	public Boolean getStarted() {
		return started;
	}

	public void setStarted(Date now) {
		if (this.beginDatetime.before(now) && this.endDatetime.after(now)) {
			started = true;
		} else {
			this.started = false;
		}
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getBeginDatetime() {
		return beginDatetime;
	}

	public void setBeginDatetime(Date beginDatetime) {
		this.beginDatetime = beginDatetime;
	}

	public Date getEndDatetime() {
		return endDatetime;
	}

	public void setEndDatetime(Date endDatetime) {
		this.endDatetime = endDatetime;
	}
}
