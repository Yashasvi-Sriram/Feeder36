from __future__ import unicode_literals

from django.db import models


class SpecialAdmin:
    email = "admin@a.b"
    password = "admin"


class Student(models.Model):
    name = models.CharField(max_length=50)
    user_name = models.EmailField(max_length=30)
    password = models.CharField(max_length=20)
    courses = models.ManyToManyField('Course')

    def __str__(self):
        return self.name


class Course(models.Model):
    code = models.CharField(max_length=10)
    name = models.CharField(max_length=50)
    students = models.ManyToManyField(Student)

    def __str__(self):
        return self.code + " - " + self.name


class Task(models.Model):
    tag = models.CharField(max_length=50)
    detail = models.CharField(max_length=400)
    deadline = models.CharField(max_length=20)
    course = models.ForeignKey(Course, on_delete=models.CASCADE, null=True)

    def __str__(self):
        return self.tag + "-" + self.deadline + " of " + self.course


class FeedBackForm(models.Model):
    name = models.CharField(max_length=50)
    question_set = models.TextField()
    deadline = models.CharField(max_length=20)
    course = models.ForeignKey(Course, on_delete=models.CASCADE, null=True)

    def __str__(self):
        return self.name + "-" + self.deadline + " of " + self.course


class FeedbackResponse(models.Model):
    answer_set = models.TextField()
    comment = models.TextField()
    student = models.ForeignKey(Student, on_delete=models.CASCADE, null=True)
    feedback_form = models.ForeignKey(FeedBackForm, on_delete=models.CASCADE, null=True)

    def __str__(self):
        return self.student.user_name + " response to " + self.feedback_form.name
