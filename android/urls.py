from django.conf.urls import url
from . import views

app_name = 'android'

urlpatterns = [
    # localhost:8036/android/student/login/
    url(r'^student/login/$', views.student_login, name='student_login'),
    # localhost:8036/android/student/password_change/
    url(r'^student/password_change/$', views.student_password_change, name='student_password_change'),
    # localhost:8036/android/student/sync/
    url(r'^student/sync/$', views.student_sync, name='student_sync'),

]
