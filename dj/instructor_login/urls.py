from django.conf.urls import url
from . import views

app_name = 'instructor_login'

urlpatterns = [
    url(r'^login/$', views.login, name='login'),
    url(r'^auth/$', views.auth_view, name='auth_view'),
    # you need to modify this#add variable#restrict regex
    url(r'^loggedin/$', views.loggedin, name='loggedin'),
    url(r'^logout/$', views.logout, name='logout'),
    url(r'^invalid/$', views.invalid, name='invalid'),
    url(r'^register/$', views.register, name='register'),
    url(r'^register_success/$', views.register_success, name='register_success'),
    url(r'^register_failure/$', views.register_failure, name='register_failure'),
    url(r'^course/(?P<pk>[1-9][0-9]*)/tasks/$', views.tasks, name='tasks'),
    # localhost:8036/special_admin/course/<pk>/task/new/
    url(r'^course/(?P<pk>[1-9][0-9]*)/task/new/$', views.new_task, name='new_task'),
    # localhost:8036/special_admin/task/<task_pk>/
    url(r'^task/(?P<pk>[1-9][0-9]*)/$', views.old_task, name='old_task'),
    url(r'^task_redirect/$', views.tasks_redirect, name='tasks_redirect'),
    url(r'^courses/$', views.courses, name='courses'),

    url(r'^course/(?P<pk>[1-9][0-9]*)/feedback/forms/$', views.feedback_forms, name='feedback_forms'),
    # localhost:8036/special_admin/course/<pk>/feedback/form/new/
    url(r'^course/(?P<pk>[1-9][0-9]*)/feedback/form/new/$', views.new_feedback_form, name='new_feedback_form'),
    # localhost:8036/special_admin/feedback/form/<task_pk>/
    url(r'^feedback/form/(?P<pk>[1-9][0-9]*)/$', views.old_feedback_form, name='old_feedback_form'),

]
