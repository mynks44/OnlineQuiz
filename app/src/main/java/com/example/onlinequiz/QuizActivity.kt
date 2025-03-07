package com.example.onlinequiz

import android.annotation.SuppressLint
import android.os.Bundle
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinequiz.databinding.ActivityQuizBinding
import com.example.onlinequiz.databinding.ScoreDialogBinding

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

        binding.apply {
            questionIndicatorTextview.text = "Question ${currentQuestionIndex + 1} / ${questionModelList.size}"
            questionProgressIndicator.progress =
                ((currentQuestionIndex.toFloat() / questionModelList.size.toFloat()) * 100).toInt()
            questionTextview.text = currentQuestion.question
            btn0.text = currentQuestion.options[0]
            btn1.text = currentQuestion.options[1]
            btn2.text = currentQuestion.options[2]
            btn3.text = currentQuestion.options[3]

            // Reset button colors
            btn0.setBackgroundColor(getColor(R.color.gray))
            btn1.setBackgroundColor(getColor(R.color.gray))
            btn2.setBackgroundColor(getColor(R.color.gray))
            btn3.setBackgroundColor(getColor(R.color.gray))
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

            // Store user answer
            currentQuestion.userAnswer = selectedAnswer

            // Check if answer is correct
            if (selectedAnswer == currentQuestion.correct) {
                score++
            } else {
                incorrectAnswers.add(currentQuestion.copy(userAnswer = selectedAnswer))
            }

            currentQuestionIndex++
            loadQuestions()
        } else {
            // Option button clicked
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

            finishBtn.setOnClickListener {
                showIncorrectAnswers()
            }
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()
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
}
