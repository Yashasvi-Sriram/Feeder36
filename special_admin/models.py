from __future__ import unicode_literals

from django.db import models


class Super:
    email = "special_admin@feeder.com"
    password = "special_admin"


class Course(models.Model):
    code = models.CharField(max_length=10)
    name = models.CharField(max_length=100)

    def __str__(self):
        return self.code + "-" + self.name


class Student(models.Model):
    courses_registered = models.ManyToManyField(Course, related_name='courses')
    name = models.CharField(max_length=100)
    user_name = models.EmailField(max_length=50)
    password = models.Field(max_length=50)

    def __str__(self):
        return self.name
