package com.quizzyonline.app.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.quizzyonline.app.QuestionModel
import com.example.onlinequiz.R
import com.example.onlinequiz.databinding.ActivityQuizBinding
import com.example.onlinequiz.databinding.ScoreDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class QuizActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        var questionModelList: List<QuestionModel> = listOf()
        var time: String = ""
    }

    private lateinit var binding: ActivityQuizBinding
    private var currentQuestionIndex = 0
    private var selectedAnswer = ""
    private var score = 0
    private val incorrectAnswers = mutableListOf<QuestionModel>() // Store incorrect answers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btn0.setOnClickListener(this@QuizActivity)
            btn1.setOnClickListener(this@QuizActivity)
            btn2.setOnClickListener(this@QuizActivity)
            btn3.setOnClickListener(this@QuizActivity)
            nextBtn.setOnClickListener(this@QuizActivity)
        }

        loadQuestions()
        startTimer()
    }

    private fun startTimer() {
        val totalTimeInMillis = time.toInt() * 60 * 1000L
        object : CountDownTimer(totalTimeInMillis, 1000L) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text = String.format("%02d:%02d", minutes, remainingSeconds)
            }

            override fun onFinish() {
                finishQuiz()
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun loadQuestions() {
        selectedAnswer = ""

        if (currentQuestionIndex >= questionModelList.size) {
            finishQuiz()
            return
        }

        val currentQuestion = questionModelList[currentQuestionIndex]
        val options = currentQuestion.options

        binding.apply {
            questionIndicatorTextview.text = "Question ${currentQuestionIndex + 1} / ${questionModelList.size}"
            questionProgressIndicator.progress =
                ((currentQuestionIndex.toFloat() / questionModelList.size.toFloat()) * 100).toInt()
            questionTextview.text = currentQuestion.question

            val buttons = listOf(btn0, btn1, btn2, btn3)
            for (i in buttons.indices) {
                if (i < options.size) {
                    buttons[i].visibility = View.VISIBLE
                    buttons[i].text = options[i]
                    buttons[i].setBackgroundColor(getColor(R.color.gray))
                } else {
                    buttons[i].visibility = View.GONE
                }
            }
        }
    }



    override fun onClick(view: View?) {
        val clickedBtn = view as Button

        if (clickedBtn.id == R.id.next_btn) {
            if (selectedAnswer.isEmpty()) {
                Toast.makeText(this, "Please select an answer to continue", Toast.LENGTH_SHORT).show()
                return
            }

            val currentQuestion = questionModelList[currentQuestionIndex]

            // Store answer
            currentQuestion.userAnswer = selectedAnswer

            if (selectedAnswer == currentQuestion.correct) {
                score++
            } else {
                incorrectAnswers.add(currentQuestion.copy(userAnswer = selectedAnswer))
            }

            currentQuestionIndex++
            loadQuestions()
        } else {
            selectedAnswer = clickedBtn.text.toString()
            highlightSelectedOption(clickedBtn)
        }
    }

    private fun highlightSelectedOption(selectedButton: Button) {
        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.gray))
            btn1.setBackgroundColor(getColor(R.color.gray))
            btn2.setBackgroundColor(getColor(R.color.gray))
            btn3.setBackgroundColor(getColor(R.color.gray))
        }
        selectedButton.setBackgroundColor(getColor(R.color.orange))
    }

    @SuppressLint("SetTextI18n")
    private fun finishQuiz() {
        val totalQuestions = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()

        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            scoreProgressText.text = "$percentage%"
            scoreTitle.text = if (percentage > 60) "Congrats! You have passed" else "Oops! You have failed"
            scoreTitle.setTextColor(if (percentage > 60) Color.BLUE else Color.RED)
            scoreSubtitle.text = "$score out of $totalQuestions are correct"
        }

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.finishBtn.text = "Finish"
        dialogBinding.finishBtn.setOnClickListener {
            alertDialog.dismiss()
            saveQuizResult()  // Save the quiz result before finishing
            finishQuizAndGoHome()
        }

        dialogBinding.repeatBtn.visibility = View.VISIBLE
        dialogBinding.repeatBtn.setOnClickListener {
            alertDialog.dismiss()
            restartQuiz()
        }

        dialogBinding.reviewBtn.visibility = View.VISIBLE
        dialogBinding.reviewBtn.setOnClickListener {
            alertDialog.dismiss()
            showIncorrectAnswers()
        }

        alertDialog.show()
    }

    private fun saveQuizResult() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().reference.child("History").child(userId)

        val totalQuestions = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()

        val attemptData = mapOf(
            "quizName" to intent.getStringExtra("quizName"),
            "scorePercentage" to percentage,
            "correctAnswers" to score,
            "wrongAnswers" to incorrectAnswers.size,
            "attemptNumber" to System.currentTimeMillis(),
            "incorrectQuestions" to incorrectAnswers.map { question ->
                mapOf(
                    "question" to question.question,
                    ("yourAnswer" to question.userAnswer ?: "Not Answered") as Pair<Any, Any>,
                    "correctAnswer" to question.correct
                )
            }
        )

        databaseReference.push().setValue(attemptData).addOnSuccessListener {
            Toast.makeText(this, "History Saved!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to Save History", Toast.LENGTH_SHORT).show()
        }
    }

    private fun finishQuizAndGoHome() {
        finish()
    }

    private fun restartQuiz() {
        currentQuestionIndex = 0
        selectedAnswer = ""
        score = 0
        incorrectAnswers.clear()

        loadQuestions()
        startTimer()
    }

    private fun showIncorrectAnswers() {
        if (incorrectAnswers.isEmpty()) {
            finish()
            return
        }

        val incorrectAnswerText = buildString {
            append("Incorrect Answers:\n\n")
            incorrectAnswers.forEach { question ->
                append("Q: ${question.question}\n")
                append("Your Answer: ${question.userAnswer ?: "Not Answered"}\n")
                append("Correct Answer: ${question.correct}\n\n")
            }

        }

        AlertDialog.Builder(this)
            .setTitle("Review Incorrect Answers")
            .setMessage(incorrectAnswerText)
            .setPositiveButton("OK") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

}
