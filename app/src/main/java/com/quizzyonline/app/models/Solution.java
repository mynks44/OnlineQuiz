package com.quizzyonline.app.models;

import com.google.firebase.Timestamp;

public class Solution {
    private String solutionId;
    private String solutionText;
    private String studentName;
    private Timestamp timestamp;

    public Solution() {}

    public Solution(String solutionId, String solutionText, String studentName, Timestamp timestamp) {
        this.solutionId = solutionId;
        this.solutionText = solutionText;
        this.studentName = studentName;
        this.timestamp = timestamp;
    }

    public String getSolutionId() { return solutionId; }
    public String getSolutionText() { return solutionText; }
    public String getStudentName() { return studentName; }
    public Timestamp getTimestamp() { return timestamp; }
}
