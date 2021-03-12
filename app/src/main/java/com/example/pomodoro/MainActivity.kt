  package com.example.pomodoro

import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.view.circulartimerview.CircularTimerView


  class MainActivity : AppCompatActivity() {
    var currentCountdown: CountDownTimer? = null
    var studyMins = 25
    var breakMins = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startTimer(view: View)
    {
        currentCountdown?.cancel()
        val progBar = findViewById<CircularTimerView>(R.id.progress_circular)
        val studyTimeMilli = studyMins * 60 * 1000
        val breakTimeMilli = breakMins * 60 * 1000

        progBar.setMaxValue(studyTimeMilli.toFloat())
        currentCountdown = PomodoroClockTimer(
            studyTimeMilli.toLong(),
            progBar,
            100,
            studyTimeMilli,
            breakTimeMilli,
            true,
            this
        )
        setToStudy()
        (currentCountdown as PomodoroClockTimer).start()
    }

      /**
       * Stops the clock and returns the circle visual
       * to a neutral state
       */
      fun stopTimer(view: View)
    {
        currentCountdown?.cancel()
        val progBar = findViewById<CircularTimerView>(R.id.progress_circular)
        progBar.progress = 0F
        progBar.setText("")
        progBar.setPrefix("Stopped")

    }

      /**
       * called at the transition between break and study states,
       * changes max value, color and text to the correct values
       */
    fun setToStudy()
    {
        var progbar = findViewById<CircularTimerView>(R.id.progress_circular)
        progbar.setPrefix("Time:")
        progbar.setMaxValue((studyMins * 60 * 1000).toFloat())
        progbar.setBackgroundColor("#e76130")

        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

      /**
       * does the same thing as setToStudy, except changes between study and break
       * states
       */
    fun setToBreak()
    {
        var progbar = findViewById<CircularTimerView>(R.id.progress_circular)
        progbar.setPrefix("Break:")
        progbar.setMaxValue((breakMins * 60 * 1000).toFloat())
        progbar.setBackgroundColor("#3242a8")

        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

      /**
       * These four methods are used by each increase/decrease
       * arrow to adjust the length of the break and study periods
        */
    fun incStudy(view: View)
    {
        var studyCount = findViewById<ImageView>(R.id.study_up)
        studyMins = studyMins + 1
        findViewById<TextView>(R.id.study_text).text = studyMins.toString()
    }

    fun decStudy(view: View)
    {
        if (studyMins > 0)
        {
            studyMins = studyMins - 1
            findViewById<TextView>(R.id.study_text).text = studyMins.toString()
        }
    }

    fun incBreak(view: View)
    {
        breakMins = breakMins + 1
        findViewById<TextView>(R.id.break_text).text = breakMins.toString()
    }

      fun decBreak(view: View)
      {
          if(breakMins > 0)
          {
              breakMins = breakMins - 1
              findViewById<TextView>(R.id.break_text).text = breakMins.toString()
          }
      }



}

class PomodoroClockTimer(
    millisInFuture: Long,
    targetBar: CircularTimerView,
    countDownInterval: Long,
    studyTime: Int,
    breakTime: Int,
    study: Boolean,
    main: MainActivity
)
    : CountDownTimer(millisInFuture, countDownInterval)
{
    var targetBar = targetBar
    var totalTime = millisInFuture
    var studyTime = studyTime
    var breakTime = breakTime
    var isStudy = study
    var root = main

    override fun onTick(millisUntilFinished: Long) {
        var mins = (millisUntilFinished / 1000)/60
        var secs = (millisUntilFinished / 1000)%60
        var time: String = mins.toString()+":"+secs.toString()
        targetBar.progress = millisUntilFinished.toFloat()
        targetBar.setText(time)
    }

    override fun onFinish() {
        if (isStudy)
        {
            println("ababababababa")
            root.currentCountdown = PomodoroClockTimer(
                breakTime.toLong(),
                targetBar,
                50,
                studyTime,
                breakTime,
                false,
                root
            )
            this.cancel()
            (root.currentCountdown as PomodoroClockTimer).start()
            root.setToBreak()
        }
        else
        {
            println("hello world")
            root.currentCountdown = PomodoroClockTimer(
                studyTime.toLong(),
                targetBar,
                50,
                studyTime,
                breakTime,
                true,
                root
            )
            (root.currentCountdown as PomodoroClockTimer).start()
            this.cancel()
            root.setToStudy()
        }
    }

}