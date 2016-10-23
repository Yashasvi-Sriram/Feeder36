from django.conf.urls import url
from . import views

app_name = 'special_admin'

urlpatterns = [
    # localhost:8036/special_admin/home/
    url(r'^home/$', views.home, name='home'),
    # localhost:8036/special_admin/students/
    url(r'^students/$', views.students, name='students'),
    # localhost:8036/special_admin/student/pk/
    url(r'^students/(?P<pk>[1-9][0-9]*)/$', views.old, name='old'),
    # localhost:8036/special_admin/student/new/
    url(r'^students/new/$', views.new, name='new'),
]
