package org.agfjord.domain;

import java.util.List;

public class Result {

	String err;
	List<Linearlization> ls;

	public String getErr() {
		return err;
	}

	public void setErr(String err) {
		this.err = err;
	}

	public List<Linearlization> getLs() {
		return ls;
	}

	public void setLs(List<Linearlization> ls) {
		this.ls = ls;
	}
	

}
