from django.conf.urls import url
from . import views

app_name = 'android'

urlpatterns = [
    # localhost:8036/android/student_login/
    url(r'^student/login/$', views.student_login, name='student_login'),
    # localhost:8036/android/student/
    url(r'^student/password/change/$', views.student_password_change, name='student_password_change'),

]
