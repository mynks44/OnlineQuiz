package com.quizzyonline.app.models;

public class Discussion {
    private String question;
    private String answer;
    private long timestamp;
    private boolean isPermanent;

    public Discussion() {
    }

    public Discussion(String question, String answer, long timestamp, boolean isPermanent) {
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
        this.isPermanent = isPermanent;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public void setPermanent(boolean permanent) {
        isPermanent = permanent;
    }
}
