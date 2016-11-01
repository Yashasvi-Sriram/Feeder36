from django.conf.urls import url
from . import views

app_name = 'special_admin'

urlpatterns = [
    # localhost:8036/special_admin/home/
    url(r'^home/$', views.home, name='home'),
    # localhost:8036/special_admin/home/restart/
    url(r'^home/restart/$', views.restart, name='restart'),
    # localhost:8036/special_admin/home/tasks_redirect/
    url(r'^home/task_redirect/$', views.tasks_redirect, name='tasks_redirect'),
    # localhost:8036/special_admin/home/fb_forms_redirect/
    url(r'^home/fb_form_redirect/$', views.fb_forms_redirect, name='fb_forms_redirect'),

    # localhost:8036/special_admin/students/
    url(r'^students/$', views.students, name='students'),
    # localhost:8036/special_admin/student/<pk>/
    url(r'^student/(?P<pk>[1-9][0-9]*)/$', views.old_student, name='old_student'),
    # localhost:8036/special_admin/student/new/
    url(r'^students/new/$', views.new_student, name='new_student'),

    # localhost:8036/special_admin/courses/
    url(r'^courses/$', views.courses, name='courses'),
    # localhost:8036/special_admin/course/<pk>/
    url(r'^course/(?P<pk>[1-9][0-9]*)/$', views.old_course, name='old_course'),
    # localhost:8036/special_admin/course/new/
    url(r'^course/new/$', views.new_course, name='new_course'),

    # localhost:8036/special_admin/course/<pk>/tasks/
    url(r'^course/(?P<pk>[1-9][0-9]*)/tasks/$', views.tasks, name='tasks'),
    # localhost:8036/special_admin/course/<pk>/task/new/
    url(r'^course/(?P<pk>[1-9][0-9]*)/task/new/$', views.new_task, name='new_task'),
    # localhost:8036/special_admin/task/<task_pk>/
    url(r'^task/(?P<pk>[1-9][0-9]*)/$', views.old_task, name='old_task'),

    # localhost:8036/special_admin/course/<pk>/feedback/forms/
    url(r'^course/(?P<pk>[1-9][0-9]*)/feedback/forms/$', views.feedback_forms, name='feedback_forms'),
    # localhost:8036/special_admin/course/<pk>/feedback/form/new/
    url(r'^course/(?P<pk>[1-9][0-9]*)/feedback/form/new/$', views.new_feedback_form, name='new_feedback_form'),
    # localhost:8036/special_admin/feedback/form/<task_pk>/
    url(r'^feedback/form/(?P<pk>[1-9][0-9]*)/$', views.old_feedback_form, name='old_feedback_form'),

]
