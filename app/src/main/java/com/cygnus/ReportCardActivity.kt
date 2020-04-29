package com.cygnus

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ArrayAdapter
import co.aspirasoft.view.NestedListView
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.TestsDao
import com.cygnus.model.Student
import com.cygnus.model.Test
import com.cygnus.model.User
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.activity_report_card.*

/**
 * TestsActivity displays names of all tests of a subject
 *
 * Purpose of this activity is to show a list of all graded tests
 * in a subject, and allow teachers to create and update test
 * grades.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class ReportCardActivity : DashboardChildActivity() {

    private lateinit var subjects: ArrayList<String>
    private lateinit var classId: String

    private val tests = HashMap<String, ArrayList<Test>>()
    private val scores = HashMap<String, ArrayList<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_card)

        subjects = intent.getStringArrayListExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) ?: return finish()
        classId = intent.getStringExtra(CygnusApp.EXTRA_STUDENT_CLASS_ID) ?: return finish()
    }

    override fun updateUI(currentUser: User) {
        if (currentUser is Student) {
            for (subject in subjects) {
                TestsDao.getTestsBySubject(schoolId, subject, classId, OnSuccessListener { testsList ->
                    val exams = this.tests.getOrDefault(subject, ArrayList())
                    exams.clear()
                    testsList?.let { savedTests ->
                        savedTests.forEach { test ->
                            if (test.name.contains("exam", true)) exams.add(test)
                        }
                    }
                    this.tests[subject] = exams

                    val scores = this.scores.getOrDefault(subject, ArrayList())
                    scores.clear()
                    exams.forEach { exam ->
                        exam.obtainedScore.find { it.studentRollNo == currentUser.rollNo }?.let { score ->
                            val totalMarks = exam.maxMarksTheory + exam.maxMarksPractical
                            val obtainedMarks = score.theoryMarks + score.practicalMarks
                            val percentage = 100F * obtainedMarks / totalMarks
                            scores.add(String.format("%s: %d/%d (%.1f%%)", exam.name, obtainedMarks, totalMarks, percentage))
                        }
                    }
                    this.scores[subject] = scores

                    contentList.addView(MaterialTextView(this).apply {
                        text = subject
                        setTextAppearance(R.style.TextAppearance_MaterialComponents_Button)
                        setTypeface(null, Typeface.BOLD)
                        setPadding(16, 0, 0, 0)
                    })
                    contentList.addView(NestedListView(this).apply {
                        this.adapter = TestAdapter(context, scores)
                    })
                })
            }
        }
    }

    private inner class TestAdapter(context: Context, val grades: ArrayList<String>)
        : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, grades)

}