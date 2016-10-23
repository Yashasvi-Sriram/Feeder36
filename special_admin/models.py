from __future__ import unicode_literals

from django.db import models


class SpecialAdmin:
    email = "admin@a.b"
    password = "admin"


class Student(models.Model):
    name = models.CharField(max_length=50)
    user_name = models.EmailField(max_length=30)
    password = models.CharField(max_length=20)
    courses = models.ForeignKey('Course', null=True, on_delete=models.SET_NULL)

    def __str__(self):
        return self.name


class Course(models.Model):
    code = models.CharField(max_length=10)
    name = models.CharField(max_length=50)
    students = models.ForeignKey(Student, null=True, on_delete=models.SET_NULL)

    def __str__(self):
        return self.code + " - " + self.name
