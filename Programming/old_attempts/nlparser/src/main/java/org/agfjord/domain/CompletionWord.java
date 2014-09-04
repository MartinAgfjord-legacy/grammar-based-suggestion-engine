package org.agfjord.domain;

public class CompletionWord {
	boolean partial;
	String word;
	
	public CompletionWord(boolean partial, String word){
		this.partial = partial;
		this.word = word;
	}

	public boolean isPartial() {
		return partial;
	}

	public void setPartial(boolean partial) {
		this.partial = partial;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
	
	@Override
	public int hashCode() {
		return word.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		CompletionWord completionWord = (CompletionWord) obj;
		return word.equals(completionWord.getWord());
	}
	
}
