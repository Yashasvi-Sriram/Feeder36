# -*- coding: utf-8 -*-
# Generated by Django 1.10 on 2016-10-22 14:47
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('special_admin', '0005_student_courses'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='student',
            name='courses',
        ),
        migrations.AddField(
            model_name='course',
            name='students',
            field=models.ForeignKey(null=True, on_delete=django.db.models.deletion.SET_NULL, to='special_admin.Student'),
        ),
    ]
